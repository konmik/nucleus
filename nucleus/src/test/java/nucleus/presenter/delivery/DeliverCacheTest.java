package nucleus.presenter.delivery;

import org.junit.Test;

import java.util.ArrayList;

import io.reactivex.Notification;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertFalse;

public class DeliverCacheTest {

    @Test
    public void testCache() throws Exception {
        PublishSubject<Object> view = PublishSubject.create();
        final TestObserver<Delivery<Object, Integer>> testObserver = new TestObserver<>();
        final ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        final PublishSubject<Integer> subject = PublishSubject.create();
        DeliverLatestCache<Object, Integer> restartable = new DeliverLatestCache<>(view);
        Disposable subscription = restartable.apply(subject)
            .subscribe(new Consumer<Delivery<Object, Integer>>() {
                @Override
                public void accept(Delivery<Object, Integer> delivery) throws Exception {
                    delivery.split(
                        new BiConsumer<Object, Integer>() {
                            @Override
                            public void accept(Object o, Integer integer) {
                                testObserver.onNext(new Delivery<>(o, Notification.createOnNext(integer)));
                            }
                        },
                        new BiConsumer<Object, Throwable>() {
                            @Override
                            public void accept(Object o, Throwable throwable) {
                                testObserver.onNext(new Delivery<>(o, Notification.<Integer>createOnError(throwable)));
                            }
                        }
                    );
                }
            });

        // only latest value is delivered
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);

        testObserver.assertNotComplete();
        testObserver.assertNoValues();

        view.onNext(100);
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(3)));

        testObserver.assertValueCount(1);
        testObserver.assertNotComplete();

        // no values delivered if a view has been detached
        view.onNext(null);

        testObserver.assertValueCount(1);
        testObserver.assertNotComplete();

        // the latest value will be delivered to the new view
        view.onNext(101);
        deliveries.add(new Delivery<Object, Integer>(101, Notification.createOnNext(3)));

        testObserver.assertValueCount(2);
        testObserver.assertNotComplete();

        // a throwable will be delivered as well
        Throwable throwable = new Throwable();
        subject.onError(throwable);
        deliveries.add(new Delivery<Object, Integer>(101, Notification.<Integer>createOnError(throwable)));

        testObserver.assertValueCount(3);
        testObserver.assertNotComplete();

        // the throwable will be delivered after a new view is attached
        view.onNext(102);
        deliveries.add(new Delivery<Object, Integer>(102, Notification.<Integer>createOnError(throwable)));

        testObserver.assertValueCount(4);
        testObserver.assertNotComplete();

        // final checks
        testObserver.assertValueSequence(deliveries);

        subscription.dispose();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }

}
