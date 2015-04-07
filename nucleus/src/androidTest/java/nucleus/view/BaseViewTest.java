package nucleus.view;

import android.app.Activity;
import android.os.Bundle;
import android.test.UiThreadTest;

import junit.framework.Assert;

import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import nucleus.manager.PresenterManager;
import nucleus.presenter.Presenter;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseViewTest<ActivityType extends Activity> extends BaseActivityTest<ActivityType> {

    private Presenter mockPresenter;
    private PresenterManager mockPresenterManager;

    public BaseViewTest(Class<ActivityType> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dexmakerWorkaround();
                mockPresenter = Mockito.mock(Presenter.class);
                mockPresenterManager = Mockito.mock(PresenterManager.class);
                when(mockPresenterManager.provide(eq(getPresenterClass()), any(Bundle.class))).thenReturn(mockPresenter);
                PresenterManager.setInstance(mockPresenterManager);
            }
        });
        super.setUp();
    }

    @UiThreadTest
    public void testInit() {
        Assert.assertEquals(mockPresenter, getViewPresenter());
        assertProvideOnce();
        verify(mockPresenter, times(1)).takeView(getView());
    }

    protected void assertProvideOnce() {
        verify(mockPresenterManager, times(1)).provide(eq(getPresenterClass()), isNull(Bundle.class));
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
                verify(mockPresenterManager, times(1)).destroy(mockPresenter);
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
                when(mockPresenterManager.save(any(Presenter.class))).thenReturn(state);
            }
        });
        restartActivity();
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                verify(mockPresenter, times(1)).takeView(getView());
                verify(mockPresenterManager, times(1)).save(mockPresenter);
                verify(mockPresenterManager, times(1)).provide(eq(getPresenterClass()), argThat(new ArgumentMatcher<Bundle>() {
                    @Override
                    public boolean matches(Object o) {
                        return o != null && ((Bundle)o).getInt("1") == 1;
                    }
                }));
            }
        });
    }

    protected abstract Class<? extends Presenter> getPresenterClass();

    protected abstract Object getView();

    protected abstract Presenter getViewPresenter();
}
