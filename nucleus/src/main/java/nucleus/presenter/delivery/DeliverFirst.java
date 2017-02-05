package nucleus.presenter.delivery;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import nucleus.view.OptionalView;

import static nucleus.presenter.delivery.Delivery.validObservable;

public class DeliverFirst<View, T> implements ObservableTransformer<T, Delivery<View, T>> {

    private final Observable<OptionalView<View>> view;

    public DeliverFirst(Observable<OptionalView<View>> view) {
        this.view = view;
    }

    @Override
    public ObservableSource<Delivery<View, T>> apply(Observable<T> observable) {
        return observable.materialize()
            .take(1)
            .switchMap(new Function<Notification<T>, ObservableSource<Delivery<View, T>>>() {
                @Override
                public ObservableSource<Delivery<View, T>> apply(final Notification<T> notification) throws Exception {
                    return view.concatMap(new Function<OptionalView<View>, ObservableSource<Delivery<View, T>>>() {
                        @Override
                        public ObservableSource<Delivery<View, T>> apply(OptionalView<View> view) throws Exception {
                            return validObservable(view, notification);
                        }
                    });
                }
            })
            .take(1);
    }
}
