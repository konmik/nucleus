package nucleus.presenter.delivery;

import junit.framework.TestCase;

import java.util.ArrayList;

import rx.Notification;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

public class DeliveryOnceTransformerTest extends TestCase {

    public void testOnce() throws Exception {
        PublishSubject<Object> view = PublishSubject.create();
        TestSubscriber<Delivery<Object, Integer>> testSubscriber = new TestSubscriber<>();
        ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        DeliveryOnceTransformer<Object, Integer> transformer = new DeliveryOnceTransformer<>(view);
        PublishSubject<Integer> subject = PublishSubject.create();
        Subscription subscription = subject
            .compose(transformer)
            .subscribe(testSubscriber);

        // only first value is delivered
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();

        view.onNext(100);
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(1)));

        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();

        // no values delivered if a view has been detached
        view.onNext(null);

        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();

        // the latest value will not be delivered to the new view
        view.onNext(101);

        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();

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
        TestSubscriber<Delivery<Object, Integer>> testSubscriber = new TestSubscriber<>();
        ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        DeliveryOnceTransformer<Object, Integer> transformer = new DeliveryOnceTransformer<>(view);
        PublishSubject<Integer> subject = PublishSubject.create();
        Subscription subscription = subject
            .compose(transformer)
            .subscribe(testSubscriber);

        // only first value is delivered
        Throwable throwable = new Throwable();
        subject.onError(throwable);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();

        view.onNext(100);
        deliveries.add(new Delivery<Object, Integer>(100, Notification.<Integer>createOnError(throwable)));

        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();

        // final checks
        testSubscriber.assertValues(deliveries.toArray(new Delivery[deliveries.size()]));

        subscription.unsubscribe();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }
}
