package nucleus5.presenter.delivery;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.subjects.ReplaySubject;
import nucleus5.view.OptionalView;

import static nucleus5.presenter.delivery.Delivery.validObservable;

public class DeliverReplay<View, T> implements ObservableTransformer<T, Delivery<View, T>> {

    private final Observable<OptionalView<View>> view;

    public DeliverReplay(Observable<OptionalView<View>> view) {
        this.view = view;
    }

    @Override
    public Observable<Delivery<View, T>> apply(Observable<T> observable) {
        final ReplaySubject<Notification<T>> subject = ReplaySubject.create();
        final Disposable disposable = observable
            .materialize()
            .doOnEach(subject)
            .subscribe();
        return view
            .switchMap(new Function<OptionalView<View>, ObservableSource<Delivery<View, T>>>() {
                @Override
                public Observable<Delivery<View, T>> apply(final OptionalView<View> view) throws Exception {
                    return subject
                        .concatMap(new Function<Notification<T>, ObservableSource<Delivery<View, T>>>() {
                            @Override
                            public ObservableSource<Delivery<View, T>> apply(Notification<T> notification) throws Exception {
                                return validObservable(view, notification);
                            }
                        });
                }
            })
            .doOnDispose(new Action() {
                @Override
                public void run() {
                    disposable.dispose();
                }
            });
    }
}
