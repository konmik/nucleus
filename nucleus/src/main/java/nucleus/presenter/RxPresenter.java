package nucleus.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func0;
import rx.subjects.BehaviorSubject;

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

    private BehaviorSubject<Boolean> viewStatusSubject = BehaviorSubject.create();

    /**
     * Returns an observable that emits current status of view.
     * True - a view is attached, False - a view is detached.
     *
     * @return an observable that emits current status of view.
     */
    public Observable<Boolean> viewStatus() {
        return viewStatusSubject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedState) {
        if (savedState != null)
            requestedQueries = savedState.getIntegerArrayList(REQUESTED_QUERIES);
        viewStatusSubject.onNext(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Subscription subs : querySubscriptions.values())
            subs.unsubscribe();
        viewStatusSubject.onCompleted();
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
        viewStatusSubject.onNext(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDropView() {
        super.onDropView();
        viewStatusSubject.onNext(false);
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
     * Returns a transformer that will delay onNext, onError and onComplete emissions unless a view become available.
     * getView() is guaranteed to be != null during emissions. This transformer can only be used on Application's main thread.
     * <p/>
     * Use this operator if you need to deliver all emissions to a view, in example when you're sending items
     * into adapter one by one.
     *
     * @param <T> a type of onNext value.
     * @return the delaying operator.
     */
    public <T> Observable.Transformer<T, T> deliver() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.lift(OperatorSemaphore.<T>semaphore(viewStatus()));
            }
        };
    }

    /**
     * Returns a transformer that will delay onNext, onError and onComplete emissions unless a view become available.
     * getView() is guaranteed to be != null during emissions. This transformer can only be used on Application's main thread.
     * <p/>
     * If this transformer receives a next value while the previous value has not been delivered, the
     * previous value will be dropped.
     * <p/>
     * Use this operator when you need to show updatable data.
     *
     * @param <T> a type of onNext value.
     * @return the delaying operator.
     */
    public <T> Observable.Transformer<T, T> deliverLatest() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.lift(OperatorSemaphore.<T>semaphoreLatest(viewStatus()));
            }
        };
    }

    /**
     * Returns a transformer that will delay onNext, onError and onComplete emissions unless a view become available.
     * getView() is guaranteed to be != null during emissions. This transformer can only be used on Application's main thread.
     * <p/>
     * If the transformer receives a next value while the previous value has not been delivered, the
     * previous value will be dropped.
     * <p/>
     * The transformer will duplicate the latest onNext emission in case if a view has been reattached.
     * <p/>
     * This operator ignores the onComplete emission and never sends one.
     * <p/>
     * Use this operator when you need to show updatable data that needs to be cached in memory.
     *
     * @param <T> a type of onNext value.
     * @return the delaying operator.
     */
    public <T> Observable.Transformer<T, T> deliverLatestCache() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.lift(OperatorSemaphore.<T>semaphoreLatestCache(viewStatus()));
            }
        };
    }

    /**
     * @hide testing facility
     */
    public boolean viewStatusHasObservers() {
        return viewStatusSubject.hasObservers();
    }
}
