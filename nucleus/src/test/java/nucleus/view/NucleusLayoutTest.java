package nucleus.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.FrameLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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
import static org.powermock.api.support.membermodification.MemberMatcher.constructor;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({NucleusLayoutTest.TestView.class, PresenterLifecycleDelegate.class, ReflectionPresenterFactory.class})
public class NucleusLayoutTest {

    public static final Class<?> BASE_VIEW_CLASS = FrameLayout.class;
    public static final Class<TestView> VIEW_CLASS = TestView.class;

    public static class TestPresenter extends Presenter {
    }

    @RequiresPresenter(TestPresenter.class)
    public static class TestView extends NucleusLayout {
        public TestView() {
            super(null);
        }

        @Override
        public boolean isInEditMode() {
            return false;
        }
    }

    public void setUpIsFinishing(boolean b) {
        Activity activity = mock(Activity.class);
        when(activity.isFinishing()).thenReturn(b);
        stub(method(BASE_VIEW_CLASS, "getContext")).toReturn(activity);
    }

    private TestPresenter mockPresenter;
    private PresenterLifecycleDelegate mockDelegate;
    private ReflectionPresenterFactory mockFactory;
    private TestView tested;

    private void setUpPresenter() throws Exception {
        mockPresenter = mock(TestPresenter.class);

        PowerMockito.whenNew(Bundle.class).withNoArguments().thenAnswer(new Answer<Bundle>() {
            @Override
            public Bundle answer(InvocationOnMock invocation) throws Throwable {
                return BundleMock.mock();
            }
        });

        mockDelegate = mock(PresenterLifecycleDelegate.class);
        PowerMockito.whenNew(PresenterLifecycleDelegate.class).withAnyArguments().thenReturn(mockDelegate);
        when(mockDelegate.getPresenter()).thenReturn(mockPresenter);

        mockFactory = mock(ReflectionPresenterFactory.class);
        when(mockFactory.createPresenter()).thenReturn(mockPresenter);

        PowerMockito.mockStatic(ReflectionPresenterFactory.class);
        when(ReflectionPresenterFactory.fromViewClass(any(Class.class))).thenReturn(mockFactory);
    }

    @Before
    public void setUp() throws Exception {
        setUpPresenter();

        tested = spy(VIEW_CLASS);
        suppress(constructor(BASE_VIEW_CLASS, Context.class));
        suppress(method(BASE_VIEW_CLASS, "onRestoreInstanceState", Parcelable.class));
        suppress(method(BASE_VIEW_CLASS, "onSaveInstanceState"));
        suppress(method(BASE_VIEW_CLASS, "onAttachedToWindow"));
        suppress(method(BASE_VIEW_CLASS, "onDetachedFromWindow"));

        setUpIsFinishing(false);
    }

    @Test
    public void testCreation() throws Exception {
        assertEquals(mockPresenter, tested.getPresenter());
        PowerMockito.verifyStatic(times(1));
        ReflectionPresenterFactory.fromViewClass(argThat(new ArgumentMatcher<Class<?>>() {
            @Override
            public boolean matches(Object argument) {
                return TestView.class.isAssignableFrom((Class)argument);
            }
        }));
        verify(mockDelegate, times(1)).getPresenter();
        verifyNoMoreInteractions(mockPresenter, mockDelegate, mockFactory);
    }

    @Test
    public void testLifecycle() throws Exception {
        tested.onAttachedToWindow();
        verify(mockDelegate, times(1)).onResume(tested);
        tested.onDetachedFromWindow();
        verify(mockDelegate, times(1)).onDropView();
        verify(mockDelegate, times(1)).onDestroy(false);
        tested.onSaveInstanceState();
        verify(mockDelegate, times(1)).onSaveInstanceState();
        verifyNoMoreInteractions(mockPresenter, mockDelegate, mockFactory);
    }

    @Test
    public void testSaveRestore() throws Exception {
        Bundle presenterBundle = BundleMock.mock();
        when(mockDelegate.onSaveInstanceState()).thenReturn(presenterBundle);

        Bundle state = (Bundle)tested.onSaveInstanceState();

        tested = spy(TestView.class);
        tested.onRestoreInstanceState(state);
        verify(mockDelegate).onRestoreInstanceState(presenterBundle);
    }

    @Test
    public void testDestroy() throws Exception {
        setUpIsFinishing(true);
        tested.onDetachedFromWindow();
        verify(mockDelegate, times(1)).onDestroy(true);
    }

    @Test
    public void getActivityFromContext() throws Exception {
        Activity activity = mock(Activity.class);
        stub(method(BASE_VIEW_CLASS, "getContext")).toReturn(activity);
        assertEquals(activity, tested.getActivity());
    }

    @Test
    public void getActivityFromWrappedContext() throws Exception {
        Activity activity = mock(Activity.class);
        ContextWrapper wrapper = mock(ContextWrapper.class);
        when(wrapper.getBaseContext()).thenReturn(activity);
        ContextWrapper wrapper2 = mock(ContextWrapper.class);
        when(wrapper2.getBaseContext()).thenReturn(wrapper);
        stub(method(BASE_VIEW_CLASS, "getContext")).toReturn(wrapper2);
        assertEquals(activity, tested.getActivity());
    }
}
