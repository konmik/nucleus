package nucleus.presenter.restartable;

import junit.framework.TestCase;

import java.util.ArrayList;

import rx.Notification;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

public class DeliveryOnceTransformerTest extends TestCase {

    public void testOnce() throws Exception {
        PublishSubject<Object> view = PublishSubject.create();
        final TestSubscriber<Delivery<Object, Integer>> testSubscriber = new TestSubscriber<>();
        ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        final PublishSubject<Integer> subject = PublishSubject.create();
        RestartableOnce<Object, Integer> restartable = new RestartableOnce<>(view,
            new Func0<Observable<Integer>>() {
                @Override
                public Observable<Integer> call() {
                    return subject;
                }
            },
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
            });
        Subscription subscription = restartable.call();

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
        testSubscriber.assertValues(deliveries.toArray(new Delivery[deliveries.size()]));

        subscription.unsubscribe();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }

    public void testOnceThrowable() throws Exception {
        PublishSubject<Object> view = PublishSubject.create();
        final TestSubscriber<Delivery<Object, Integer>> testSubscriber = new TestSubscriber<>();
        final ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        final PublishSubject<Integer> subject = PublishSubject.create();
        RestartableOnce<Object, Integer> restartable = new RestartableOnce<>(view,
            new Func0<Observable<Integer>>() {
                @Override
                public Observable<Integer> call() {
                    return subject;
                }
            },
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
            });
        Subscription subscription = restartable.call();

        // only first value is delivered
        Throwable throwable = new Throwable();
        subject.onError(throwable);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();

        view.onNext(100);
        deliveries.add(new Delivery<Object, Integer>(100, Notification.<Integer>createOnError(throwable)));

        testSubscriber.assertValueCount(1);

        // final checks
        testSubscriber.assertValues(deliveries.toArray(new Delivery[deliveries.size()]));

        subscription.unsubscribe();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }
}
