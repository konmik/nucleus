package nucleus.presenter.restartable;

import rx.Notification;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;

public class RestartableCache<View, T> implements Restartable {

    private final Observable<View> view;
    private final Func0<Observable<T>> factory;
    private final Action2<View, T> onNext;
    private final Action2<View, Throwable> onError;

    public RestartableCache(Observable<View> view, Func0<Observable<T>> factory, Action2<View, T> onNext, Action2<View, Throwable> onError) {
        this.view = view;
        this.factory = factory;
        this.onNext = onNext;
        this.onError = onError;
    }

    @Override
    public Subscription call() {
        return Observable
            .combineLatest(
                view,
                factory.call()
                    .materialize()
                    .filter(new Func1<Notification<T>, Boolean>() {
                        @Override
                        public Boolean call(Notification<T> notification) {
                            return !notification.isOnCompleted();
                        }
                    }),
                new Func2<View, Notification<T>, Delivery<View, T>>() {
                    @Override
                    public Delivery<View, T> call(View view, Notification<T> notification) {
                        return view == null ? null : new Delivery<>(view, notification);
                    }
                })
            .filter(new Func1<Delivery<View, T>, Boolean>() {
                @Override
                public Boolean call(Delivery<View, T> delivery) {
                    return delivery != null;
                }
            })
            .subscribe(new Action1<Delivery<View, T>>() {
                @Override
                public void call(Delivery<View, T> delivery) {
                    delivery.split(onNext, onError);
                }
            });
    }
}
