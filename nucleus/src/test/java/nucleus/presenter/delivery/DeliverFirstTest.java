package nucleus.presenter.delivery;

import org.junit.Test;

import java.util.ArrayList;

import io.reactivex.Notification;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;

import static org.junit.Assert.assertFalse;

public class DeliverFirstTest {

    @Test
    public void testOnce() throws Exception {
        PublishSubject<Object> view = PublishSubject.create();
        final TestSubscriber<Delivery<Object, Integer>> testSubscriber = new TestSubscriber<>();
        ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        final PublishSubject<Integer> subject = PublishSubject.create();
        DeliverFirst<Object, Integer> restartable = new DeliverFirst<>(view);
        Disposable subscription = restartable.apply(subject)
            .subscribe(new Consumer<Delivery<Object, Integer>>() {
                @Override
                public void accept(Delivery<Object, Integer> delivery) throws Exception {
                    delivery.split(
                        new BiConsumer<Object, Integer>() {
                            @Override
                            public void accept(Object o, Integer integer) {
                                testSubscriber.onNext(new Delivery<>(o, Notification.createOnNext(integer)));
                            }
                        },
                        new BiConsumer<Object, Throwable>() {
                            @Override
                            public void accept(Object o, Throwable throwable) {
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

        testSubscriber.assertNotComplete();
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
        testSubscriber.assertValueSequence(deliveries);

        subscription.dispose();
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
        Disposable subscription = restartable.apply(subject)
            .subscribe(new Consumer<Delivery<Object, Integer>>() {
                @Override
                public void accept(Delivery<Object, Integer> delivery) throws Exception {
                    delivery.split(
                        new BiConsumer<Object, Integer>() {
                            @Override
                            public void accept(Object o, Integer integer) {
                                testSubscriber.onNext(new Delivery<>(o, Notification.createOnNext(integer)));
                            }
                        },
                        new BiConsumer<Object, Throwable>() {
                            @Override
                            public void accept(Object o, Throwable throwable) {
                                testSubscriber.onNext(new Delivery<>(o, Notification.<Integer>createOnError(throwable)));
                            }
                        }
                    );
                }
            });

        // only first value is delivered
        Throwable throwable = new Throwable();
        subject.onError(throwable);

        testSubscriber.assertNotComplete();
        testSubscriber.assertNoValues();

        view.onNext(100);
        deliveries.add(new Delivery<Object, Integer>(100, Notification.<Integer>createOnError(throwable)));

        testSubscriber.assertValueCount(1);

        // final checks
        testSubscriber.assertValueSequence(deliveries);

        subscription.dispose();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }

    //  https://github.com/ReactiveX/RxJava/issues/3182
    @Test(expected = RuntimeException.class)
    public void testThrowDuringOnNext() throws Exception {

        PublishSubject<Object> view = PublishSubject.create();

        final PublishSubject<Integer> subject = PublishSubject.create();
        new DeliverFirst<Object, Integer>(view)
            .apply(subject)
            .subscribe(new Consumer<Delivery<Object, Integer>>() {
                @Override
                public void accept(Delivery<Object, Integer> delivery) throws Exception {
                    delivery.split(
                        new BiConsumer<Object, Integer>() {
                            @Override
                            public void accept(Object o, Integer integer) {
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
