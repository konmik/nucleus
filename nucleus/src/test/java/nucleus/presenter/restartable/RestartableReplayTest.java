package nucleus.presenter.restartable;

import org.junit.Test;

import java.util.ArrayList;

import nucleus.presenter.delivery.DeliverReply;
import nucleus.presenter.delivery.Delivery;
import rx.Notification;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;
import rx.observers.TestObserver;
import rx.observers.TestSubscriber;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RestartableReplayTest {

    @Test
    public void testReplay() throws Exception {
        PublishSubject<Object> view = PublishSubject.create();
        final TestSubscriber<Delivery<Object, Integer>> testSubscriber = new TestSubscriber<>();
        final ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        final PublishSubject<Integer> subject = PublishSubject.create();
        DeliverReply<Object, Integer> restartable = new DeliverReply<>(view);
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

    private static final int PAGE_SIZE = 3;

    private Observable<String> requestPage(int pageNumber, int pageSize) {
        return Observable.range(pageNumber * pageSize, pageSize).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer integer) {
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
            .concatMap(new Func1<Integer, Observable<Integer>>() {
                @Override
                public Observable<Integer> call(Integer targetPage) {
                    return targetPage <= requestedPageCount ?
                        Observable.<Integer>never() :
                        Observable.range(requestedPageCount, targetPage - requestedPageCount);
                }
            })
            .doOnNext(new Action1<Integer>() {
                @Override
                public void call(Integer it) {
                    requestedPageCount = it + 1;
                }
            })
            .startWith(Observable.range(0, requestedPageCount))
            .concatMap(new Func1<Integer, Observable<String>>() {
                @Override
                public Observable<String> call(final Integer page) {
                    return requestPage(page, PAGE_SIZE);
                }
            })
            .compose(new DeliverReply<Object, String>(view))
            .subscribe(testObserver);

        ArrayList<Delivery<Object, String>> onNext = new ArrayList<>();

        testObserver.assertReceivedOnNext(onNext);

        view.onNext(999);
        addOnNext(onNext, 999, 0, 1, 2);

        testObserver.assertReceivedOnNext(onNext);

        nextPageRequests.onNext(2);
        addOnNext(onNext, 999, 3, 4, 5);

        testObserver.assertReceivedOnNext(onNext);

        view.onNext(null);

        assertEquals(0, testObserver.getOnCompletedEvents().size());
        testObserver.assertReceivedOnNext(onNext);

        nextPageRequests.onNext(3);

        assertEquals(0, testObserver.getOnCompletedEvents().size());
        testObserver.assertReceivedOnNext(onNext);

        view.onNext(9999);
        addOnNext(onNext, 9999, 0, 1, 2, 3, 4, 5, 6, 7, 8);

        assertEquals(0, testObserver.getOnCompletedEvents().size());
        testObserver.assertReceivedOnNext(onNext);
    }

    private void addOnNext(ArrayList<Delivery<Object, String>> onNext, Object view, int... values) {
        for (int value : values)
            onNext.add(new Delivery<>(view, Notification.createOnNext(Integer.toString(value))));
    }
}
