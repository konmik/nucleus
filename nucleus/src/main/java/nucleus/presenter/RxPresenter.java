package nucleus.presenter;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import nucleus.SilentCallable;
import nucleus.presenter.delivery.DeliverFirst;
import nucleus.presenter.delivery.DeliverLatestCache;
import nucleus.presenter.delivery.DeliverReplay;
import nucleus.presenter.delivery.Delivery;

/**
 * This is an extension of {@link Presenter} which provides RxJava functionality.
 *
 * @param <View> a type of view.
 */
public class RxPresenter<View> extends Presenter<View> {

    private static final String REQUESTED_KEY = RxPresenter.class.getName() + "#requested";

    private final BehaviorSubject<View> views = BehaviorSubject.create();
    private final CompositeDisposable subscriptions = new CompositeDisposable();

    private final HashMap<Integer, SilentCallable<Disposable>> restartables = new HashMap<>();
    private final HashMap<Integer, Disposable> restartableSubscriptions = new HashMap<>();
    private final ArrayList<Integer> requested = new ArrayList<>();

    /**
     * Returns an {@link Observable} that emits the current attached view or null.
     * See {@link BehaviorSubject} for more information.
     *
     * @return an observable that emits the current attached view or null.
     */
    public Observable<View> view() {
        return views;
    }

    /**
     * Registers a subscription to automatically unsubscribe it during onDestroy.
     * See {@link CompositeDisposable#add(Disposable) for details.}
     *
     * @param subscription a subscription to add.
     */
    public void add(Disposable subscription) {
        subscriptions.add(subscription);
    }

    /**
     * Removes and unsubscribes a subscription that has been registered with {@link #add} previously.
     * See {@link CompositeDisposable#remove(Disposable)} for details.
     *
     * @param subscription a subscription to remove.
     */
    public void remove(Disposable subscription) {
        subscriptions.remove(subscription);
    }

    /**
     * A restartable is any RxJava observable that can be started (subscribed) and
     * should be automatically restarted (re-subscribed) after a process restart if
     * it was still subscribed at the moment of saving presenter's state.
     *
     * Registers a factory. Re-subscribes the restartable after the process restart.
     *
     * @param restartableId id of the restartable
     * @param factory       factory of the restartable
     */
    public void restartable(int restartableId, SilentCallable<Disposable> factory) {
        restartables.put(restartableId, factory);
        if (requested.contains(restartableId))
            start(restartableId);
    }

    /**
     * Starts the given restartable.
     *
     * @param restartableId id of the restartable
     */
    public void start(int restartableId) {
        stop(restartableId);
        requested.add(restartableId);
        restartableSubscriptions.put(restartableId, restartables.get(restartableId).call());
    }

    /**
     * Unsubscribes a restartable
     *
     * @param restartableId id of a restartable.
     */
    public void stop(int restartableId) {
        requested.remove((Integer) restartableId);
        Disposable subscription = restartableSubscriptions.get(restartableId);
        if (subscription != null)
            subscription.dispose();
    }

    /**
     * Checks if a restartable is unsubscribed.
     *
     * @param restartableId id of the restartable.
     * @return true if the subscription is null or unsubscribed, false otherwise.
     */
    public boolean isDisposed(int restartableId) {
        Disposable subscription = restartableSubscriptions.get(restartableId);
        return subscription == null || subscription.isDisposed();
    }

    /**
     * This is a shortcut that can be used instead of combining together
     * {@link #restartable(int, SilentCallable)},
     * {@link #deliverFirst()},
     * {@link #split(BiConsumer, BiConsumer)}.
     *
     * @param restartableId     an id of the restartable.
     * @param observableFactory a factory that should return an Observable when the restartable should run.
     * @param onNext            a callback that will be called when received data should be delivered to view.
     * @param onError           a callback that will be called if the source observable emits onError.
     * @param <T>               the type of the observable.
     */
    public <T> void restartableFirst(int restartableId, final SilentCallable<Observable<T>> observableFactory,
        final BiConsumer<View, T> onNext, @Nullable final BiConsumer<View, Throwable> onError) {

        restartable(restartableId, new SilentCallable<Disposable>() {
            @Override
            public Disposable call() {
                return observableFactory.call()
                    .compose(RxPresenter.this.<T>deliverFirst())
                    .subscribe(split(onNext, onError));
            }
        });
    }

    /**
     * This is a shortcut for calling {@link #restartableFirst(int, SilentCallable, BiConsumer, BiConsumer)} with the last parameter = null.
     */
    public <T> void restartableFirst(int restartableId, final SilentCallable<Observable<T>> observableFactory, final BiConsumer<View, T> onNext) {
        restartableFirst(restartableId, observableFactory, onNext, null);
    }

    /**
     * This is a shortcut that can be used instead of combining together
     * {@link #restartable(int, SilentCallable)},
     * {@link #deliverLatestCache()},
     * {@link #split(BiConsumer, BiConsumer)}.
     *
     * @param restartableId     an id of the restartable.
     * @param observableFactory a factory that should return an Observable when the restartable should run.
     * @param onNext            a callback that will be called when received data should be delivered to view.
     * @param onError           a callback that will be called if the source observable emits onError.
     * @param <T>               the type of the observable.
     */
    public <T> void restartableLatestCache(int restartableId, final SilentCallable<Observable<T>> observableFactory,
        final BiConsumer<View, T> onNext, @Nullable final BiConsumer<View, Throwable> onError) {

        restartable(restartableId, new SilentCallable<Disposable>() {
            @Override
            public Disposable call() {
                return observableFactory.call()
                    .compose(RxPresenter.this.<T>deliverLatestCache())
                    .subscribe(split(onNext, onError));
            }
        });
    }

    /**
     * This is a shortcut for calling {@link #restartableLatestCache(int, SilentCallable, BiConsumer, BiConsumer)} with the last parameter = null.
     */
    public <T> void restartableLatestCache(int restartableId, final SilentCallable<Observable<T>> observableFactory, final BiConsumer<View, T> onNext) {
        restartableLatestCache(restartableId, observableFactory, onNext, null);
    }

    /**
     * This is a shortcut that can be used instead of combining together
     * {@link #restartable(int, SilentCallable)},
     * {@link #deliverReplay()},
     * {@link #split(BiConsumer, BiConsumer)}.
     *
     * @param restartableId     an id of the restartable.
     * @param observableFactory a factory that should return an Observable when the restartable should run.
     * @param onNext            a callback that will be called when received data should be delivered to view.
     * @param onError           a callback that will be called if the source observable emits onError.
     * @param <T>               the type of the observable.
     */
    public <T> void restartableReplay(int restartableId, final SilentCallable<Observable<T>> observableFactory,
        final BiConsumer<View, T> onNext, @Nullable final BiConsumer<View, Throwable> onError) {

        restartable(restartableId, new SilentCallable<Disposable>() {
            @Override
            public Disposable call() {
                return observableFactory.call()
                    .compose(RxPresenter.this.<T>deliverReplay())
                    .subscribe(split(onNext, onError));
            }
        });
    }

    /**
     * This is a shortcut for calling {@link #restartableReplay(int, SilentCallable, BiConsumer, BiConsumer)} with the last parameter = null.
     */
    public <T> void restartableReplay(int restartableId, final SilentCallable<Observable<T>> observableFactory, final BiConsumer<View, T> onNext) {
        restartableReplay(restartableId, observableFactory, onNext, null);
    }

    /**
     * Returns an {@link ObservableTransformer} that couples views with data that has been emitted by
     * the source {@link Observable}.
     *
     * {@link #deliverLatestCache} keeps the latest onNext value and emits it each time a new view gets attached.
     * If a new onNext value appears while a view is attached, it will be delivered immediately.
     *
     * @param <T> the type of source observable emissions
     */
    public <T> DeliverLatestCache<View, T> deliverLatestCache() {
        return new DeliverLatestCache<>(views);
    }

    /**
     * Returns an {@link ObservableTransformer} that couples views with data that has been emitted by
     * the source {@link Observable}.
     *
     * {@link #deliverFirst} delivers only the first onNext value that has been emitted by the source observable.
     *
     * @param <T> the type of source observable emissions
     */
    public <T> DeliverFirst<View, T> deliverFirst() {
        return new DeliverFirst<>(views);
    }

    /**
     * Returns an {@link ObservableTransformer} that couples views with data that has been emitted by
     * the source {@link Observable}.
     *
     * {@link #deliverReplay} keeps all onNext values and emits them each time a new view gets attached.
     * If a new onNext value appears while a view is attached, it will be delivered immediately.
     *
     * @param <T> the type of source observable emissions
     */
    public <T> DeliverReplay<View, T> deliverReplay() {
        return new DeliverReplay<>(views);
    }

    /**
     * Returns a method that can be used for manual restartable chain build. It returns an Consumer that splits
     * a received {@link Delivery} into two {@link BiConsumer} onNext and onError calls.
     *
     * @param onNext  a method that will be called if the delivery contains an emitted onNext value.
     * @param onError a method that will be called if the delivery contains an onError throwable.
     * @param <T>     a type on onNext value.
     * @return an Consumer that splits a received {@link Delivery} into two {@link BiConsumer} onNext and onError calls.
     */
    public <T> Consumer<Delivery<View, T>> split(final BiConsumer<View, T> onNext, @Nullable final BiConsumer<View, Throwable> onError) {
        return new Consumer<Delivery<View, T>>() {
            @Override
            public void accept(Delivery<View, T> delivery) throws Exception {
                delivery.split(onNext, onError);
            }
        };
    }

    /**
     * This is a shortcut for calling {@link #split(BiConsumer, BiConsumer)} when the second parameter is null.
     */
    public <T> Consumer<Delivery<View, T>> split(BiConsumer<View, T> onNext) {
        return split(onNext, null);
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    protected void onCreate(Bundle savedState) {
        if (savedState != null)
            requested.addAll(savedState.getIntegerArrayList(REQUESTED_KEY));
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    protected void onDestroy() {
        views.onComplete();
        subscriptions.dispose();
        for (Map.Entry<Integer, Disposable> entry : restartableSubscriptions.entrySet())
            entry.getValue().dispose();
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    protected void onSave(Bundle state) {
        for (int i = requested.size() - 1; i >= 0; i--) {
            int restartableId = requested.get(i);
            Disposable subscription = restartableSubscriptions.get(restartableId);
            if (subscription != null && subscription.isDisposed())
                requested.remove(i);
        }
        state.putIntegerArrayList(REQUESTED_KEY, requested);
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    protected void onTakeView(View view) {
        views.onNext(view);
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    protected void onDropView() {
        views.onNext(null);
    }

    /**
     * Please, use restartableXX and deliverXX methods for pushing data from RxPresenter into View.
     */
    @Deprecated
    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }
}
