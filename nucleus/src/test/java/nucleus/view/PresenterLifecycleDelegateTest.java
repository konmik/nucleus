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

import java.util.ArrayList;

import mocks.BundleMock;
import nucleus.factory.PresenterFactory;
import nucleus.factory.PresenterStorage;
import nucleus.presenter.Presenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PresenterLifecycleDelegate.class, PresenterStorage.class})
public class PresenterLifecycleDelegateTest {

    PresenterStorage storage;
    PresenterFactory<Presenter> factory;
    ArrayList<Presenter> presenters = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        PowerMockito.whenNew(Bundle.class).withNoArguments().thenAnswer(new Answer<Bundle>() {
            @Override
            public Bundle answer(InvocationOnMock invocation) throws Throwable {
                return BundleMock.mock();
            }
        });

        storage = mockStorage();

        factory = mock(PresenterFactory.class);
        when(factory.createPresenter()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return mockPresenter();
            }
        });
    }

    Presenter mockPresenter() {
        Presenter presenter = mock(Presenter.class);

        final ArrayList<Presenter.OnDestroyListener> onDestroyListeners = new ArrayList<>();

        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                onDestroyListeners.add((Presenter.OnDestroyListener)invocation.getArguments()[0]);
                return null;
            }
        }).when(presenter).addOnDestroyListener(any(Presenter.OnDestroyListener.class));
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                for (Presenter.OnDestroyListener listener : onDestroyListeners)
                    listener.onDestroy();
                return null;
            }
        }).when(presenter).destroy();
        return presenter;
    }

    // PresenterStorage should be prepared with @PrepareForTest
    public PresenterStorage mockStorage() {
        PresenterStorage storage = mock(PresenterStorage.class);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                presenters.add((Presenter)invocation.getArguments()[0]);
                return null;
            }
        }).when(storage).add(any(Presenter.class));
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return "" + presenters.indexOf(invocation.getArguments()[0]);
            }
        }).when(storage).getId(any(Presenter.class));
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return presenters.get(Integer.parseInt((String)invocation.getArguments()[0]));
            }
        }).when(storage).getPresenter(anyString());
        Whitebox.setInternalState(PresenterStorage.class, "INSTANCE", storage);
        return storage;
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
    public void twoWaysOfFactoryInjection() throws Exception {
        PresenterLifecycleDelegate<Presenter> delegate = new PresenterLifecycleDelegate<>(factory);
        Presenter presenter = delegate.getPresenter();
        assertEquals(presenters.get(0), presenter);

        delegate = new PresenterLifecycleDelegate<>(null);
        delegate.setPresenterFactory(factory);
        presenter = delegate.getPresenter();
        assertEquals(presenters.get(1), presenter);
    }

    @Test
    public void saveRestore() throws Exception {
        PresenterLifecycleDelegate<Presenter> delegate = new PresenterLifecycleDelegate<>(factory);
        delegate.onResume(1);
        Bundle bundle = delegate.onSaveInstanceState();

        delegate = new PresenterLifecycleDelegate<>(factory);
        delegate.onRestoreInstanceState(bundle);
        assertEquals(presenters.get(0), delegate.getPresenter());

        delegate = new PresenterLifecycleDelegate<>(factory);
        assertNotEquals(presenters.get(0), delegate.getPresenter());
    }

    @Test
    public void saveNoResume() throws Exception {
        PresenterLifecycleDelegate<Presenter> delegate = new PresenterLifecycleDelegate<>(factory);
        Bundle bundle = delegate.onSaveInstanceState();

        delegate = new PresenterLifecycleDelegate<>(factory);
        delegate.onRestoreInstanceState(bundle);
        assertEquals(presenters.get(0), delegate.getPresenter());

        delegate = new PresenterLifecycleDelegate<>(factory);
        assertNotEquals(presenters.get(0), delegate.getPresenter());
    }
}
