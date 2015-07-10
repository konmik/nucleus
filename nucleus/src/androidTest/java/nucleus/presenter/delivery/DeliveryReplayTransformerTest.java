package nucleus.presenter.delivery;

import junit.framework.TestCase;

import java.util.ArrayList;

import rx.Notification;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

public class DeliveryReplayTransformerTest extends TestCase {

    public void testReplay() throws Exception {
        PublishSubject<Object> view = PublishSubject.create();
        TestSubscriber<Delivery<Object, Integer>> testSubscriber = new TestSubscriber<>();
        ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        DeliveryReplayTransformer<Object, Integer> transformer = new DeliveryReplayTransformer<>(view);
        PublishSubject<Integer> subject = PublishSubject.create();
        Subscription subscription = subject
            .compose(transformer)
            .subscribe(testSubscriber);

        // 1-3 values are delivered
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();

        view.onNext(100);
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(1)));
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(2)));
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(3)));

        testSubscriber.assertValueCount(3);
        testSubscriber.assertNotCompleted();

        // no values delivered if a view has been detached
        view.onNext(null);

        testSubscriber.assertValueCount(3);
        testSubscriber.assertNotCompleted();

        // all values will be be re-delivered to the new view
        view.onNext(101);
        deliveries.add(new Delivery<Object, Integer>(101, Notification.createOnNext(1)));
        deliveries.add(new Delivery<Object, Integer>(101, Notification.createOnNext(2)));
        deliveries.add(new Delivery<Object, Integer>(101, Notification.createOnNext(3)));

        testSubscriber.assertValueCount(6);
        testSubscriber.assertNotCompleted();

        // a throwable will be delivered as well
        Throwable throwable = new Throwable();
        subject.onError(throwable);
        deliveries.add(new Delivery<Object, Integer>(101, Notification.<Integer>createOnError(throwable)));

        testSubscriber.assertValueCount(7);
        testSubscriber.assertNotCompleted();

        // final checks
        testSubscriber.assertValues(deliveries.toArray(new Delivery[deliveries.size()]));

        subscription.unsubscribe();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }
}
