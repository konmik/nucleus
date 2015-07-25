package nucleus.factory;

import org.junit.After;
import org.junit.Test;

import nucleus.presenter.Presenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class PresenterStorageTest {

    @Test
    public void testSavePresenter() throws Exception {
        Presenter presenter = new Presenter();
        PresenterStorage.INSTANCE.add(presenter);

        Presenter presenter2 = new Presenter();
        PresenterStorage.INSTANCE.add(presenter2);

        String id = PresenterStorage.INSTANCE.getId(presenter);
        assertNotEquals(id, PresenterStorage.INSTANCE.getId(presenter2));

        assertEquals(presenter, PresenterStorage.INSTANCE.getPresenter(id));

        presenter.destroy();
        assertNull(PresenterStorage.INSTANCE.getPresenter(id));
    }

    @After
    public void after() {
        PresenterStorage.INSTANCE.clear();
    }
}
