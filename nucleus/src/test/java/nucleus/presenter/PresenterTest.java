package nucleus.presenter;

import android.os.Bundle;
import nucleus.presenter.broker.SimpleBroker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static nucleus.Mock.createTestBundle;
import static nucleus.Mock.getTestBundleValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PresenterTest {

    class TestPresenter extends Presenter {

        private static final long IN_ID = 3;
        private static final String SUPER = "super";

        public class TestBroker extends SimpleBroker {
        }

        public TestBroker testBroker1;
        public TestBroker testBroker2;

        public long outId;

        @Override
        public void onCreate(Bundle savedState) {
            super.onCreate(savedState == null ? null : savedState.getBundle(SUPER));
            outId = getTestBundleValue(savedState);

            testBroker1 = (TestBroker)addViewBroker(new TestBroker());
            testBroker2 = (TestBroker)addViewBroker(new TestBroker());
        }

        @Override
        public Bundle onSave() {
            Bundle bundle = createTestBundle(IN_ID);
            bundle.putBundle(SUPER, super.onSave());
            return bundle;
        }
    }

    @Test
    public void testProvide() throws Exception {
        final Presenter mockPresenter = Mockito.mock(Presenter.class);

        Presenter result = new Presenter().provide(new PresenterCreator() {
            @Override
            public Presenter createPresenter() {
                return mockPresenter;
            }
        }, null);

        assertEquals(result, mockPresenter);
        verify(mockPresenter).onCreate(null);
    }

    @Test
    public void testProvideWithSaveRestore() throws Exception {
        final TestPresenter testPresenter = new TestPresenter();
        new Presenter().provide(new PresenterCreator() {
            @Override
            public Presenter createPresenter() {
                return testPresenter;
            }
        }, null);

        Bundle bundle = testPresenter.save();

        final TestPresenter testPresenter2 = new TestPresenter();
        new Presenter().provide(new PresenterCreator() {
            @Override
            public Presenter createPresenter() {
                return testPresenter2;
            }
        }, bundle);

        assertEquals(TestPresenter.IN_ID, testPresenter2.outId);
    }
}
