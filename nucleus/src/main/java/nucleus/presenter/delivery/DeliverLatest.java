package nucleus.presenter.delivery;

import rx.Notification;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.functions.Func2;

public class DeliverLatest<View, T> implements Observable.Transformer<T, Delivery<View, T>> {

    private final Observable<View> view;

    public DeliverLatest(Observable<View> view) {
        this.view = view;
    }

    @Override
    public Observable<Delivery<View, T>> call(final Observable<T> observable) {
        return Observable.create(new Observable.OnSubscribe<Delivery<View, T>>() {
            @Override
            public void call(final Subscriber<? super Delivery<View, T>> subscriber) {
                Subscription subscription = Observable
                        .combineLatest(
                                view,
                                observable
                                        .doOnCompleted(new Action0() {
                                            @Override
                                            public void call() {
                                                if (!subscriber.isUnsubscribed()) {
                                                    subscriber.onCompleted();
                                                }
                                            }
                                        })
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
                                }
                        )
                        .filter(new Func1<Delivery<View, T>, Boolean>() {
                            @Override
                            public Boolean call(Delivery<View, T> delivery) {
                                return delivery != null;
                            }
                        })
                        .subscribe(subscriber);

                subscriber.add(subscription);
            }
        });
    }
}
