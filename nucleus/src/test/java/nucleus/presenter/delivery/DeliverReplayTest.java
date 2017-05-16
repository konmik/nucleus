package nucleus.presenter.delivery;

import org.junit.Test;

import java.util.ArrayList;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;

import static org.junit.Assert.assertFalse;

public class DeliverReplayTest {

    @Test
    public void testReplay() throws Exception {
        PublishSubject<Object> view = PublishSubject.create();
        final TestSubscriber<Delivery<Object, Integer>> testSubscriber = new TestSubscriber<>();
        final ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        final PublishSubject<Integer> subject = PublishSubject.create();
        DeliverReplay<Object, Integer> restartable = new DeliverReplay<>(view);
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

        // 1-3 values are delivered
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);

        testSubscriber.assertNotComplete();
        testSubscriber.assertNoValues();

        view.onNext(100);
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(1)));
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(2)));
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(3)));

        testSubscriber.assertValueCount(3);
        testSubscriber.assertNotComplete();

        // no values delivered if a view has been detached
        view.onNext(null);

        testSubscriber.assertValueCount(3);
        testSubscriber.assertNotComplete();

        // all values will be be re-delivered to the new view
        view.onNext(101);
        deliveries.add(new Delivery<Object, Integer>(101, Notification.createOnNext(1)));
        deliveries.add(new Delivery<Object, Integer>(101, Notification.createOnNext(2)));
        deliveries.add(new Delivery<Object, Integer>(101, Notification.createOnNext(3)));

        testSubscriber.assertValueCount(6);
        testSubscriber.assertNotComplete();

        // a throwable will be delivered as well
        Throwable throwable = new Throwable();
        subject.onError(throwable);
        deliveries.add(new Delivery<Object, Integer>(101, Notification.<Integer>createOnError(throwable)));

        testSubscriber.assertValueCount(7);
        testSubscriber.assertNotComplete();

        // final checks
        testSubscriber.assertValueSequence(deliveries);

        subscription.dispose();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }

    private static final int PAGE_SIZE = 3;

    private Observable<String> requestPage(int pageNumber, int pageSize) {
        return Observable.range(pageNumber * pageSize, pageSize).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) {
                return integer.toString();
            }
        });
    }

    private int requestedPageCount = 1;

    @Test
    public void testPagingCapabilities() {
        PublishSubject<Object> view = PublishSubject.create();
        BehaviorSubject<Integer> nextPageRequests = BehaviorSubject.create();
        final TestObserver<Delivery<Object, String>> testObserver = new TestObserver<>();

        nextPageRequests
            .concatMap(new Function<Integer, Observable<Integer>>() {
                @Override
                public Observable<Integer> apply(Integer targetPage) {
                    return targetPage <= requestedPageCount ?
                        Observable.<Integer>never() :
                        Observable.range(requestedPageCount, targetPage - requestedPageCount);
                }
            })
            .doOnNext(new Consumer<Integer>() {
                @Override
                public void accept(Integer it) {
                    requestedPageCount = it + 1;
                }
            })
            .startWith(Observable.range(0, requestedPageCount))
            .concatMap(new Function<Integer, Observable<String>>() {
                @Override
                public Observable<String> apply(final Integer page) {
                    return requestPage(page, PAGE_SIZE);
                }
            })
            .compose(new DeliverReplay<Object, String>(view))
            .subscribe(testObserver);

        ArrayList<Delivery<Object, String>> onNext = new ArrayList<>();

        testObserver.assertValueSequence(onNext);

        view.onNext(999);
        addOnNext(onNext, 999, 0, 1, 2);

        testObserver.assertValueSequence(onNext);

        nextPageRequests.onNext(2);
        addOnNext(onNext, 999, 3, 4, 5);

        testObserver.assertValueSequence(onNext);

        view.onNext(null);

        testObserver.assertNotComplete();
        testObserver.assertValueSequence(onNext);

        nextPageRequests.onNext(3);

        testObserver.assertNotComplete();
        testObserver.assertValueSequence(onNext);

        view.onNext(9999);
        addOnNext(onNext, 9999, 0, 1, 2, 3, 4, 5, 6, 7, 8);

        testObserver.assertNotComplete();
        testObserver.assertValueSequence(onNext);
    }

    private void addOnNext(ArrayList<Delivery<Object, String>> onNext, Object view, int... values) {
        for (int value : values)
            onNext.add(new Delivery<>(view, Notification.createOnNext(Integer.toString(value))));
    }
}
