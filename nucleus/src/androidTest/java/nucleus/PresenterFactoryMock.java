package nucleus;

import android.os.Bundle;

import org.mockito.Mockito;

import nucleus.factory.PresenterFactory;
import nucleus.presenter.Presenter;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class PresenterFactoryMock {
    public static PresenterFactory mock(Presenter presenter) {
        PresenterFactory mock = Mockito.mock(PresenterFactory.class);
        when(mock.createPresenter(any(Bundle.class))).thenReturn(presenter);
        return mock;
    }

    public static PresenterFactory mock() {
        return mock(PresenterMock.mock());
    }
}
