package nucleus.view;

import android.os.Bundle;
import nucleus.presenter.Presenter;
import nucleus.presenter.PresenterCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static nucleus.Mock.createTestBundle;
import static nucleus.Mock.getTestBundleValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PresenterActivityTest {

    ActivityController<TestNucleusActivity> activityController;
    TestNucleusActivity activity;

    public static class TestNucleusActivity extends NucleusActivity {
        @Override
        public Presenter createPresenter() {
            return Mockito.mock(Presenter.class);
        }
    }

    static Presenter mockPresenter;
    static int ParentPresenter_provide;
    static Bundle ParentPresenter_bundleIn;
    static Bundle ParentPresenter_bundleOut;

    public static Presenter parentPresenter = new Presenter() {
        @Override
        public Presenter provide(PresenterCreator creator, Bundle savedState) {
            ParentPresenter_provide++;
            ParentPresenter_bundleOut = savedState;

            mockPresenter = creator.createPresenter();
            when(mockPresenter.save()).thenReturn(ParentPresenter_bundleIn);
            return mockPresenter;
        }
    };

    public void setUp(Bundle bundle) throws Exception {

        ParentPresenter_provide = 0;
        ParentPresenter_bundleIn = createTestBundle(1);
        ParentPresenter_bundleOut = null;

        PresenterFinder.setInstance(new PresenterFinder() {
            @Override
            public Presenter findParentPresenter(Object view) {
                return parentPresenter;
            }
        });

        activityController = Robolectric.buildActivity(TestNucleusActivity.class);

        if (bundle != null)
            activityController.setup(bundle);
        else
            activityController.setup();

        activity = activityController.get();
    }

    @Test
    public void testOnCreateNull() throws Exception {
        // EXPECTATION: onCreate() -> activity.findParentPresenter().provide() -> presenter

        setUp(null);

        assertEquals(ParentPresenter_provide, 1);
        assertThat(mockPresenter, is(notNullValue()));
        assertEquals(mockPresenter, activity.getPresenter());
    }

    @Test
    public void testOnAttachedToWindow() throws Exception {
        // EXPECTATION: onAttachedToWindow() -> presenter.takeView(activity)

        setUp(null);

        verify(mockPresenter, times(1)).takeView(activity);
    }

    @Test
    public void testOnDestroy() throws Exception {
        // EXPECTATION: onDestroy() -> presenter.dropView(), finish()+onDestroy() -> presenter.destroy()

        setUp(null);
        activityController.destroy();

        verify(mockPresenter, times(1)).dropView(any(TestNucleusActivity.class));
        verify(mockPresenter, never()).destroy();

        setUp(null);
        activity.finish();
        activityController.destroy();

        verify(mockPresenter, times(1)).dropView(any(TestNucleusActivity.class));
        verify(mockPresenter, times(1)).destroy();
    }

    @Test
    public void testOnSaveInstanceState() throws Exception {
        // EXPECTATION: saveInstanceState() -> presenter.save() -> bundle, bundle -> presenter.onCreate() -> parentPresenter.provide()

        Bundle bundle = new Bundle();

        setUp(null);
        activityController.saveInstanceState(bundle);
        activityController.destroy();

        setUp(bundle);

        assertEquals(getTestBundleValue(ParentPresenter_bundleIn), getTestBundleValue(ParentPresenter_bundleOut));
    }
}