package nucleus.presenter;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;

import nucleus.presenter.delivery.DeliverDelivery;
import nucleus.presenter.delivery.Delivery;
import nucleus.presenter.delivery.DeliveryCacheTransformer;
import nucleus.presenter.delivery.DeliveryOnceTransformer;
import nucleus.presenter.delivery.DeliveryReplayTransformer;
import nucleus.presenter.delivery.DeliveryRule;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.internal.util.SubscriptionList;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * This is an extension of {@link nucleus.presenter.Presenter} which provides RxJava functionality.
 *
 * @param <ViewType> a type of view
 */
public class RxPresenter<ViewType> extends Presenter<ViewType> {

    private static final String REQUESTED_KEY = RxPresenter.class.getName() + "#requested";

    public static final DeliveryRule ONCE = DeliveryRule.ONCE;
    public static final DeliveryRule CACHE = DeliveryRule.CACHE;
    public static final DeliveryRule REPLAY = DeliveryRule.REPLAY;

    private ArrayList<Integer> requested = new ArrayList<>();
    private HashMap<Integer, Func0<Subscription>> factories = new HashMap<>();
    private HashMap<Integer, Subject> triggers = new HashMap<>();
    private HashMap<Integer, Subscription> restartableSubscriptions = new HashMap<>();

    private BehaviorSubject<ViewType> view = BehaviorSubject.create();
    private SubscriptionList subscriptions = new SubscriptionList();

    /**
     * Returns an observable that emits current status of a view.
     * True - a view is attached, False - a view is detached.
     *
     * @return an observable that emits current status of a view.
     */
    public Observable<ViewType> view() {
        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedState) {
        if (savedState != null)
            requested = savedState.getIntegerArrayList(REQUESTED_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Subscription subs : restartableSubscriptions.values())
            subs.unsubscribe();
        view.onCompleted();
        subscriptions.unsubscribe();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSave(Bundle state) {
        super.onSave(state);
        for (int i = requested.size() - 1; i >= 0; i--) {
            Integer restartableId = requested.get(i);
            if (restartableSubscriptions.get(restartableId).isUnsubscribed()) {
                requested.remove(i);
                restartableSubscriptions.remove(restartableId);
            }
        }
        state.putIntegerArrayList(REQUESTED_KEY, requested);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTakeView(ViewType view) {
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
     * A restartable is any RxJava query/request that can be started (subscribed) and
     * should be automatically restarted (re-subscribed) after a process restart.
     * <p/>
     * Registers a factory for a restartable. Re-subscribes the restartable after a process restart.
     *
     * @param restartableId id of a restartable.
     * @param factory       a factory that will create an actual rxjava subscription when requested.
     */
    public void registerRestartable(int restartableId, Func0<Subscription> factory) {
        factories.put(restartableId, factory);
        if (requested.contains(restartableId))
            restartableSubscriptions.put(restartableId, factories.get(restartableId).call());
    }

    public Observable<Integer> restartable(final int restartableId) {
        PublishSubject<Integer> subject = PublishSubject.create();
        triggers.put(restartableId, subject);
        if (requested.contains(restartableId))
            subscribeRestartable(restartableId);
        return subject.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                unsubscribeRestartable(restartableId);
            }
        });
    }

    /**
     * Subscribes (runs) a restartable using a factory method provided with {@link #registerRestartable}.
     * If a presenter gets lost during a process restart while a restartable is still
     * subscribed, the restartable will be restarted on next {@link #registerRestartable} call.
     * <p/>
     * If the restartable is already subscribed then it will be unsubscribed first.
     * <p/>
     * The restartable will be unsubscribed during {@link #onDestroy()}
     *
     * @param restartableId id of a restartable.
     */
    public void subscribeRestartable(int restartableId) {
        unsubscribeRestartable(restartableId);
        requested.add(restartableId);
        if (factories.containsKey(restartableId))
            restartableSubscriptions.put(restartableId, factories.get(restartableId).call());
        else
            triggers.get(restartableId).onNext(restartableId);
    }

    /**
     * Unsubscribes a restartable
     *
     * @param restartableId id of a restartable.
     */
    public void unsubscribeRestartable(int restartableId) {
        if (restartableSubscriptions.containsKey(restartableId)) {
            restartableSubscriptions.get(restartableId).unsubscribe();
            restartableSubscriptions.remove(restartableId);
        }
        requested.remove((Integer)restartableId);
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

    /**
     * Returns a transformer that will delay onNext, onError and onComplete emissions unless a view become available.
     * getView() is guaranteed to be != null during all emissions. This transformer can only be used on application's main thread.
     * <p/>
     * Use this operator if you need to deliver *all* emissions to a view, in example when you're sending items
     * into adapter one by one.
     *
     * @param <T> a type of onNext value.
     * @return the delaying operator.
     */
    public <T> Observable.Transformer<T, Delivery<ViewType, T>> delivery(DeliveryRule rule) {
        return rule == ONCE ? new DeliveryOnceTransformer<ViewType, T>(view) :
            rule == CACHE ? new DeliveryCacheTransformer<ViewType, T>(view) :
                new DeliveryReplayTransformer<ViewType, T>(view);
    }

    public <T> Action1<Delivery<ViewType, T>> deliver(Action2<ViewType, T> onNext, Action2<ViewType, Throwable> onError) {
        return new DeliverDelivery<>(onNext, onError);
    }

    public <T> Action1<Delivery<ViewType, T>> deliver(Action2<ViewType, T> onNext) {
        return new DeliverDelivery<>(onNext);
    }
}
