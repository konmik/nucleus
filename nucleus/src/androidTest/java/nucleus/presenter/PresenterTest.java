package nucleus.presenter;

import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class PresenterTest extends TestCase {

    public void testOnDestroy() throws Exception {
        Presenter.OnDestroyListener listener = mock(Presenter.OnDestroyListener.class);
        Presenter presenter = new Presenter();
        presenter.onCreate(null);
        presenter.addOnDestroyListener(listener);
        presenter.onDestroy();
        verify(listener, times(1)).onDestroy();
        verifyNoMoreInteractions(listener);
    }

    public void testOnNoDestroy() throws Exception {
        Presenter.OnDestroyListener listener = mock(Presenter.OnDestroyListener.class);
        Presenter presenter = new Presenter();
        presenter.onCreate(null);
        presenter.addOnDestroyListener(listener);
        presenter.removeOnDestroyListener(listener);
        presenter.onDestroy();
        verifyNoMoreInteractions(listener);
    }
}