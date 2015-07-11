package nucleus.presenter;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nucleus.presenter.restartable.Restartable;
import nucleus.presenter.restartable.RestartableCache;
import nucleus.presenter.restartable.RestartableOnce;
import nucleus.presenter.restartable.RestartableReplay;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.internal.util.SubscriptionList;
import rx.subjects.BehaviorSubject;

/**
 * This is an extension of {@link nucleus.presenter.Presenter} which provides RxJava functionality.
 *
 * @param <View> a type of view
 */
public class RxPresenter<View> extends Presenter<View> {

    private static final String STARTED_KEY = RxPresenter.class.getName() + "#started";

    private final BehaviorSubject<View> view = BehaviorSubject.create();
    private final SubscriptionList subscriptions = new SubscriptionList();

    private final HashMap<Integer, Restartable> restartables = new HashMap<>();
    private final HashMap<Integer, Subscription> restartableSubscriptions = new HashMap<>();
    private final ArrayList<Integer> started = new ArrayList<>();

    public <T> void restartableOnce(int restartableId, Func0<Observable<T>> factory,
        Action2<View, T> onNext, Action2<View, Throwable> onError) {
        restartable(restartableId, new RestartableOnce<>(view, factory, onNext, onError));
    }

    public <T> void restartableCache(int restartableId, Func0<Observable<T>> factory,
        Action2<View, T> onNext, Action2<View, Throwable> onError) {
        restartable(restartableId, new RestartableCache<>(view, factory, onNext, onError));
    }

    public <T> void restartableReplay(int restartableId, Func0<Observable<T>> factory,
        Action2<View, T> onNext, Action2<View, Throwable> onError) {
        restartable(restartableId, new RestartableReplay<>(view, factory, onNext, onError));
    }

    public void start(int restartableId) {
        stop(restartableId);
        started.add(restartableId);
        restartableSubscriptions.put(restartableId, restartables.get(restartableId).call());
    }

    /**
     * Unsubscribes a restartable
     *
     * @param restartableId id of a restartable.
     */
    public void stop(int restartableId) {
        started.remove((Integer)restartableId);
        Subscription subscription = restartableSubscriptions.get(restartableId);
        if (subscription != null)
            subscription.unsubscribe();
    }

    public void restartable(int restartableId, Restartable restartable) {
        restartables.put(restartableId, restartable);
        if (started.contains(restartableId))
            start(restartableId);
    }

    /**
     * Returns an observable that emits current status of a view.
     * True - a view is attached, False - a view is detached.
     *
     * @return an observable that emits current status of a view.
     */
    public Observable<View> view() {
        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedState) {
        if (savedState != null)
            started.addAll(savedState.getIntegerArrayList(STARTED_KEY));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        view.onCompleted();
        subscriptions.unsubscribe();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSave(Bundle state) {
        super.onSave(state);
        for (Map.Entry<Integer, Subscription> entity : restartableSubscriptions.entrySet()) {
            if (entity.getValue().isUnsubscribed())
                started.remove(entity.getKey());
        }
        state.putIntegerArrayList(STARTED_KEY, started);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTakeView(View view) {
        super.onTakeView(view);
        this.view.onNext(view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDropView() {
        super.onDropView();
        view.onNext(null);
    }

    /**
     * Registers a subscription to automatically unsubscribe it during onDestroy.
     * See {@link SubscriptionList#add(Subscription) for details.}
     *
     * @param subscription a subscription to add.
     */
    public void add(Subscription subscription) {
        subscriptions.add(subscription);
    }

    /**
     * Removes and unsubscribes a subscription that has been registered with {@link #add} previously.
     * See {@link SubscriptionList#remove(Subscription) for details.}
     *
     * @param subscription a subscription to remove.
     */
    public void remove(Subscription subscription) {
        subscriptions.remove(subscription);
    }
}
