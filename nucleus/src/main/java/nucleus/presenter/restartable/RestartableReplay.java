package nucleus.presenter.restartable;

import nucleus.presenter.delivery.Delivery;
import rx.Notification;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subjects.ReplaySubject;

public class RestartableReplay<View, T> extends Restartable {

    private final Observable<View> view;
    private final Func0<Observable<T>> factory;
    private final Action2<View, T> onNext;
    private final Action2<View, Throwable> onError;

    public RestartableReplay(Observable<View> view, Func0<Observable<T>> factory, Action2<View, T> onNext, Action2<View, Throwable> onError) {
        this.view = view;
        this.factory = factory;
        this.onNext = onNext;
        this.onError = onError;
    }

    @Override
    public Subscription start() {
        final ReplaySubject<Notification<T>> subject = ReplaySubject.create();
        final Subscription subscription = factory.call()
            .materialize()
            .filter(new Func1<Notification<T>, Boolean>() {
                @Override
                public Boolean call(Notification<T> notification) {
                    return !notification.isOnCompleted();
                }
            })
            .subscribe(subject);
        return view
            .filter(new Func1<View, Boolean>() {
                @Override
                public Boolean call(View it) {
                    return it != null;
                }
            })
            .switchMap(new Func1<View, Observable<Delivery<View, T>>>() {
                @Override
                public Observable<Delivery<View, T>> call(final View view) {
                    return subject
                        .map(new Func1<Notification<T>, Delivery<View, T>>() {
                            @Override
                            public Delivery<View, T> call(Notification<T> notification) {
                                return new Delivery<>(view, notification);
                            }
                        });
                }
            })
            .doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
                    subscription.unsubscribe();
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
