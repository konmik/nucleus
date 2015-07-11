package nucleus.presenter.restartable;

import rx.Notification;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;

public class RestartableOnce<View, T> implements Restartable {

    private final Observable<View> view;
    private final Func0<Observable<T>> factory;
    private final Action2<View, T> onNext;
    private final Action2<View, Throwable> onError;

    public RestartableOnce(Observable<View> view, Func0<Observable<T>> factory, Action2<View, T> onNext, Action2<View, Throwable> onError) {
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
                factory.call().materialize().first(),
                new Func2<View, Notification<T>, Delivery<View, T>>() {
                    @Override
                    public Delivery<View, T> call(View view, Notification<T> tNotification) {
                        return view == null ? null : new Delivery<>(view, tNotification);
                    }
                })
            .filter(new Func1<Delivery<View, T>, Boolean>() {
                @Override
                public Boolean call(Delivery<View, T> delivery) {
                    return delivery != null;
                }
            })
            .first()
            .subscribe(new Action1<Delivery<View, T>>() {
                @Override
                public void call(Delivery<View, T> delivery) {
                    delivery.split(onNext, onError);
                }
            });
    }
}
