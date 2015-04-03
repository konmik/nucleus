package nucleus.presenter;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * This operator delays onNext, onComplete and onError emissions until a True value received from a given observable.
 * When the given observable emits False, the operator starts delaying emissions again.
 * <p/>
 * semaphoreLatest variant drops older not emitted onNext value if a new value has been received.
 * <p/>
 * semaphoreLatestCache keeps the latest value after emission and sends it on each True value
 * from a given observable received. This variant never emits onCompleted.
 *
 * @param <T> a type of onNext value
 */
public class OperatorSemaphore<T> implements Observable.Operator<T, T> {

    private Observable<Boolean> go;
    private boolean latest;
    private boolean cache;

    /**
     * Returns an operator that delays onNext, onComplete and onError emissions until a True value received from a given observable.
     * When the given observable emits False, the operator starts delaying emissions again.
     *
     * @param go  an operator that controls emission.
     * @param <T> a type of onNext value.
     * @return an operator that delays onNext, onComplete and onError emissions until a True value received from a given observable.
     * When the given observable emits False, the operator starts delaying emissions again.
     */
    public static <T> OperatorSemaphore<T> semaphore(Observable<Boolean> go) {
        return new OperatorSemaphore<>(go);
    }

    /**
     * Returns an operator that delays onNext, onComplete and onError emissions until a True value received from a given observable.
     * When the given observable emits False, the operator starts delaying emissions again.
     * <p/>
     * This variant drops older not emitted value if a new value has been received.
     *
     * @param go  an operator that controls emission.
     * @param <T> a type of onNext value.
     * @return an operator that delays onNext, onComplete and onError emissions until a True value received from a given observable.
     * When the given observable emits False, the operator starts delaying emissions again.
     * <p/>
     * This variant drops older not emitted value if a new value has been received.
     */
    public static <T> OperatorSemaphore<T> semaphoreLatest(Observable<Boolean> go) {
        return new OperatorSemaphore<>(go, true);
    }

    /**
     * Returns an operator that delays onNext, onComplete and onError emissions until a True value received from a given observable.
     * When the given observable emits False, the operator starts delaying emissions again.
     * <p/>
     * This variant drops older not emitted value if a new value has been received.
     * <p/>
     * It also keeps the latest value after emission and sends it on each True value
     * from a given observable received. This variant never emits onCompleted.
     *
     * @param go  an operator that controls emission.
     * @param <T> a type of onNext value.
     * @return an operator that delays onNext, onComplete and onError emissions until a True value received from a given observable.
     * When the given observable emits False, the operator starts delaying emissions again.
     * <p/>
     * This variant drops older not emitted value if a new value has been received.
     * <p/>
     * It also keeps the latest value after emission and sends it on each True value
     * from a given observable received. This variant never emits onCompleted.
     */
    public static <T> OperatorSemaphore<T> semaphoreLatestCache(Observable<Boolean> go) {
        return new OperatorSemaphore<>(go, true, true);
    }

    private OperatorSemaphore(Observable<Boolean> go) {
        this.go = go;
    }

    private OperatorSemaphore(Observable<Boolean> go, boolean latest) {
        this.go = go;
        this.latest = latest;
    }

    private OperatorSemaphore(Observable<Boolean> go, boolean latest, boolean cache) {
        this.go = go;
        this.latest = latest;
        this.cache = cache;
    }

    @Override
    public Subscriber<? super T> call(final Subscriber<? super T> child) {
        return new Subscriber<T>() {

            boolean isOpen;
            ArrayList<T> next = new ArrayList<>();
            boolean deliverCompleted;
            Throwable error;
            boolean deliverError;

            T nextCache;
            boolean hasCache;

            void tick(boolean deliverCache) {
                if (!isUnsubscribed() && isOpen) {

                    while (next.size() > 0) {
                        T value = next.remove(0);
                        child.onNext(value);
                        deliverCache = false;
                        if (cache) {
                            nextCache = value;
                            hasCache = true;
                        }
                    }

                    if (deliverCache && hasCache)
                        child.onNext(nextCache);

                    if (deliverCompleted) {
                        child.onCompleted();
                        unsubscribe();
                    }

                    if (deliverError) {
                        child.onError(error);
                        unsubscribe();
                    }
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                add(go.subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        isOpen = aBoolean;
                        tick(cache);
                    }
                }));
                child.add(this);
            }

            @Override
            public void onCompleted() {
                if (!cache) {
                    deliverCompleted = true;
                    tick(false);
                }
            }

            @Override
            public void onError(Throwable e) {
                error = e;
                deliverError = true;
                tick(false);
            }

            @Override
            public void onNext(T o) {
                if (latest)
                    next.clear();
                next.add(o);
                tick(false);
            }
        };
    }
}
