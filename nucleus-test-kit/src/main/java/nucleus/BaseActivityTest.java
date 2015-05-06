package nucleus;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;

import java.util.concurrent.atomic.AtomicBoolean;

public class BaseActivityTest<ActivityClass extends Activity> extends ActivityInstrumentationTestCase2<ActivityClass> {

    public static final int DEFAULT_SCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private final Class<ActivityClass> activityClass;

    public BaseActivityTest(Class<ActivityClass> activityClass) {
        super(activityClass);
        this.activityClass = activityClass;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    boolean restarted;

    @Override
    protected void tearDown() throws Exception {
        if (restarted) {
            // prevents the situation when the screen orientation is going to be changed by previous test.
            final Activity activity = launchActivity(DefaultOrientationActivity.class);
            waitFor(new Condition() {
                @Override
                public boolean call() {
                    return activity.getResources().getConfiguration().orientation == DEFAULT_SCREEN_ORIENTATION;
                }
            });
        }
        super.tearDown();
    }

    public interface Condition {
        boolean call();
    }

    /**
     * Waits for expression to become true on the main thread.
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

    public void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Activity launchActivity(Class activityClass) {
        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(activityClass.getName(), null, false);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(getInstrumentation().getTargetContext(), activityClass.getName());
        Activity activity = getInstrumentation().startActivitySync(intent);
        getInstrumentation().waitForIdleSync();
        getInstrumentation().waitForMonitor(monitor);
        getInstrumentation().removeMonitor(monitor);
        return activity;
    }

    public void restartActivity() {
        Instrumentation.ActivityMonitor monitor = new Instrumentation.ActivityMonitor(activityClass.getName(), null, false);
        getInstrumentation().addMonitor(monitor);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().setRequestedOrientation(getActivity().getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ?
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().waitForMonitor(monitor);
        getInstrumentation().removeMonitor(monitor);
        restarted = true;
    }

    public void runOnUiThread(final Runnable runnable) {
        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            });
        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    // prevents mockito exception on some devices
    public void dexmakerWorkaround() {
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
    }
}
