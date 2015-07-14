package nucleus.factory;

import android.os.Bundle;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import mocks.BundleMock;
import nucleus.presenter.Presenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class ReflectionPresenterFactoryTest {


    public static class ViewNoPresenter {

    }


    public static class TestPresenter extends Presenter {

        public int value;

        @Override
        public void onCreate(Bundle savedState) {
            super.onCreate(savedState);
            if (savedState != null)
                value = savedState.getInt("1");
        }

        @Override
        public void onSave(Bundle state) {
            super.onSave(state);
            state.putInt("1", 1);
        }
    }

    @RequiresPresenter(TestPresenter.class)
    public static class ViewWithPresenter extends Presenter {

    }

    @Test
    public void testNoPresenter() throws Exception {
        assertNull(ReflectionPresenterFactory.fromViewClass(ViewNoPresenter.class));
    }

    @Test
    public void testProvidePresenter() throws Exception {
        assertNotNull(new ReflectionPresenterFactory<>(TestPresenter.class).providePresenter(null));

        PresenterFactory<Presenter> factory = ReflectionPresenterFactory.fromViewClass(ViewWithPresenter.class);
        assertNotNull(factory);
        assertTrue(factory.providePresenter(null) instanceof TestPresenter);
    }

    @Test
    public void testSavePresenter() throws Exception {
        Bundle bundle = BundleMock.mock();

        ReflectionPresenterFactory<TestPresenter> factory = new ReflectionPresenterFactory<>(TestPresenter.class);
        TestPresenter presenter = factory.providePresenter(null);
        factory.savePresenter(presenter, bundle);

        TestPresenter presenter1 = factory.providePresenter(bundle);
        assertEquals(presenter1, presenter);

        presenter.onDestroy();

        TestPresenter presenter2 = factory.providePresenter(bundle);
        assertNotEquals(presenter, presenter2);
        assertEquals(1, presenter2.value);
    }
}
