package nucleus.factory;

import android.test.ActivityInstrumentationTestCase2;

import nucleus.TestActivity;
import nucleus.presenter.Presenter;

public class ReflectionPresenterFactoryTest extends ActivityInstrumentationTestCase2<TestActivity> {

    public ReflectionPresenterFactoryTest() {
        super(TestActivity.class);
    }

    static class MyPresenter extends Presenter {
    }

    @RequiresPresenter(MyPresenter.class)
    class View {
    }

    class NoPresenterView {
    }

    public void testCreatesPresenter() throws Exception {
        PresenterFactory<Presenter> factory = ReflectionPresenterFactory.fromViewClass(View.class);
        Presenter presenter = factory.createPresenter();
        assertNotNull(presenter);
        assertTrue(presenter instanceof MyPresenter);
    }

    public void testCreateNoFactory() throws Exception {
        assertNull(ReflectionPresenterFactory.fromViewClass(NoPresenterView.class));
    }
}
