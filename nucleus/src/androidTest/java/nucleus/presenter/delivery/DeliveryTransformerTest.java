package nucleus.presenter.delivery;

import junit.framework.TestCase;

import java.util.ArrayList;

import rx.Notification;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

public class DeliveryTransformerTest extends TestCase {

    public void testOnce() throws Exception {
        PublishSubject<Object> view = PublishSubject.create();
        TestSubscriber<Delivery<Object, Integer>> testSubscriber = new TestSubscriber<>();
        ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        DeliveryTransformer<Object, Integer> transformer = new DeliveryTransformer<>(view, DeliveryTransformer.DeliveryRule.CACHE);
        PublishSubject<Integer> subject = PublishSubject.create();
        Subscription subscription = subject
            .compose(transformer)
            .subscribe(testSubscriber);

        // only latest value is delivered
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();

        view.onNext(100);
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(1)));

        testSubscriber.assertValueCount(1);
        testSubscriber.assertNotCompleted();

        // no values delivered if a view has been detached
        view.onNext(null);

        testSubscriber.assertValueCount(1);
        testSubscriber.assertNotCompleted();

        // the latest value will be delivered to the new view
        view.onNext(101);

        testSubscriber.assertValueCount(1);
        testSubscriber.assertNotCompleted();

        // a throwable will be delivered as well
        Throwable throwable = new Throwable();
        subject.onError(throwable);

        testSubscriber.assertValueCount(1);
        testSubscriber.assertNotCompleted();

        // the throwable will be delivered after a new view is attached
        view.onNext(102);

        testSubscriber.assertValueCount(1);
        testSubscriber.assertNotCompleted();

        // final checks
        testSubscriber.assertValues(deliveries.toArray(new Delivery[deliveries.size()]));

        subscription.unsubscribe();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }

    public void testCache() throws Exception {
        PublishSubject<Object> view = PublishSubject.create();
        TestSubscriber<Delivery<Object, Integer>> testSubscriber = new TestSubscriber<>();
        ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        DeliveryTransformer<Object, Integer> transformer = new DeliveryTransformer<>(view, DeliveryTransformer.DeliveryRule.CACHE);
        PublishSubject<Integer> subject = PublishSubject.create();
        Subscription subscription = subject
            .compose(transformer)
            .subscribe(testSubscriber);

        // only latest value is delivered
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();

        view.onNext(100);
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(3)));

        testSubscriber.assertValueCount(1);
        testSubscriber.assertNotCompleted();

        // no values delivered if a view has been detached
        view.onNext(null);

        testSubscriber.assertValueCount(1);
        testSubscriber.assertNotCompleted();

        // the latest value will be delivered to the new view
        view.onNext(101);
        deliveries.add(new Delivery<Object, Integer>(101, Notification.createOnNext(3)));

        testSubscriber.assertValueCount(2);
        testSubscriber.assertNotCompleted();

        // a throwable will be delivered as well
        Throwable throwable = new Throwable();
        subject.onError(throwable);
        deliveries.add(new Delivery<Object, Integer>(101, Notification.<Integer>createOnError(throwable)));

        testSubscriber.assertValueCount(3);
        testSubscriber.assertNotCompleted();

        // the throwable will be delivered after a new view is attached
        view.onNext(102);
        deliveries.add(new Delivery<Object, Integer>(102, Notification.<Integer>createOnError(throwable)));

        testSubscriber.assertValueCount(4);
        testSubscriber.assertNotCompleted();

        // final checks
        testSubscriber.assertValues(deliveries.toArray(new Delivery[deliveries.size()]));

        subscription.unsubscribe();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }
}
