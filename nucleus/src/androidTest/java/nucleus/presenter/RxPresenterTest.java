package nucleus.presenter;

import android.os.Bundle;
import android.test.InstrumentationTestCase;
import android.test.UiThreadTest;
import android.util.Log;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import nucleus.manager.DefaultPresenterManager;
import nucleus.manager.PresenterManager;
import nucleus.manager.RequiresPresenter;
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

    public static class TestPresenter extends RxPresenter<TestView> {

    }

    @RequiresPresenter(TestPresenter.class)
    static class TestView {
    }

    AtomicLong onNextValue = new AtomicLong();
    AtomicLong onNextCounter = new AtomicLong();
    AtomicLong onErrorCounter = new AtomicLong();
    AtomicLong onCompleteCounter = new AtomicLong();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            testScheduler = Schedulers.test();
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PresenterManager.setInstance(new DefaultPresenterManager());
                    resetCounters();
                }
            });
        }
        catch (Throwable throwable) {
            throw new Exception(throwable);
        }
    }

    private void resetCounters() {
        onNextValue = new AtomicLong();
        onNextCounter = new AtomicLong();
        onErrorCounter = new AtomicLong();
        onCompleteCounter = new AtomicLong();
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
    public void testDeliverLatestCache() throws Exception {
        TestPresenter presenter = PresenterManager.getInstance().provide(new TestView(), null);
        PublishSubject<Integer> bus = createBusSubscriptionWithOperator(presenter.<Integer>deliverLatestCache());
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
        assertEquals(0, onCompleteCounter.get());

        presenter.dropView();
        presenter.takeView(new TestView());

        assertEquals(3, onNextValue.get());
        assertEquals(0, onCompleteCounter.get());

        presenter.dropView();

        onNextValue.set(0);
        onCompleteCounter.set(0);
        onErrorCounter.set(0);

        bus = createBusSubscriptionWithOperator(presenter.<Integer>deliverLatestCache());
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

    @UiThreadTest
    public void testDeliver() {
        TestPresenter presenter = PresenterManager.getInstance().provide(new TestView(), null);

        PublishSubject<Integer> bus = createBusSubscriptionWithOperator(presenter.<Integer>deliver());
        bus.onNext(100);
        bus.onNext(200);
        assertEquals(0, onNextCounter.get());
        presenter.takeView(new TestView());
        assertEquals(200, onNextValue.get());
        assertEquals(2, onNextCounter.get());
        presenter.dropView();
        presenter.takeView(new TestView());
        assertEquals(2, onNextCounter.get());

        for (int onComplete = 0; onComplete < 2; onComplete++) {
            resetCounters();
            presenter = PresenterManager.getInstance().provide(new TestView(), null);
            bus = createBusSubscriptionWithOperator(presenter.<Integer>deliver());
            bus.onNext(100);
            bus.onNext(200);
            if (onComplete == 1)
                bus.onCompleted();
            else
                bus.onError(new Exception());
            assertEquals(0, onNextCounter.get());
            assertEquals(0, onCompleteCounter.get());
            assertEquals(0, onErrorCounter.get());
            presenter.takeView(new TestView());
            assertEquals(200, onNextValue.get());
            assertEquals(2, onNextCounter.get());
            assertEquals(onComplete, onCompleteCounter.get());
            assertEquals(1 - onComplete, onErrorCounter.get());

            presenter.dropView();
            presenter.takeView(new TestView());
            assertEquals(2, onNextCounter.get());
            assertEquals(onComplete, onCompleteCounter.get());
            assertEquals(1 - onComplete, onErrorCounter.get());
        }
    }

    @UiThreadTest
    public void testDeliverLatest() {
        TestPresenter presenter = PresenterManager.getInstance().provide(new TestView(), null);

        PublishSubject<Integer> bus = createBusSubscriptionWithOperator(presenter.<Integer>deliverLatest());
        bus.onNext(100);
        bus.onNext(200);
        assertEquals(0, onNextCounter.get());
        presenter.takeView(new TestView());
        assertEquals(200, onNextValue.get());
        assertEquals(1, onNextCounter.get());
        presenter.dropView();
        presenter.takeView(new TestView());
        assertEquals(200, onNextValue.get());
        assertEquals(1, onNextCounter.get());

        for (int onComplete = 0; onComplete < 2; onComplete++) {
            resetCounters();
            presenter = PresenterManager.getInstance().provide(new TestView(), null);
            bus = createBusSubscriptionWithOperator(presenter.<Integer>deliverLatest());
            bus.onNext(100);
            bus.onNext(200);
            if (onComplete == 1)
                bus.onCompleted();
            else
                bus.onError(new Exception());
            assertEquals(0, onNextCounter.get());
            assertEquals(0, onCompleteCounter.get());
            assertEquals(0, onErrorCounter.get());
            presenter.takeView(new TestView());
            assertEquals(200, onNextValue.get());
            assertEquals(1, onNextCounter.get());
            assertEquals(onComplete, onCompleteCounter.get());
            assertEquals(1 - onComplete, onErrorCounter.get());

            presenter.dropView();
            presenter.takeView(new TestView());
            assertEquals(1, onNextCounter.get());
            assertEquals(onComplete, onCompleteCounter.get());
            assertEquals(1 - onComplete, onErrorCounter.get());
        }
    }

    private PublishSubject<Integer> createBusSubscriptionWithOperator(Observable.Transformer<Integer, Integer> operator) {
        PublishSubject<Integer> bus = PublishSubject.create();
        bus.compose(operator).subscribe(new Action1<Integer>() {
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
    public void testOperatorsLifecycle() {
        for (int type = 0; type < 3; type++) {
            // 0 = Deliver, 1 = DeliverLatest, 2 = DeliverLatestCache
            Log.v(getClass().getSimpleName(), String.format("type: %d", type));

            PresenterManager.setInstance(new DefaultPresenterManager());

            TestPresenter presenter = PresenterManager.getInstance().provide(new TestView(), null);
            //noinspection unchecked
            Observable.Transformer<Integer, Integer> operator =
                type == 0 ? presenter.<Integer>deliver() :
                    type == 1 ? presenter.<Integer>deliverLatest() :
                        presenter.<Integer>deliverLatestCache();

            TestOperator<Integer> operator1 = new TestOperator<>();
            TestOperator<Integer> operator2 = new TestOperator<>();
            PublishSubject<Integer> bus = PublishSubject.create();
            Subscription subscription = bus.lift(operator1).compose(operator).lift(operator2).subscribe(new Action1<Integer>() {
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

            assertTrue(bus.hasObservers());
            assertFalse(operator1.destinationSubscriber.isUnsubscribed());
            assertFalse(operator1.createdSubscriber.isUnsubscribed());
            assertFalse(operator2.destinationSubscriber.isUnsubscribed());
            assertFalse(operator2.createdSubscriber.isUnsubscribed());

            bus.onCompleted();

            assertTrue(!bus.hasObservers());
            assertFalse(operator1.destinationSubscriber.isUnsubscribed());
            assertFalse(operator1.createdSubscriber.isUnsubscribed());
            assertFalse(operator2.destinationSubscriber.isUnsubscribed());
            assertFalse(operator2.createdSubscriber.isUnsubscribed());

            presenter.takeView(new TestView());
            assertEquals(type != 2 ? false : true, presenter.viewStatusHasObservers());

            operator1.print("operator1");
            operator2.print("operator2");

            subscription.unsubscribe();

            operator1.print("operator1");
            operator2.print("operator2");
            assertFalse(presenter.viewStatusHasObservers());
        }
    }
}
