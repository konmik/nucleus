package nucleus.presenter.delivery;

import rx.Notification;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public final class DeliveryCacheTransformer<ViewType, T> implements Observable.Transformer<T, Delivery<ViewType, T>> {

    private final Observable<ViewType> view;

    public DeliveryCacheTransformer(Observable<ViewType> view) {
        this.view = view;
    }

    @Override
    public Observable<Delivery<ViewType, T>> call(final Observable<T> observable1) {
        return Observable
            .combineLatest(view, observable1.materialize(), new Func2<ViewType, Notification<T>, Delivery<ViewType, T>>() {
                @Override
                public Delivery<ViewType, T> call(ViewType view, Notification<T> notification) {
                    return view == null ? null : new Delivery<>(view, notification);
                }
            })
            .filter(new Func1<Delivery<ViewType, T>, Boolean>() {
                @Override
                public Boolean call(Delivery<ViewType, T> it) {
                    return it != null;
                }
            });
    }
}
