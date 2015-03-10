package nucleus.presenter;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class DeliverOperator<T> implements Observable.Operator<T, T> {
    @Override
    public Subscriber<? super T> call(Subscriber<? super T> subscriber) {
        return null;
    }
//    boolean keepLatestOnly;
//    boolean replayLatest;
//
//    private DeliverOperator(boolean keepLatestOnly, boolean replayLatest) {
//        this.keepLatestOnly = keepLatestOnly;
//        this.replayLatest = replayLatest;
//    }
//
//    public static <T> DeliverOperator<T> deliver(Observable<Boolean> semaphore) {
//
//    }
//
//    @Override
//    public Subscriber<? super T> call(final Subscriber<? super T> child) {
//        return new Subscriber<T>() {
//
//            Action0 tick = new Action0() {
//                @Override
//                public void call() {
//                    if (!child.isUnsubscribed() && getView() != null) {
//
//                        while (deliverNext.size() > (replayLatest ? 1 : 0))
//                            child.onNext(deliverNext.remove(0));
//
//                        if (deliverNext.size() > 0)
//                            child.onNext(deliverNext.get(0));
//
//                        if (deliverError) {
//                            deliverNext.clear();
//                            child.onError(error);
//                            unsubscribe();
//                            error = null;
//                            deliverError = false;
//                        }
//                        if (deliverCompleted && !replayLatest) {
//                            deliverNext.clear();
//                            child.onCompleted();
//                            unsubscribe();
//                            deliverCompleted = false;
//                        }
//                    }
//                }
//            };
//
//            ArrayList<T> deliverNext = new ArrayList<>();
//            Throwable error;
//            boolean deliverError;
//            boolean deliverCompleted;
//
//            @Override
//            public void onStart() {
//                super.onStart();
//                RxPresenter.this.callOnTakeView.add(tick);
//                add(Subscriptions.create(new Action0() {
//                    @Override
//                    public void call() {
//                        RxPresenter.this.callOnTakeView.remove(tick);
//                    }
//                }));
//                child.add(this);
//            }
//
//            @Override
//            public void onCompleted() {
//                deliverCompleted = true;
//                tick.call();
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                error = throwable;
//                deliverError = true;
//                tick.call();
//            }
//
//            @Override
//            public void onNext(T value) {
//                if (keepLatestOnly)
//                    deliverNext.clear();
//                deliverNext.add(value);
//                tick.call();
//            }
//        };
//    }
}
