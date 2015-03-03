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
     * Delays onNext, onError and onComplete delivery unless a view becomes available.
     * Duplicates latest onNext emission in case if a view has been reattached.
     * getView() is guaranteed to be != null during successive calls.
     * If onNext it called by source observable while previous undelivered result exists,
     * the older result will be dropped.
     *
     * @param <T> a type of onNext data
     */
    public class DeliverLatest<T> implements Observable.Operator<T, T> {
        @Override
        public Subscriber<? super T> call(final Subscriber<? super T> s) {
            return new Subscriber<T>() {

                Action0 deliver = new Action0() {
                    @Override
                    public void call() {
                        if (!s.isUnsubscribed() && getView() != null) {
                            if (deliverLatest)
                                s.onNext(latest);
                            if (deliverError) {
                                s.onError(error);
                                error = null;
                                deliverError = false;
                            }
                            if (deliverCompleted) {
                                s.onCompleted();
                                deliverCompleted = false;
                            }
                        }
                    }
                };

                T latest;
                boolean deliverLatest;
                Throwable error;
                boolean deliverError;
                boolean deliverCompleted;

                {
                    RxPresenter.this.callOnTakeView.add(deliver);
                    add(Subscriptions.create(new Action0() {
                        @Override
                        public void call() {
                            RxPresenter.this.callOnTakeView.remove(deliver);
                        }
                    }));
                }

                @Override
                public void onCompleted() {
                    deliverCompleted = true;
                    deliver.call();
                }

                @Override
                public void onError(Throwable e) {
                    error = e;
                    deliverError = true;
                    deliver.call();
                }

                @Override
                public void onNext(T t) {
                    latest = t;
                    deliverLatest = true;
                    deliver.call();
                }
            };
        }
    }
}
