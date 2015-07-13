package nucleus.presenter;

import android.os.Bundle;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import mocks.BundleMock;
import rx.Subscription;
import rx.functions.Func0;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class RxPresenterTest {

    @Test
    public void testAdd()  throws Exception {
        RxPresenter presenter = new RxPresenter();
        Subscription mock = Mockito.mock(Subscription.class);
        when(mock.isUnsubscribed()).thenReturn(false);
        presenter.add(mock);
        presenter.onDestroy();
        verify(mock, times(1)).unsubscribe();
        verify(mock, atLeastOnce()).isUnsubscribed();
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void testAddRemove()  throws Exception {
        RxPresenter presenter = new RxPresenter();
        Subscription mock = Mockito.mock(Subscription.class);
        when(mock.isUnsubscribed()).thenReturn(false);
        presenter.add(mock);
        presenter.remove(mock);
        verify(mock, atLeastOnce()).isUnsubscribed();
        verify(mock, times(1)).unsubscribe();
        presenter.onDestroy();
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void testRestartable() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.onCreate(null);

        Func0<Subscription> restartable = mock(Func0.class);
        Subscription subscription = mock(Subscription.class);
        when(restartable.call()).thenReturn(subscription);
        when(subscription.isUnsubscribed()).thenReturn(false);
        presenter.restartable(1, restartable);

        verifyNoMoreInteractions(restartable);

        presenter.start(1);

        verify(restartable, times(1)).call();
        verifyNoMoreInteractions(restartable);

        Bundle bundle = BundleMock.mock();
        presenter.onSave(bundle);

        presenter = new RxPresenter();
        presenter.onCreate(bundle);
        presenter.restartable(1, restartable);

        verify(restartable, times(2)).call();
        verifyNoMoreInteractions(restartable);
    }

    @Test
    public void testStopRestartable() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.onCreate(null);

        Func0<Subscription> restartable = mock(Func0.class);
        Subscription subscription = mock(Subscription.class);
        when(restartable.call()).thenReturn(subscription);
        when(subscription.isUnsubscribed()).thenReturn(false);
        presenter.restartable(1, restartable);

        verifyNoMoreInteractions(restartable);

        presenter.start(1);

        verify(restartable, times(1)).call();
        verifyNoMoreInteractions(restartable);

        presenter.stop(1);

        Bundle bundle = BundleMock.mock();
        presenter.onSave(bundle);

        presenter = new RxPresenter();
        presenter.onCreate(bundle);
        presenter.restartable(1, restartable);

        verify(restartable, times(1)).call();
        verifyNoMoreInteractions(restartable);
    }

    @Test
    public void testCompletedRestartable() throws Exception {
        Func0<Subscription> restartable = mock(Func0.class);
        Subscription subscription = mock(Subscription.class);

        RxPresenter presenter = new RxPresenter();
        presenter.onCreate(null);

        when(restartable.call()).thenReturn(subscription);
        when(subscription.isUnsubscribed()).thenReturn(true);
        presenter.restartable(1, restartable);

        verifyNoMoreInteractions(restartable);

        presenter.start(1);
    }

    @Test
    public void testViewObservable() {
        RxPresenter<Integer> presenter = new RxPresenter<>();
        presenter.onCreate(null);

        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        presenter.view().subscribe(testSubscriber);
        testSubscriber.assertValueCount(0);

        List<Integer> values = new ArrayList<>();

        presenter.onTakeView(1);
        values.add(1);
        assertValues(values, testSubscriber);

        presenter.onDropView();
        values.add(null);
        assertValues(values, testSubscriber);

        presenter.onTakeView(2);
        values.add(2);
        assertValues(values, testSubscriber);

        presenter.onDestroy();
        assertValues(values, testSubscriber);
        testSubscriber.assertCompleted();
    }

    private void assertValues(List<Integer> values, TestSubscriber<Integer> subscriber) {
        subscriber.assertValues(values.toArray(new Integer[values.size()]));
    }
}
