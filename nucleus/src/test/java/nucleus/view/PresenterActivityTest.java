package nucleus.view;

import android.os.Bundle;
import nucleus.presenter.Presenter;
import nucleus.presenter.PresenterCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
//import org.powermock.core.classloader.annotations.PowerMockIgnore;
//import org.powermock.core.classloader.annotations.PrepareForTest;
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
//@PrepareForTest({Presenter.class})
//@PowerMockIgnore({"org.robolectric.*", "android.*"})
@Config(manifest = Config.NONE)
public class PresenterActivityTest {

    private static final String TIME_KEY = "time";

//    @Rule
//    public PowerMockRule rule = new PowerMockRule();

    ActivityController<TestNucleusActivity> activityController;
    TestNucleusActivity activity;
    static Presenter mockPresenter;

    public static class TestNucleusActivity extends NucleusActivity {

        public static Presenter parentPresenter = new Presenter() {
            @Override
            public Presenter provide(PresenterCreator creator, Bundle savedState) {
                ParentPresenter_provide++;
                ParentPresenter_bundleOut = savedState;

                mockPresenter = Mockito.mock(Presenter.class);
                when(mockPresenter.save()).thenReturn(ParentPresenter_bundleIn);
                return mockPresenter;
            }
        };

        public static int TestNucleusActivity_findParentProvider;
        public static int ParentPresenter_provide;

        public static Bundle ParentPresenter_bundleIn;
        public static Bundle ParentPresenter_bundleOut;

        public static void reset() {
            TestNucleusActivity_findParentProvider = 0;
            ParentPresenter_provide = 0;
            ParentPresenter_bundleIn = createTestBundle(1);
            ParentPresenter_bundleOut = null;
        }

        public TestNucleusActivity() {
        }

        @Override
        public Presenter createPresenter() {
            return null;
        }

        @Override
        protected Presenter findParentPresenter() {
            TestNucleusActivity_findParentProvider++;
            return parentPresenter;
        }
    }

    public void setUp(Bundle bundle) throws Exception {
        TestNucleusActivity.reset();

        activityController = Robolectric.buildActivity(TestNucleusActivity.class);

        if (bundle != null)
            activityController.setup(bundle);
        else
            activityController.setup();

        activity = activityController.get();
    }

    @Test
    public void testOnCreateNull() throws Exception {
        // EXPECTATION: activity.findParentPresenter().provide() -> presenter

        setUp(null);

        assertEquals(TestNucleusActivity.ParentPresenter_provide, 1);
        assertThat(mockPresenter, is(notNullValue()));
        assertEquals(mockPresenter, activity.getPresenter());
    }

    @Test
    public void testOnAttachedToWindow() throws Exception {
        // EXPECTATION: presenter.takeView(activity)

        setUp(null);

        verify(mockPresenter, times(1)).takeView(activity);
    }

    @Test
    public void testOnDestroy() throws Exception {
        // EXPECTATION: presenter.dropView(), presenter.destroy

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
        // EXPECTATION: presenter.save() -> bundle, bundle -> presenter.onCreate

        Bundle bundle = new Bundle();

        setUp(null);
        activityController.saveInstanceState(bundle);
        activityController.destroy();

        setUp(bundle);

        assertEquals(getTestBundleValue(TestNucleusActivity.ParentPresenter_bundleIn), getTestBundleValue(TestNucleusActivity.ParentPresenter_bundleOut));
    }
}