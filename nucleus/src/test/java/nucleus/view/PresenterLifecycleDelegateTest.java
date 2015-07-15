package nucleus.view;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import mocks.BundleMock;
import nucleus.factory.PresenterFactory;
import nucleus.factory.PresenterStorage;
import nucleus.presenter.Presenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PresenterLifecycleDelegate.class)
public class PresenterLifecycleDelegateTest {

    PresenterStorage storage;
    PresenterFactory<?> factory;
    Presenter presenter;

    Presenter.OnDestroyListener onDestroyListener;

    @Before
    public void setUp() throws Exception {
        PowerMockito.whenNew(Bundle.class).withNoArguments().thenAnswer(new Answer<Bundle>() {
            @Override
            public Bundle answer(InvocationOnMock invocation) throws Throwable {
                return BundleMock.mock();
            }
        });

        storage = mock(PresenterStorage.class);
        Whitebox.setInternalState(PresenterStorage.class, "INSTANCE", storage);

        presenter = mock(Presenter.class);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return onDestroyListener = (Presenter.OnDestroyListener)invocation.getArguments()[0];
            }
        }).when(presenter).addOnDestroyListener(any(Presenter.OnDestroyListener.class));
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if (onDestroyListener != null)
                    onDestroyListener.onDestroy();
                return null;
            }
        }).when(presenter).onDestroy();

        factory = mock(PresenterFactory.class);
        when(factory.createPresenter()).thenReturn(presenter);
    }

    @Test
    public void testWithNullFactory() throws Exception {
        PresenterLifecycleDelegate<?> delegate = new PresenterLifecycleDelegate<>(null);
        delegate.onRestoreInstanceState(BundleMock.mock());
        assertNull(delegate.getPresenterFactory());
        assertNull(delegate.getPresenter());
        assertNotNull(delegate.onSaveInstanceState());
        delegate.setPresenterFactory(null);
        delegate.onResume(1);
        delegate.onPause(false);
        delegate.onPause(true);
    }

    @Test
    public void test() throws Exception {
        PresenterLifecycleDelegate<?> delegate = new PresenterLifecycleDelegate<>(factory);
        assertEquals(presenter, delegate.getPresenter());

    }
}
