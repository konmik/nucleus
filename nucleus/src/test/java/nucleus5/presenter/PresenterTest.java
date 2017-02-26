package nucleus5.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.junit.Test;

import java.util.ArrayList;

import nucleus5.presenter.Presenter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class PresenterTest {

    static class TestPresenter extends Presenter<Object> {

        ArrayList<Bundle> onCreate = new ArrayList<>();
        ArrayList<Bundle> onSave = new ArrayList<>();
        ArrayList<Object> onTakeView = new ArrayList<>();
        int onDestroy, onDropView;

        @Override
        protected void onCreate(@Nullable Bundle savedState) {
            onCreate.add(savedState);
        }

        @Override
        protected void onDestroy() {
            onDestroy++;
        }

        @Override
        protected void onSave(Bundle state) {
            onSave.add(state);
        }

        @Override
        protected void onTakeView(Object o) {
            onTakeView.add(o);
        }

        @Override
        protected void onDropView() {
            onDropView++;
        }
    }

    @Test
    public void testLifecycle() throws Exception {
        TestPresenter presenter = new TestPresenter();
        Bundle bundle = mock(Bundle.class);

        presenter.create(bundle);
        assertEquals(bundle, presenter.onCreate.get(0));

        presenter.save(bundle);
        assertEquals(bundle, presenter.onSave.get(0));

        Object view = 1;
        presenter.takeView(view);
        assertEquals(view, presenter.onTakeView.get(0));

        presenter.dropView();
        assertEquals(1, presenter.onDropView);

        presenter.destroy();
        assertEquals(1, presenter.onDestroy);


        assertEquals(1, presenter.onCreate.size());
        assertEquals(1, presenter.onSave.size());
        assertEquals(1, presenter.onTakeView.size());
        assertEquals(1, presenter.onDropView);
        assertEquals(1, presenter.onDestroy);
    }

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