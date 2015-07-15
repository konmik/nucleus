package nucleus.presenter;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class PresenterTest {

    @Test
    public void testOnDestroy() throws Exception {
        Presenter.OnDestroyListener listener = mock(Presenter.OnDestroyListener.class);
        Presenter presenter = new Presenter();
        presenter.create(null);
        presenter.addOnDestroyListener(listener);
        presenter.destroy();
        verify(listener, times(1)).onDestroy();
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testOnNoDestroy() throws Exception {
        Presenter.OnDestroyListener listener = mock(Presenter.OnDestroyListener.class);
        Presenter presenter = new Presenter();
        presenter.create(null);
        presenter.addOnDestroyListener(listener);
        presenter.removeOnDestroyListener(listener);
        presenter.destroy();
        verifyNoMoreInteractions(listener);
    }
}