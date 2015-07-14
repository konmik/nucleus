package nucleus;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class BaseActivityTest<ActivityClass extends Activity> extends ActivityInstrumentationTestCase2<ActivityClass> {

    public BaseActivityTest(Class<ActivityClass> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public interface Condition {
        boolean call();
    }

    /**
     * Waits for an expression to become true on the main thread.
     *
     * @param condition
     */
    public void waitFor(final Condition condition) {
        final AtomicBoolean done = new AtomicBoolean();
        do {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    done.set(condition.call());
                }
            });
        }
        while (!done.get());
    }

    public void restartActivity() {
        final Activity activity = getActivity();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activity.recreate();
            }
        });
        setActivity(null);
        getActivity(); // DOES NOT WORK ON LOLLIPOP - has some bug in instrumentation
    }

    private void sleep(String description, final int ms) {
        final long time1 = System.nanoTime();
        waitFor(new Condition() {
            @Override
            public boolean call() {
                return (System.nanoTime() - time1) / 1000000 > ms;
            }
        });
        Log.v(getClass().getSimpleName(), "WAIT " + description + " COMPLETE");
    }

    public void runOnUiThread(final Runnable runnable) {
        getInstrumentation().runOnMainSync(runnable);
    }

    // prevents mockito exception on some devices
    public void dexmakerWorkaround() {
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
    }
}
