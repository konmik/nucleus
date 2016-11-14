package nucleus.presenter.delivery;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class DeliverFirst<View, T> implements ObservableTransformer<T, Delivery<View, T>> {

    private final Observable<View> view;

    public DeliverFirst(Observable<View> view) {
        this.view = view;
    }

    @Override
    public Observable<Delivery<View, T>> apply(Observable<T> observable) {
        return observable.materialize()
            .take(1)
            .switchMap(new Function<Notification<T>, ObservableSource<? extends Delivery<View,T>>>() {
                @Override
                public Observable<? extends Delivery<View, T>> apply(final Notification<T> notification) {
                    return view.map(new Function<View, Delivery<View, T>>() {
                        @Override
                        public Delivery<View, T> apply(View view) {
                            return view == null ? null : new Delivery<>(view, notification);
                        }
                    });
                }
            })
            .filter(new Predicate<Delivery<View,T>>() {
                @Override
                public boolean test(Delivery<View, T> delivery) {
                    return delivery != null;
                }
            })
            .take(1);
    }
}
