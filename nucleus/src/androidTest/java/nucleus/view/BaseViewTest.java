package nucleus.view;

import android.app.Activity;
import android.os.Bundle;
import android.test.UiThreadTest;

import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import nucleus.BaseActivityTest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

// can't share this class because of circular dependencies, so copy/paste it to each module
public abstract class BaseViewTest<ActivityType extends Activity> extends BaseActivityTest<ActivityType> {

    public BaseViewTest(Class<ActivityType> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dexmakerWorkaround();
            }
        });
        super.setUp();
    }

    @UiThreadTest
    public void testInit() {
        assertNotNull(getView().getPresenter());
        verify(getView().getPresenter(), times(1)).takeView(getView());
    }

    public void testDestroy() throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        });
        waitForDestructionComplete();
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                IllegalStateException exception = null;
                try {
                    verify(getView().getPresenter(), times(1)).destroy();
                }
                catch (IllegalStateException ex) {
                    exception = ex;
                }
                assertNotNull(exception);
            }
        });
    }

    // override in children classes of need to
    protected void waitForDestructionComplete() {
        getInstrumentation().waitForIdleSync();
    }

    public void testRestart() throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bundle state = new Bundle();
                state.putInt("1", 1);
                doAnswer(new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        Bundle bundle = (Bundle)invocation.getArguments()[1];
                        bundle.putInt("1", 1);
                        return null;
                    }
                }).when(getView().getPresenterFactory())
                    .savePresenter(eq(getView().getPresenter()), any(Bundle.class));
            }
        });
        restartActivity();
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                verify(getView().getPresenter(), times(1)).takeView(getView());
//                verify(getViewPresenter(), times(1)).save(any(Bundle.class));
                verify(getView().getPresenterFactory(), times(1)).providePresenter(argThat(new ArgumentMatcher<Bundle>() {
                    @Override
                    public boolean matches(Object o) {
                        return o != null && ((Bundle)o).getInt("1") == 1;
                    }
                }));
            }
        });
    }

    protected abstract ViewWithPresenter getView();
}
