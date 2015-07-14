package nucleus;

import org.mockito.Mockito;

import nucleus.presenter.Presenter;

public class PresenterMock {
    public static Presenter mock() {
        return Mockito.mock(Presenter.class);
    }
}
