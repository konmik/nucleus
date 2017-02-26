package nucleus5.presenter.delivery;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import nucleus5.view.OptionalView;

public class DeliverLatestCache<View, T> implements ObservableTransformer<T, Delivery<View, T>> {

    private final Observable<OptionalView<View>> view;

    public DeliverLatestCache(Observable<OptionalView<View>> view) {
        this.view = view;
    }

    @Override
    public ObservableSource<Delivery<View, T>> apply(Observable<T> observable) {
        return Observable
            .combineLatest(
                view,
                observable
                    .materialize()
                    .filter(new Predicate<Notification<T>>() {
                        @Override
                        public boolean test(Notification<T> notification) throws Exception {
                            return !notification.isOnComplete();
                        }
                    }),
                new BiFunction<OptionalView<View>, Notification<T>, Object[]>() {
                    @Override
                    public Object[] apply(OptionalView<View> view, Notification<T> notification) throws Exception {
                        return new Object[]{view, notification};
                    }
                })
            .concatMap(new Function<Object[], ObservableSource<Delivery<View, T>>>() {
                @Override
                public ObservableSource<Delivery<View, T>> apply(Object[] pack) throws Exception {
                    return Delivery.validObservable((OptionalView<View>) pack[0], (Notification<T>) pack[1]);
                }
            });
    }
}
