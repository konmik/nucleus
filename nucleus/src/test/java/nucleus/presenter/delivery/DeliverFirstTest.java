package nucleus.presenter.delivery;

import org.junit.Test;

import java.util.ArrayList;

import rx.Notification;
import rx.Subscription;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertFalse;

public class DeliverFirstTest {

    @Test
    public void testOnce() throws Exception {
        PublishSubject<Object> view = PublishSubject.create();
        final TestSubscriber<Delivery<Object, Integer>> testSubscriber = new TestSubscriber<>();
        ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        final PublishSubject<Integer> subject = PublishSubject.create();
        DeliverFirst<Object, Integer> restartable = new DeliverFirst<>(view);
        Subscription subscription = restartable.call(subject)
            .subscribe(new Action1<Delivery<Object, Integer>>() {
                @Override
                public void call(Delivery<Object, Integer> delivery) {
                    delivery.split(
                        new Action2<Object, Integer>() {
                            @Override
                            public void call(Object o, Integer integer) {
                                testSubscriber.onNext(new Delivery<>(o, Notification.createOnNext(integer)));
                            }
                        },
                        new Action2<Object, Throwable>() {
                            @Override
                            public void call(Object o, Throwable throwable) {
                                testSubscriber.onNext(new Delivery<>(o, Notification.<Integer>createOnError(throwable)));
                            }
                        }
                    );
                }
            });

        // only first value is delivered
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();

        view.onNext(100);
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(1)));

        testSubscriber.assertValueCount(1);

        // no values delivered if a view has been detached
        view.onNext(null);

        testSubscriber.assertValueCount(1);

        // the latest value will not be delivered to the new view
        view.onNext(101);

        testSubscriber.assertValueCount(1);

        // successive values will be ignored
        subject.onNext(4);

        testSubscriber.assertValueCount(1);

        // final checks
        testSubscriber.assertReceivedOnNext(deliveries);

        subscription.unsubscribe();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }

    @Test
    public void testOnceThrowable() throws Exception {
        PublishSubject<Object> view = PublishSubject.create();
        final TestSubscriber<Delivery<Object, Integer>> testSubscriber = new TestSubscriber<>();
        final ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        final PublishSubject<Integer> subject = PublishSubject.create();
        DeliverFirst<Object, Integer> restartable = new DeliverFirst<>(view);
        Subscription subscription = restartable.call(subject)
            .subscribe(new Action1<Delivery<Object, Integer>>() {
                @Override
                public void call(Delivery<Object, Integer> delivery) {
                    delivery.split(
                        new Action2<Object, Integer>() {
                            @Override
                            public void call(Object o, Integer integer) {
                                testSubscriber.onNext(new Delivery<>(o, Notification.createOnNext(integer)));
                            }
                        },
                        new Action2<Object, Throwable>() {
                            @Override
                            public void call(Object o, Throwable throwable) {
                                testSubscriber.onNext(new Delivery<>(o, Notification.<Integer>createOnError(throwable)));
                            }
                        }
                    );
                }
            });

        // only first value is delivered
        Throwable throwable = new Throwable();
        subject.onError(throwable);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();

        view.onNext(100);
        deliveries.add(new Delivery<Object, Integer>(100, Notification.<Integer>createOnError(throwable)));

        testSubscriber.assertValueCount(1);

        // final checks
        testSubscriber.assertReceivedOnNext(deliveries);

        subscription.unsubscribe();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }

    //  https://github.com/ReactiveX/RxJava/issues/3182
    @Test(expected = OnErrorNotImplementedException.class)
    public void testThrowDuringOnNext() throws Exception {

//        Observable
//            .just(1)
//            .filter(new Func1<Integer, Boolean>() {
//                @Override
//                public Boolean call(Integer integer) {
//                    return true;
//                }
//            })
//            .first()
//            .subscribe(new Action1<Integer>() {
//                @Override
//                public void call(Integer integer) {
//                    throw new RuntimeException();
//                }
//            });

        PublishSubject<Object> view = PublishSubject.create();

        final PublishSubject<Integer> subject = PublishSubject.create();
        new DeliverFirst<Object, Integer>(view)
            .call(subject)
            .subscribe(new Action1<Delivery<Object, Integer>>() {
                @Override
                public void call(Delivery<Object, Integer> delivery) {
                    delivery.split(
                        new Action2<Object, Integer>() {
                            @Override
                            public void call(Object o, Integer integer) {
                                throw new RuntimeException();
                            }
                        },
                        null
                    );
                }
            });

        subject.onNext(3);
        view.onNext(100);
    }
}
