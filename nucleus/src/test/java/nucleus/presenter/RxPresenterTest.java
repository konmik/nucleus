package nucleus.presenter;

import android.os.Bundle;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import mocks.BundleMock;
import nucleus.SilentCallable;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class RxPresenterTest {

    @Test
    public void testAdd()  throws Exception {
        RxPresenter presenter = new RxPresenter();
        Disposable mock = Mockito.mock(Disposable.class);
        when(mock.isDisposed()).thenReturn(false);
        presenter.add(mock);
        presenter.onDestroy();
        verify(mock, times(1)).dispose();
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void testAddRemove()  throws Exception {
        RxPresenter presenter = new RxPresenter();
        Disposable mock = Mockito.mock(Disposable.class);
        when(mock.isDisposed()).thenReturn(false);
        presenter.add(mock);
        presenter.remove(mock);
        verify(mock, times(1)).dispose();
        presenter.onDestroy();
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void testRestartable() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.create(null);

        SilentCallable<Disposable> restartable = mock(SilentCallable.class);
        Disposable subscription = mock(Disposable.class);
        when(restartable.call()).thenReturn(subscription);
        when(subscription.isDisposed()).thenReturn(false);
        presenter.restartable(1, restartable);

        verifyNoMoreInteractions(restartable);

        presenter.start(1);

        verify(restartable, times(1)).call();
        verifyNoMoreInteractions(restartable);

        Bundle bundle = BundleMock.mock();
        presenter.onSave(bundle);

        presenter = new RxPresenter();
        presenter.create(bundle);
        presenter.restartable(1, restartable);

        verify(restartable, times(2)).call();
        verifyNoMoreInteractions(restartable);
    }

    @Test
    public void testStopRestartable() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.onCreate(null);

        SilentCallable<Disposable> restartable = mock(SilentCallable.class);
        Disposable subscription = mock(Disposable.class);
        when(restartable.call()).thenReturn(subscription);
        when(subscription.isDisposed()).thenReturn(false);
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
        SilentCallable<Disposable> restartable = mock(SilentCallable.class);
        Disposable subscription = mock(Disposable.class);

        RxPresenter presenter = new RxPresenter();
        presenter.create(null);

        when(restartable.call()).thenReturn(subscription);
        when(subscription.isDisposed()).thenReturn(true);
        presenter.restartable(1, restartable);

        verifyNoMoreInteractions(restartable);

        presenter.start(1);
    }

    @Test
    public void testCompletedRestartableDoesNoRestart() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.onCreate(null);

        SilentCallable<Disposable> restartable = mock(SilentCallable.class);
        Disposable subscription = mock(Disposable.class);
        when(restartable.call()).thenReturn(subscription);
        when(subscription.isDisposed()).thenReturn(false);
        presenter.restartable(1, restartable);

        verifyNoMoreInteractions(restartable);

        presenter.start(1);

        verify(restartable, times(1)).call();
        verifyNoMoreInteractions(restartable);

        when(subscription.isDisposed()).thenReturn(true);
        Bundle bundle = BundleMock.mock();
        presenter.onSave(bundle);

        presenter = new RxPresenter();
        presenter.onCreate(bundle);
        presenter.restartable(1, restartable);

        verifyNoMoreInteractions(restartable);
    }

    @Test
    public void testRestartableIsUnsubscribed() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.create(null);

        SilentCallable<Disposable> restartable = mock(SilentCallable.class);
        Disposable subscription = mock(Disposable.class);
        when(restartable.call()).thenReturn(subscription);
        when(subscription.isDisposed()).thenReturn(false);

        presenter.restartable(1, restartable);
        assertTrue(presenter.isDisposed(1));
    }

    @Test
    public void testStartedRestartableIsNotUnsubscribed() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.create(null);

        SilentCallable<Disposable> restartable = mock(SilentCallable.class);
        Disposable subscription = mock(Disposable.class);
        when(restartable.call()).thenReturn(subscription);
        when(subscription.isDisposed()).thenReturn(false);

        presenter.restartable(1, restartable);
        assertTrue(presenter.isDisposed(1));
        presenter.start(1);
        assertFalse(presenter.isDisposed(1));
    }

    @Test
    public void testCompletedRestartableIsUnsubscribed() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.create(null);

        SilentCallable<Disposable> restartable = mock(SilentCallable.class);
        Disposable subscription = mock(Disposable.class);
        when(restartable.call()).thenReturn(subscription);
        when(subscription.isDisposed()).thenReturn(true);

        presenter.restartable(1, restartable);
        assertTrue(presenter.isDisposed(1));
        presenter.start(1);
        assertTrue(presenter.isDisposed(1));
    }
    
    @Test
    public void testViewObservable() {
        RxPresenter<Integer> presenter = new RxPresenter<>();
        presenter.onCreate(null);

        TestObserver<Integer> testObserver = new TestObserver<>();
        presenter.view().subscribe(testObserver);
        testObserver.assertValueCount(0);

        List<Integer> values = new ArrayList<>();

        presenter.onTakeView(1);
        values.add(1);
        assertValues(values, testObserver);

        presenter.onDropView();
        values.add(null);
        assertValues(values, testObserver);

        presenter.onTakeView(2);
        values.add(2);
        assertValues(values, testObserver);

        presenter.onDestroy();
        assertValues(values, testObserver);
        testObserver.assertComplete();
    }

    private void assertValues(List<Integer> values, TestObserver<Integer> observer) {
        observer.assertValues(values.toArray(new Integer[values.size()]));
    }
}
