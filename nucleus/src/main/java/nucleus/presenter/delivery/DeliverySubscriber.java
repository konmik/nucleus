package nucleus.presenter.delivery;


import rx.Notification;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public final class DeliverySubscriber<View, T> extends Subscriber<T> {

    private final BehaviorSubject<View> view;
    private final DeliveryRule rule;
    private final Action2<View, T> onNext;
    private final Action2<View, Throwable> onError;
    private Subject<T, T> source;

    public DeliverySubscriber(BehaviorSubject<View> view, DeliveryRule rule, Action2<View, T> onNext) {
        this(view, rule, onNext, null);
    }

    public DeliverySubscriber(BehaviorSubject<View> view, DeliveryRule rule, Action2<View, T> onNext, Action2<View, Throwable> onError) {
        this.rule = rule;
        this.view = view;
        this.onNext = onNext;
        this.onError = onError;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (rule == DeliveryRule.CACHE) {
            source = PublishSubject.create();
            add(Observable
                    .combineLatest(view, source.materialize(), new Func2<View, Notification<T>, Delivery<View, T>>() {
                        @Override
                        public Delivery<View, T> call(View view, Notification<T> notification) {
                            return view == null ? null : new Delivery<>(view, notification);
                        }
                    })
                    .filter(new Func1<Delivery<View, T>, Boolean>() {
                        @Override
                        public Boolean call(Delivery<View, T> it) {
                            return it != null;
                        }
                    })
                    .subscribe(new Action1<Delivery<View, T>>() {
                        @Override
                        public void call(Delivery<View, T> delivery) {
                            delivery.split(onNext, onError);
                        }
                    })
            );
        }
    }

    @Override
    public void onCompleted() {
        source.onCompleted();
    }

    @Override
    public void onError(Throwable e) {
        source.onError(e);
    }

    @Override
    public void onNext(T t) {
        source.onNext(t);
    }
}

