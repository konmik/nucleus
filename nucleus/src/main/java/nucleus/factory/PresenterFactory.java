package nucleus.factory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import nucleus.presenter.Presenter;

public interface PresenterFactory<T extends Presenter> {
    T providePresenter(@Nullable Bundle bundle);
    void savePresenter(Presenter presenter, @NonNull Bundle bundle);
}
