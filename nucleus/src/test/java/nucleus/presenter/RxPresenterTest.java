package nucleus.presenter;

import android.os.Bundle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import mock.BundleMock;
import nucleus.presenter.restartable.Restartable;
import rx.Subscription;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class RxPresenterTest {

    @Test
    public void testRestartable() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.onCreate(null);

        Restartable restartable = mock(Restartable.class);
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
    public void testCompletedRestartable() throws Exception {
        Restartable restartable = mock(Restartable.class);
        Subscription subscription = mock(Subscription.class);

        RxPresenter presenter = new RxPresenter();
        presenter.onCreate(null);

        when(restartable.call()).thenReturn(subscription);
        when(subscription.isUnsubscribed()).thenReturn(true);
        presenter.restartable(1, restartable);

        verifyNoMoreInteractions(restartable);

        presenter.start(1);
    }
}
