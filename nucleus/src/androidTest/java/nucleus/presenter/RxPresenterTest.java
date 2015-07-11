package nucleus.presenter;

import junit.framework.TestCase;

public class RxPresenterTest extends TestCase {

    public void testRestartable() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.onCreate(null);

    }
}