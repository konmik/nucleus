package nucleus.presenter.delivery;

import org.junit.Test;

import java.util.ArrayList;

import rx.Notification;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertFalse;

public class DeliverCacheTest {

    @Test
    public void testCache() throws Exception {
        PublishSubject<Object> view = PublishSubject.create();
        final TestSubscriber<Delivery<Object, Integer>> testSubscriber = new TestSubscriber<>();
        final ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        final PublishSubject<Integer> subject = PublishSubject.create();
        DeliverLatestCache<Object, Integer> restartable = new DeliverLatestCache<>(view);
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
        testSubscriber.assertReceivedOnNext(deliveries);

        subscription.unsubscribe();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }

}
