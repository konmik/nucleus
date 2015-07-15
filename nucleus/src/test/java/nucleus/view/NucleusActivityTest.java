package nucleus.view;

import android.app.Activity;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import mocks.BundleMock;
import nucleus.factory.ReflectionPresenterFactory;
import nucleus.factory.RequiresPresenter;
import nucleus.presenter.Presenter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({NucleusActivityTest.TestActivity.class, PresenterLifecycleDelegate.class, ReflectionPresenterFactory.class})
public class NucleusActivityTest {

    public static class TestPresenter extends Presenter {

    }

    @RequiresPresenter(TestPresenter.class)
    public static class TestActivity extends NucleusActivity {
        @Override
        public void onDestroy() {
            super.onDestroy();
        }
    }

    private TestPresenter mockPresenter;
    private PresenterLifecycleDelegate mockDelegate;
    private ReflectionPresenterFactory mockFactory;
    private TestActivity tested;

    @Before
    public void setUp() throws Exception {
        mockPresenter = mock(TestPresenter.class);

        mockDelegate = mock(PresenterLifecycleDelegate.class);
        PowerMockito.whenNew(PresenterLifecycleDelegate.class).withAnyArguments().thenReturn(mockDelegate);
        when(mockDelegate.getPresenter()).thenReturn(mockPresenter);

        mockFactory = mock(ReflectionPresenterFactory.class);
        when(mockFactory.createPresenter()).thenReturn(mockPresenter);

        PowerMockito.mockStatic(ReflectionPresenterFactory.class);
        when(ReflectionPresenterFactory.fromViewClass(any(Class.class))).thenReturn(mockFactory);

        tested = spy(TestActivity.class);
        suppress(method(Activity.class, "onCreate", Bundle.class));
        suppress(method(Activity.class, "onSaveInstanceState", Bundle.class));
        suppress(method(Activity.class, "onResume"));
        suppress(method(Activity.class, "onPause"));
        stub(method(Activity.class, "isFinishing")).toReturn(false);
    }

    @Test
    public void testCreation() throws Exception {
        tested.onCreate(null);
        assertEquals(mockPresenter, tested.getPresenter());
        PowerMockito.verifyStatic(times(1));
        ReflectionPresenterFactory.fromViewClass(argThat(new ArgumentMatcher<Class<?>>() {
            @Override
            public boolean matches(Object argument) {
                return TestActivity.class.isAssignableFrom((Class)argument);
            }
        }));
        verify(mockDelegate, times(1)).getPresenter();
        verifyNoMoreInteractions(mockPresenter, mockDelegate, mockFactory);
    }

    @Test
    public void testLifecycle() throws Exception {
        tested.onCreate(null);
        tested.onResume();
        verify(mockDelegate, times(1)).onResume(tested);
        tested.onPause();
        verify(mockDelegate, times(1)).onPause(false);
        tested.onSaveInstanceState(BundleMock.mock());
        verify(mockDelegate, times(1)).onSaveInstanceState();
        verifyNoMoreInteractions(mockPresenter, mockDelegate, mockFactory);
    }

    @Test
    public void testSaveRestore() throws Exception {
        Bundle presenterBundle = BundleMock.mock();
        when(mockDelegate.onSaveInstanceState()).thenReturn(presenterBundle);

        tested.onCreate(null);

        Bundle state = BundleMock.mock();
        tested.onSaveInstanceState(state);

        tested = spy(TestActivity.class);
        tested.onCreate(state);
        verify(mockDelegate).onRestoreInstanceState(presenterBundle);
    }
}
