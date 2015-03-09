package nucleus.manager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.ActivityInstrumentationTestCase2;

import java.util.concurrent.atomic.AtomicBoolean;

import nucleus.TestActivity;
import nucleus.manager.DefaultPresenterManager;
import nucleus.manager.PresenterManager;
import nucleus.manager.RequiresPresenter;
import nucleus.presenter.Presenter;

public class PresenterManagerTest extends ActivityInstrumentationTestCase2 {
    public PresenterManagerTest() {
        super(TestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        PresenterManager.setInstance(new DefaultPresenterManager());
    }

    public void testGetInstance() throws Exception {
        assertNotNull(PresenterManager.getInstance());
    }

    static class TestPresenter extends Presenter {
        AtomicBoolean testPresenterOnCreateBundleStateAssert = new AtomicBoolean();
        AtomicBoolean testPresenterOnSave = new AtomicBoolean();

        @Override
        protected void onCreate(@Nullable Bundle savedState) {
            if (savedState != null) {
                assertEquals(1, savedState.getInt("test"));
                testPresenterOnCreateBundleStateAssert.set(true);
            }
        }

        @Override
        protected void onSave(@NonNull Bundle state) {
            state.putInt("test", 1);
            testPresenterOnSave.set(true);
        }
    }

    @RequiresPresenter(TestPresenter.class)
    static class TestView {
    }

    public void testProvideException() throws Exception {
        try {
            PresenterManager.getInstance().provide(new Object(), null);
            assertTrue(false);
        }
        catch (Exception ignored) {
        }
    }

    public void testProvide() throws Exception {
        TestPresenter presenter = PresenterManager.getInstance().provide(new TestView(), null);
        assertNotNull(presenter);
        Bundle bundle = PresenterManager.getInstance().save(presenter);
        assertTrue(presenter.testPresenterOnSave.get());
        PresenterManager.setInstance(new DefaultPresenterManager());
        presenter = PresenterManager.getInstance().provide(new TestView(), bundle);
        assertTrue(presenter.testPresenterOnCreateBundleStateAssert.get());
    }
}