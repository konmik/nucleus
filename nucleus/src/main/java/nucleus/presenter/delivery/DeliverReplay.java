package nucleus.presenter.delivery;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.ReplaySubject;

public class DeliverReplay<View, T> implements ObservableTransformer<T, Delivery<View, T>> {

    private final Observable<View> view;

    public DeliverReplay(Observable<View> view) {
        this.view = view;
    }

    @Override
    public Observable<Delivery<View, T>> apply(Observable<T> observable) {
        final ReplaySubject<Notification<T>> subject = ReplaySubject.create();
        final Disposable subscription = observable
            .materialize()
            .filter(new Predicate<Notification<T>>() {
                @Override
                public boolean test(Notification<T> notification) {
                    return !notification.isOnComplete();
                }
            })
            .doOnEach(subject)
            .subscribe();
        return view
            .switchMap(new Function<View, Observable<Delivery<View, T>>>() {
                @Override
                public Observable<Delivery<View, T>> apply(final View view) {
                    return view == null ? Observable.<Delivery<View, T>>never() : subject
                        .map(new Function<Notification<T>, Delivery<View, T>>() {
                            @Override
                            public Delivery<View, T> apply(Notification<T> notification) {
                                return new Delivery<>(view, notification);
                            }
                        });
                }
            })
            .doOnDispose(new Action() {
                @Override
                public void run() {
                    subscription.dispose();
                }
            });
    }
}
