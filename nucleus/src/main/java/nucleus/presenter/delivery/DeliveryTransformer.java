package nucleus.presenter.delivery;

import rx.Notification;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public final class DeliveryTransformer<ViewType, T> implements Observable.Transformer<T, Delivery<ViewType, T>> {

    public enum DeliveryRule {
        /**
         * Only the first item or throwable will be delivered and only once.
         */
        ONCE,

        /**
         * Only the latest item or throwable will be saved in memory and delivered each time a new view gets attached.
         * If a new item is received while a view is attached it will be delivered immediately.
         */
        CACHE,

        /**
         * All items and a throwable will be saved in memory and replayed every time a view is being attached.
         */
        REPLAY
    }

    private final Observable<ViewType> view;
    private final DeliveryRule rule;

    public DeliveryTransformer(Observable<ViewType> view, DeliveryRule rule) {
        this.view = view;
        this.rule = rule;
    }

    @Override
    public Observable<Delivery<ViewType, T>> call(Observable<T> observable1) {

        final Subject<Notification<T>, Notification<T>> subject = rule == DeliveryRule.REPLAY ?
            ReplaySubject.<Notification<T>>create() :
            ReplaySubject.<Notification<T>>createWithSize(1);

        final Observable<Notification<T>> source = rule == DeliveryRule.ONCE ?
            subject.take(1) :
            subject;

        final Subscription subscription = observable1.materialize().subscribe(subject);

        return view
            .switchMap(new Func1<ViewType, Observable<Delivery<ViewType, T>>>() {
                @Override
                public Observable<Delivery<ViewType, T>> call(final ViewType view) {
                    return view == null ? Observable.<Delivery<ViewType, T>>never() :
                        source.map(new Func1<Notification<T>, Delivery<ViewType, T>>() {
                            @Override
                            public Delivery<ViewType, T> call(Notification<T> t) {
                                return new Delivery<>(view, t);
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
