package nucleus.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func0;
import rx.subscriptions.Subscriptions;

/**
 * This is an extension of {@link nucleus.presenter.Presenter} which provides RxJava functionality.
 *
 * @param <ViewType> a type of view
 */
public class RxPresenter<ViewType> extends Presenter<ViewType> {

    private static final String REQUESTED_QUERIES = RxPresenter.class.getName() + "#requestedQueries";

    private ArrayList<Integer> requestedQueries = new ArrayList<>();
    private HashMap<Integer, Func0<Subscription>> queryFactories = new HashMap<>();
    private HashMap<Integer, Subscription> querySubscriptions = new HashMap<>();

    private CopyOnWriteArrayList<Action0> callOnTakeView = new CopyOnWriteArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedState) {
        if (savedState != null)
            requestedQueries = savedState.getIntegerArrayList(REQUESTED_QUERIES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Subscription subs : querySubscriptions.values())
            subs.unsubscribe();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSave(@NonNull Bundle state) {
        super.onSave(state);
        for (int i = requestedQueries.size() - 1; i >= 0; i--) {
            Integer queryId = requestedQueries.get(i);
            if (querySubscriptions.get(queryId).isUnsubscribed()) {
                requestedQueries.remove(i);
                querySubscriptions.remove(queryId);
            }
        }
        state.putIntegerArrayList(REQUESTED_QUERIES, requestedQueries);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onTakeView(ViewType view) {
        super.onTakeView(view);
        for (Action0 action : callOnTakeView)
            action.call();
    }

    /**
     * Registers a query factory. Runs the query if it has been requested earlier (during previous instance
     * of the presenter).
     *
     * @param queryId id of a query
     * @param factory a factory that will make an actual query when requested.
     */
    public void registerQuery(int queryId, Func0<Subscription> factory) {
        queryFactories.put(queryId, factory);
        if (requestedQueries.contains(queryId))
            querySubscriptions.put(queryId, queryFactories.get(queryId).call());
    }

    /**
     * Subscribes (runs) a query using a factory method provided with {@link #registerQuery}.
     * If a presented gets destroyed during a temporary process shutdown while query is still
     * subscribed, the query will be restarted on next {@link #registerQuery} call.
     *
     * @param queryId id of a query to subscribe
     */
    public void subscribeQuery(int queryId) {
        unsubscribeQuery(queryId);
        requestedQueries.add(queryId);
        querySubscriptions.put(queryId, queryFactories.get(queryId).call());
    }

    /**
     * Unsubscribes a query
     *
     * @param queryId id of a query to unsubscribe
     */
    public void unsubscribeQuery(int queryId) {
        if (querySubscriptions.containsKey(queryId)) {
            querySubscriptions.get(queryId).unsubscribe();
            querySubscriptions.remove(queryId);
        }
        requestedQueries.remove((Integer)queryId);
    }

    /**
     * This operator will delay onNext, onError and onComplete emissions unless a view become available.
     * getView() is guaranteed to be != null during emissions. This operator can only be used on Application's main thread.
     * <p/>
     * Use this operator if you need o deliver all emissions to a view, in example when you're sending items
     * into adapter.
     *
     * @param <T> a type of onNext value.
     * @return the delaying operator.
     */
    public class Deliver<T> extends DeliverOperator<T> {
        public Deliver() {
            super(false, false);
        }
    }

    /**
     * This operator will delay onNext, onError and onComplete emissions unless a view become available.
     * getView() is guaranteed to be != null during emissions. This operator can only be used on Application's main thread.
     * <p/>
     * If this operator receives a next value while the previous value has not been delivered, the
     * previous value will be dropped.
     * <p/>
     * Use this operator when you need to show updatable data.
     *
     * @param <T> a type of onNext value.
     * @return the delaying operator.
     */
    public class DeliverLatest<T> extends DeliverOperator<T> {
        public DeliverLatest() {
            super(true, false);
        }
    }

    /**
     * This operator will delay onNext, onError and onComplete emissions unless a view become available.
     * getView() is guaranteed to be != null during emissions. This operator can only be used on Application's main thread.
     * <p/>
     * The operator will duplicate the latest onNext emission in case if a view has been reattached.
     * <p/>
     * If the operator receives a next value while the previous value has not been delivered, the
     * previous value will be dropped.
     * <p/>
     * This operator ignores the onComplete emission.
     * <p/>
     * Use this operator when you need to chow updatable data that needs to be cached in memory.
     *
     * @param <T> a type of onNext value.
     * @return the delaying operator.
     */
    public class DeliverLatestCache<T> extends DeliverOperator<T> {
        public DeliverLatestCache() {
            super(true, true);
        }
    }

    /**
     * @hide testing facility
     */
    public int onTakeViewListenerCount() {
        return callOnTakeView.size();
    }

    private class DeliverOperator<T> implements Observable.Operator<T, T> {

        boolean keepLatestOnly;
        boolean replayLatest;

        private DeliverOperator(boolean keepLatestOnly, boolean replayLatest) {
            this.keepLatestOnly = keepLatestOnly;
            this.replayLatest = replayLatest;
        }

        @Override
        public Subscriber<? super T> call(final Subscriber<? super T> s) {
            return new Subscriber<T>() {

                Action0 tick = new Action0() {
                    @Override
                    public void call() {
                        if (!s.isUnsubscribed() && getView() != null) {

                            while (deliverNext.size() > (replayLatest ? 1 : 0))
                                s.onNext(deliverNext.remove(0));

                            if (deliverNext.size() > 0)
                                s.onNext(deliverNext.get(0));

                            if (deliverError) {
                                deliverNext.clear();
                                s.onError(error);
                                s.unsubscribe();
                                error = null;
                                deliverError = false;
                            }
                            if (deliverCompleted && !replayLatest) {
                                deliverNext.clear();
                                s.onCompleted();
                                s.unsubscribe();
                                deliverCompleted = false;
                            }
                        }
                    }
                };

                ArrayList<T> deliverNext = new ArrayList<>();
                Throwable error;
                boolean deliverError;
                boolean deliverCompleted;

                @Override
                public void onStart() {
                    super.onStart();
                    RxPresenter.this.callOnTakeView.add(tick);
                    s.add(Subscriptions.create(new Action0() {
                        @Override
                        public void call() {
                            RxPresenter.this.callOnTakeView.remove(tick);
                        }
                    }));
                }

                @Override
                public void onCompleted() {
                    deliverCompleted = true;
                    tick.call();
                }

                @Override
                public void onError(Throwable throwable) {
                    error = throwable;
                    deliverError = true;
                    tick.call();
                }

                @Override
                public void onNext(T value) {
                    if (keepLatestOnly)
                        deliverNext.clear();
                    deliverNext.add(value);
                    tick.call();
                }
            };
        }
    }
}
