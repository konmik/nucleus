package nucleus.view;

import android.os.Bundle;
import android.test.UiThreadTest;

import org.mockito.Mockito;

import nucleus.manager.PresenterManager;
import nucleus.presenter.Presenter;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NucleusActivityNoPresenterTest extends BaseActivityTest<NucleusActivityNoPresenterTestActivity> {
    public NucleusActivityNoPresenterTest() {
        super(NucleusActivityNoPresenterTestActivity.class);
    }

    private Presenter mockPresenter;
    private PresenterManager mockPresenterManager;

    @Override
    public void setUp() throws Exception {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dexmakerWorkaround();
                mockPresenter = Mockito.mock(Presenter.class);
                mockPresenterManager = mock(PresenterManager.class);
                when(mockPresenterManager.provide(eq(Presenter.class), any(Bundle.class))).thenReturn(mockPresenter);
                PresenterManager.setInstance(mockPresenterManager);
            }
        });
        super.setUp();
    }

    @UiThreadTest
    public void testInit() {
        verify(mockPresenterManager, times(0)).provide(any(Class.class), isNull(Bundle.class));
    }

    public void testDestroy() throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        });
        getInstrumentation().waitForIdleSync();
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                verify(mockPresenterManager, times(0)).destroy(mockPresenter);
            }
        });
    }

    public void testRestart() throws Throwable {
        restartActivity();
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                verify(mockPresenterManager, times(0)).save(any(Presenter.class));
                verify(mockPresenterManager, times(0)).provide(any(Class.class), isNull(Bundle.class));
            }
        });
    }
}
