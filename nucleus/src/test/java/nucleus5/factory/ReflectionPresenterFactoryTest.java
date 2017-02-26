package nucleus5.factory;

import android.os.Bundle;

import org.junit.Test;

import nucleus5.factory.PresenterFactory;
import nucleus5.factory.ReflectionPresenterFactory;
import nucleus5.factory.RequiresPresenter;
import nucleus5.presenter.Presenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

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
        assertNotNull(new ReflectionPresenterFactory<>(TestPresenter.class).createPresenter());

        PresenterFactory<Presenter> factory = ReflectionPresenterFactory.fromViewClass(ViewWithPresenter.class);
        assertNotNull(factory);
        assertTrue(factory.createPresenter() instanceof TestPresenter);
    }
}
