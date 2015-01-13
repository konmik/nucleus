package nucleus.presenter;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Action3;
import rx.functions.Action4;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.functions.Func4;
import rx.subscriptions.Subscriptions;

public class RxPresenter<ViewType> extends Presenter<ViewType> {

    /**
     * Observes a view status. Passes a View when attached or null when the view is dropped.
     */
    protected Observable<ViewType> viewBehavior() {
        return Observable.create(new Observable.OnSubscribe<ViewType>() {
            @Override
            public void call(final Subscriber<? super ViewType> subscriber) {

                final TargetListener<ViewType> listener = new TargetListener<ViewType>() {
                    @Override
                    public void onTakeTarget(ViewType view) {
                        subscriber.onNext(view);
                    }

                    @Override
                    public void onDropTarget(ViewType view) {
                        subscriber.onNext(null); // passing the null is essential to prevent memory leaks
                    }
                };

                addViewListener(listener);

                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        removeViewListener(listener);
                    }
                }));
                subscriber.onNext(getView());
            }
        });
    }

    /**
     * This is where the magic takes place. Action is being called when:
     * 1. View is present and observable passes a result.
     * 2. View is present and observable updates a result.
     * 3. Observable's result is present and the View appears.
     * Don't forget to unsubscribe in Presenter's onDestroy
     *
     * @param observable1
     * @param action
     * @param <T1>
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T1> Subscription broker(Observable<T1> observable1, final Action2<T1, ViewType> action) {
        return Observable.combineLatest(observable1, viewBehavior(), new Func2<T1, ViewType, Object[]>() {
            @Override
            public Object[] call(T1 t1, ViewType view) {
                return new Object[]{view, t1};
            }
        }).filter(new Func1<Object[], Boolean>() {
            @Override
            public Boolean call(Object[] objects) {
                return objects[0] != null;
            }
        }).subscribe(new Action1<Object[]>() {
            @Override
            public void call(Object[] o) {
                action.call((T1)o[1], (ViewType)o[0]);
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected <T1, T2> Subscription broker(Observable<T1> observable1, Observable<T2> observable2, final Action3<T1, T2, ViewType> action) {
        return Observable.combineLatest(observable1, observable2, viewBehavior(), new Func3<T1, T2, ViewType, Object[]>() {
            @Override
            public Object[] call(T1 t1, T2 t2, ViewType view) {
                return new Object[]{view, t1, t2};
            }
        }).filter(new Func1<Object[], Boolean>() {
            @Override
            public Boolean call(Object[] objects) {
                return objects[0] != null;
            }
        }).subscribe(new Action1<Object[]>() {
            @Override
            public void call(Object[] o) {
                action.call((T1)o[1], (T2)o[2], (ViewType)o[0]);
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected <T1, T2, T3> Subscription broker(Observable<T1> observable1, Observable<T2> observable2, Observable<T3> observable3, final Action4<T1, T2, T3, ViewType> action) {
        return Observable.combineLatest(observable1, observable2, observable3, viewBehavior(), new Func4<T1, T2, T3, ViewType, Object[]>() {
            @Override
            public Object[] call(T1 t1, T2 t2, T3 t3, ViewType view) {
                return new Object[]{view, t1, t2, t3};
            }
        }).filter(new Func1<Object[], Boolean>() {
            @Override
            public Boolean call(Object[] objects) {
                return objects[0] != null;
            }
        }).subscribe(new Action1<Object[]>() {
            @Override
            public void call(Object[] o) {
                action.call((T1)o[1], (T2)o[2], (T3)o[3], (ViewType)o[0]);
            }
        });
    }

    /**
     * Simplified version of RxPresenter#broker that will auto-unsubscribe itself on Presenter#onDestroy.
     * @param observable
     * @param action
     * @param <T>
     */
    protected <T> void addRxViewBroker(Observable<T> observable, Action2<T, ViewType> action) {
        final Subscription subscription = broker(observable, action);
        addOnDestroyListener(new OnDestroyListener() {
            @Override
            public void onDestroy() {
                subscription.unsubscribe();
            }
        });
    }

    protected <T1, T2> void addRxViewBroker(Observable<T1> observable1, Observable<T2> observable2, Action3<T1, T2, ViewType> action) {
        final Subscription subscription = broker(observable1, observable2, action);
        addOnDestroyListener(new OnDestroyListener() {
            @Override
            public void onDestroy() {
                subscription.unsubscribe();
            }
        });
    }

    protected <T1, T2, T3> void addRxViewBroker(Observable<T1> observable1, Observable<T2> observable2, Observable<T3> observable3, Action4<T1, T2, T3, ViewType> action) {
        final Subscription subscription = broker(observable1, observable2, observable3, action);
        addOnDestroyListener(new OnDestroyListener() {
            @Override
            public void onDestroy() {
                subscription.unsubscribe();
            }
        });
    }
}
