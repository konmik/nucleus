package nucleus5.presenter.delivery;

import org.junit.Test;

import java.util.ArrayList;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.observers.LambdaObserver;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import nucleus5.presenter.delivery.DeliverReplay;
import nucleus5.presenter.delivery.Delivery;
import nucleus5.view.OptionalView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DeliverReplayTest {

    @Test
    public void testReplay() throws Exception {
        PublishSubject<OptionalView<Object>> view = PublishSubject.create();
        final TestObserver<Delivery<Object, Integer>> testObserver = new TestObserver<>();
        final ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        final PublishSubject<Integer> subject = PublishSubject.create();
        DeliverReplay<Object, Integer> restartable = new DeliverReplay<>(view);

        LambdaObserver<Delivery<Object, Integer>> observer = new LambdaObserver<>(new Consumer<Delivery<Object, Integer>>() {
            @Override
            public void accept(Delivery<Object, Integer> delivery) throws Exception {
                delivery.split(
                    new BiConsumer<Object, Integer>() {
                        @Override
                        public void accept(Object o, Integer integer) throws Exception {
                            testObserver.onNext(new Delivery<>(o, Notification.createOnNext(integer)));
                        }
                    },
                    new BiConsumer<Object, Throwable>() {
                        @Override
                        public void accept(Object o, Throwable throwable) throws Exception {
                            testObserver.onNext(new Delivery<>(o, Notification.<Integer>createOnError(throwable)));
                        }
                    }
                );
            }
        }, Functions.ERROR_CONSUMER, Functions.EMPTY_ACTION, Functions.<Disposable>emptyConsumer());
        restartable.apply(subject)
            .subscribe(observer);

        // 1-3 values are delivered
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);

        testObserver.assertNotComplete();
        testObserver.assertNoValues();

        view.onNext(new OptionalView<Object>(100));
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(1)));
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(2)));
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(3)));

        testObserver.assertValueCount(3);
        testObserver.assertNotComplete();

        // no values delivered if a view has been detached
        view.onNext(new OptionalView<>(null));

        testObserver.assertValueCount(3);
        testObserver.assertNotComplete();

        // all values will be be re-delivered to the new view
        view.onNext(new OptionalView<Object>(101));
        deliveries.add(new Delivery<Object, Integer>(101, Notification.createOnNext(1)));
        deliveries.add(new Delivery<Object, Integer>(101, Notification.createOnNext(2)));
        deliveries.add(new Delivery<Object, Integer>(101, Notification.createOnNext(3)));

        testObserver.assertValueCount(6);
        testObserver.assertNotComplete();

        // a throwable will be delivered as well
        Throwable throwable = new Throwable();
        subject.onError(throwable);
        deliveries.add(new Delivery<Object, Integer>(101, Notification.<Integer>createOnError(throwable)));

        testObserver.assertValueCount(7);
        testObserver.assertNotComplete();

        // final checks
        testObserver.assertValueSequence(deliveries);

        observer.dispose();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }

    private static final int PAGE_SIZE = 3;

    private Observable<String> requestPage(int pageNumber, int pageSize) {
        return Observable.range(pageNumber * pageSize, pageSize).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return integer.toString();
            }
        });
    }

    private int requestedPageCount = 1;

    @Test
    public void testPagingCapabilities() {
        PublishSubject<OptionalView<Object>> view = PublishSubject.create();
        BehaviorSubject<Integer> nextPageRequests = BehaviorSubject.create();
        final TestObserver<Delivery<Object, String>> testObserver = new TestObserver<>();

        nextPageRequests
            .concatMap(new Function<Integer, ObservableSource<Integer>>() {
                @Override
                public ObservableSource<Integer> apply(Integer targetPage) throws Exception {
                    return targetPage <= requestedPageCount ?
                        Observable.<Integer>never() :
                        Observable.range(requestedPageCount, targetPage - requestedPageCount);
                }
            })
            .doOnNext(new Consumer<Integer>() {
                @Override
                public void accept(Integer it) throws Exception {
                    requestedPageCount = it + 1;
                }
            })
            .startWith(Observable.range(0, requestedPageCount))
            .concatMap(new Function<Integer, ObservableSource<String>>() {
                @Override
                public ObservableSource<String> apply(Integer page) throws Exception {
                    return requestPage(page, PAGE_SIZE);
                }
            })
            .compose(new DeliverReplay<Object, String>(view))
            .subscribe(testObserver);

        ArrayList<Delivery<Object, String>> onNext = new ArrayList<>();

        testObserver.assertValueSequence(onNext);

        view.onNext(new OptionalView<Object>(999));
        addOnNext(onNext, 999, 0, 1, 2);

        testObserver.assertValueSequence(onNext);

        nextPageRequests.onNext(2);
        addOnNext(onNext, 999, 3, 4, 5);

        testObserver.assertValueSequence(onNext);

        view.onNext(new OptionalView<>(null));

        assertEquals(0, testObserver.completions());
        testObserver.assertValueSequence(onNext);

        nextPageRequests.onNext(3);

        assertEquals(0, testObserver.completions());
        testObserver.assertValueSequence(onNext);

        view.onNext(new OptionalView<Object>(9999));
        addOnNext(onNext, 9999, 0, 1, 2, 3, 4, 5, 6, 7, 8);

        assertEquals(0, testObserver.completions());
        testObserver.assertValueSequence(onNext);
    }

    private void addOnNext(ArrayList<Delivery<Object, String>> onNext, Object view, int... values) {
        for (int value : values)
            onNext.add(new Delivery<>(view, Notification.createOnNext(Integer.toString(value))));
    }
}
