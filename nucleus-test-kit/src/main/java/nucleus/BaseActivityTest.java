package nucleus;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

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
        } while (!done.get());
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
        getActivity();
    }

    public void runOnUiThread(final Runnable runnable) {
        getInstrumentation().runOnMainSync(runnable);
    }

    // prevents mockito exception on some devices
    public void dexmakerWorkaround() {
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
    }
}
