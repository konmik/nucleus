package nucleus.presenter;

import android.os.Bundle;
import android.test.InstrumentationTestCase;
import android.test.UiThreadTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

public class RxPresenterTest extends InstrumentationTestCase {

    private TestScheduler testScheduler;

    static class TestPresenter extends RxPresenter<TestView> {

    }

    @RequiresPresenter(TestPresenter.class)
    static class TestView {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            testScheduler = Schedulers.test();
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PresenterManager.clear();
                }
            });
        }
        catch (Throwable throwable) {
            throw new Exception(throwable);
        }
    }

    @UiThreadTest
    public void testImmediateQuery() throws Exception {
        TestPresenter presenter = PresenterManager.getInstance().provide(new TestView(), null);
        final AtomicInteger subscriptionCount = new AtomicInteger();
        registerImmediateQuery(presenter, subscriptionCount);
        assertEquals(0, subscriptionCount.get());
        presenter.subscribeQuery(1);
        assertEquals(1, subscriptionCount.get());

        Bundle bundle = PresenterManager.getInstance().save(presenter);
        PresenterManager.getInstance().destroy(presenter);
        presenter = PresenterManager.getInstance().provide(new TestView(), bundle);
        registerImmediateQuery(presenter, subscriptionCount);
        assertEquals(1, subscriptionCount.get());
    }

    private void registerImmediateQuery(TestPresenter presenter, final AtomicInteger subscriptionCount) {
        presenter.registerQuery(1, new Func0<Subscription>() {
            @Override
            public Subscription call() {
                subscriptionCount.incrementAndGet();
                return Observable.just(1).subscribe();
            }
        });
    }


    @UiThreadTest
    public void testInfiniteQuery() throws Exception {
        TestPresenter presenter = PresenterManager.getInstance().provide(new TestView(), null);
        final AtomicInteger subscriptionCount = new AtomicInteger();
        final AtomicInteger onNextCount = new AtomicInteger();
        AtomicInteger unsubscribeCounter = new AtomicInteger();
        registerInfiniteQuery(presenter, subscriptionCount, onNextCount, unsubscribeCounter);
        testScheduler.triggerActions();
        assertEquals(0, onNextCount.get());

        presenter.subscribeQuery(1);
        testScheduler.advanceTimeBy(1001, TimeUnit.MILLISECONDS);
        testScheduler.triggerActions();
        assertEquals(1, onNextCount.get());
        testScheduler.advanceTimeBy(1001, TimeUnit.MILLISECONDS);
        testScheduler.triggerActions();
        assertEquals(2, onNextCount.get());

        Bundle bundle = PresenterManager.getInstance().save(presenter);
        assertEquals(0, unsubscribeCounter.get());
        PresenterManager.getInstance().destroy(presenter);
        assertEquals(1, unsubscribeCounter.get());

        presenter = PresenterManager.getInstance().provide(new TestView(), bundle);
        registerInfiniteQuery(presenter, subscriptionCount, onNextCount, unsubscribeCounter);
        assertEquals(2, subscriptionCount.get());
        assertEquals(2, onNextCount.get());
        testScheduler.advanceTimeBy(1001, TimeUnit.MILLISECONDS);
        assertEquals(3, onNextCount.get());
    }

    private void registerInfiniteQuery(TestPresenter presenter, final AtomicInteger subscriptionCount, final AtomicInteger onNextCount, final AtomicInteger unsubscribeCounter) {
        presenter.registerQuery(1, new Func0<Subscription>() {
            @Override
            public Subscription call() {
                subscriptionCount.incrementAndGet();
                Subscription subscription = Observable.interval(1, TimeUnit.SECONDS, testScheduler).doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        unsubscribeCounter.incrementAndGet();
                    }
                }).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        onNextCount.incrementAndGet();
                    }
                });
                return subscription;
            }
        });
    }

    @UiThreadTest
    public void testDeliverOperator() throws Exception {
        TestPresenter presenter = PresenterManager.getInstance().provide(new TestView(), null);
        final AtomicLong onNextValue = new AtomicLong();
        final AtomicLong onNextCounter = new AtomicLong();
        final AtomicLong onErrorCounter = new AtomicLong();
        final AtomicLong onCompleteCounter = new AtomicLong();
        PublishSubject<Integer> bus = createBusSubscriptionWithOperator(presenter, onNextValue, onNextCounter, onErrorCounter, onCompleteCounter);
        bus.onNext(1);
        assertEquals(0, onNextValue.get());

        presenter.takeView(new TestView());
        assertEquals(1, onNextValue.get());
        bus.onNext(2);
        assertEquals(2, onNextValue.get());

        presenter.dropView();
        assertEquals(2, onNextValue.get());
        bus.onNext(3);
        assertEquals(2, onNextValue.get());

        bus.onCompleted();
        assertEquals(0, onErrorCounter.get());
        assertEquals(0, onCompleteCounter.get());
        presenter.takeView(new TestView());

        assertEquals(3, onNextValue.get());
        assertEquals(1, onCompleteCounter.get());

        presenter.dropView();
        presenter.takeView(new TestView());

        assertEquals(3, onNextValue.get());
        assertEquals(1, onCompleteCounter.get());

        presenter.dropView();

        onNextValue.set(0);
        onCompleteCounter.set(0);
        onErrorCounter.set(0);

        bus = createBusSubscriptionWithOperator(presenter, onNextValue, onNextCounter, onErrorCounter, onCompleteCounter);
        bus.onNext(4);
        Exception exception = new Exception();
        bus.onError(exception);

        assertEquals(0, onNextValue.get());
        assertEquals(0, onCompleteCounter.get());
        assertEquals(0, onErrorCounter.get());

        presenter.takeView(new TestView());

        assertEquals(4, onNextValue.get());
        assertEquals(0, onCompleteCounter.get());
        assertEquals(1, onErrorCounter.get());
    }

    private PublishSubject<Integer> createBusSubscriptionWithOperator(TestPresenter presenter, final AtomicLong onNextValue, final AtomicLong onNextCounter, final AtomicLong onErrorCounter, final AtomicLong onCompleteCounter) {
        PublishSubject<Integer> bus = PublishSubject.create();
        bus.lift(presenter.<Integer>deliverLatestCache()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer o) {
                onNextValue.set(o);
                onNextCounter.incrementAndGet();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                onErrorCounter.incrementAndGet();
            }
        }, new Action0() {
            @Override
            public void call() {
                onCompleteCounter.incrementAndGet();
            }
        });
        return bus;
    }

    @UiThreadTest
    public void testDeliveryOnTake() {
        TestPresenter presenter = PresenterManager.getInstance().provide(new TestView(), null);
        final AtomicLong onNextValue = new AtomicLong();
        final AtomicLong onNextCounter = new AtomicLong();
        final AtomicLong onErrorCounter = new AtomicLong();
        final AtomicLong onCompleteCounter = new AtomicLong();
        PublishSubject<Integer> bus = createBusSubscriptionWithOperator(presenter, onNextValue, onNextCounter, onErrorCounter, onCompleteCounter);
        bus.onNext(1);
        presenter.takeView(new TestView());
        presenter.dropView();
        presenter.takeView(new TestView());
        assertEquals(2, onNextCounter.get());
    }
}
