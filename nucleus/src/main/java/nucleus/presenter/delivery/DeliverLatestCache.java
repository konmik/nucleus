package nucleus.presenter.delivery;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Predicate;

public class DeliverLatestCache<View, T> implements ObservableTransformer<T, Delivery<View, T>> {

    private final Observable<View> view;

    public DeliverLatestCache(Observable<View> view) {
        this.view = view;
    }

    @Override
    public Observable<Delivery<View, T>> apply(Observable<T> observable) {
        return Observable
            .combineLatest(
                view,
                observable
                    .materialize()
                    .filter(new Predicate<Notification<T>>() {
                        @Override
                        public boolean test(Notification<T> notification) {
                            return !notification.isOnComplete();
                        }
                    }),
                new BiFunction<View, Notification<T>, Delivery<View, T>>() {
                    @Override
                    public Delivery<View, T> apply(View view, Notification<T> notification) {
                        return view == null ? null : new Delivery<>(view, notification);
                    }
                })
            .filter(new Predicate<Delivery<View, T>>() {
                @Override
                public boolean test(Delivery<View, T> delivery) {
                    return delivery != null;
                }
            });
    }
}
