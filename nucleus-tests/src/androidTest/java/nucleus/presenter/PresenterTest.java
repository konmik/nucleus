package nucleus.presenter;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import nucleus.TestActivity;

public class PresenterTest extends ActivityInstrumentationTestCase2 {

    public PresenterTest() {
        super(TestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @UiThreadTest
    public void testOnDestroy() throws Exception {
        final AtomicBoolean onDestroyChildren = new AtomicBoolean();
        Presenter presenter = new Presenter() {
            @Override
            protected void onDestroy() {
                onDestroyChildren.set(true);
            }
        };
        presenter.onCreate(null);
        final AtomicInteger onDestroy = new AtomicInteger();
        presenter.addOnDestroyListener(new Presenter.OnDestroyListener() {
            @Override
            public void onDestroy() {
                onDestroy.incrementAndGet();
            }
        });
        presenter.destroy();
        assertTrue(onDestroyChildren.get());
        assertEquals(1, onDestroy.get());
    }

    public void testTakeDrop() throws Exception {
        final AtomicBoolean onTakeChildren = new AtomicBoolean();
        final AtomicBoolean onDropChildren = new AtomicBoolean();
        Presenter presenter = new Presenter() {
            @Override
            protected void onTakeView(Object view) {
                assertNotNull(getView());
                onTakeChildren.set(true);
            }

            @Override
            protected void onDropView() {
                assertNotNull(getView());
                onDropChildren.set(true);
            }
        };
        presenter.onCreate(null);
        presenter.takeView(new Object());
        presenter.dropView();
        presenter.destroy();
        assertTrue(onTakeChildren.get());
        assertTrue(onDropChildren.get());
    }
}