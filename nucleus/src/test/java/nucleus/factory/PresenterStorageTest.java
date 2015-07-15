package nucleus.factory;

import org.junit.After;
import org.junit.Test;

import nucleus.presenter.Presenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PresenterStorageTest {

    @Test
    public void testSavePresenter() throws Exception {
        Presenter presenter = new Presenter();
        PresenterStorage.INSTANCE.add(presenter);

        String id = PresenterStorage.INSTANCE.getPresenterId(presenter);

        assertEquals(presenter, PresenterStorage.INSTANCE.get(id));

        presenter.onDestroy();
        assertNull(PresenterStorage.INSTANCE.get(id));
    }

    @After
    public void after() {
        PresenterStorage.INSTANCE.clear();
    }
}
