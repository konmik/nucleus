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

import nucleus.factory.ReflectionPresenterFactory;
import nucleus.factory.RequiresPresenter;
import nucleus.presenter.Presenter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({NucleusActivityTest.TestActivity.class, ReflectionPresenterFactory.class})
public class NucleusActivityTest {

    public static class TestPresenter extends Presenter {

    }

    @RequiresPresenter(TestPresenter.class)
    public static class TestActivity extends NucleusActivity {

    }

    private TestPresenter mockPresenter;
    private ReflectionPresenterFactory mockFactory;
    private TestActivity tested;

    @Before
    public void setUp() throws Exception {
        mockPresenter = mock(TestPresenter.class);

        mockFactory = mock(ReflectionPresenterFactory.class);
        when(mockFactory.providePresenter(null)).thenReturn(mockPresenter);
        PowerMockito.mockStatic(ReflectionPresenterFactory.class);
        when(ReflectionPresenterFactory.fromViewClass(any(Class.class))).thenReturn(mockFactory);

        tested = spy(TestActivity.class);
        suppress(method(Activity.class, "onCreate", Bundle.class));
        suppress(method(Activity.class, "onSaveInstanceState", Bundle.class));
        suppress(method(Activity.class, "onResume"));
        suppress(method(Activity.class, "onPause"));
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
        verify(mockFactory, times(1)).providePresenter(null);
        verifyNoMoreInteractions(mockPresenter, mockFactory);
    }
}
