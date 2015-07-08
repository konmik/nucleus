package nucleus.view;

import android.test.UiThreadTest;

import nucleus.BaseActivityTest;

public class NucleusActivityNoPresenterTest extends BaseActivityTest<NucleusActivityNoPresenterTestActivity> {
    public NucleusActivityNoPresenterTest() {
        super(NucleusActivityNoPresenterTestActivity.class);
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
        assertNull(getActivity().getPresenterFactory());
    }

    public void testDestroy() throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        });
        getInstrumentation().waitForIdleSync();
    }

    public void testRestart() throws Throwable {
        restartActivity();
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                assertNull(getActivity().getPresenterFactory());
            }
        });
    }
}
