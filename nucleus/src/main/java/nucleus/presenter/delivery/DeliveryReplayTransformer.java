package nucleus.presenter.delivery;

import rx.Notification;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subjects.ReplaySubject;

public final class DeliveryReplayTransformer<ViewType, T> implements Observable.Transformer<T, Delivery<ViewType, T>> {

    private final Observable<ViewType> view;

    public DeliveryReplayTransformer(Observable<ViewType> view) {
        this.view = view;
    }

    @Override
    public Observable<Delivery<ViewType, T>> call(final Observable<T> observable1) {
        final ReplaySubject<Notification<T>> subject = ReplaySubject.create();
        final Subscription subscription = observable1.materialize().subscribe(subject);
        return view
            .filter(new Func1<ViewType, Boolean>() {
                @Override
                public Boolean call(ViewType it) {
                    return it != null;
                }
            })
            .switchMap(new Func1<ViewType, Observable<? extends Delivery<ViewType, T>>>() {
                @Override
                public Observable<? extends Delivery<ViewType, T>> call(final ViewType view) {
                    return subject
                        .map(new Func1<Notification<T>, Delivery<ViewType, T>>() {
                            @Override
                            public Delivery<ViewType, T> call(Notification<T> notification) {
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
            });
    }
}
