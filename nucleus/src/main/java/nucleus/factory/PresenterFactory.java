package nucleus.factory;

import nucleus.presenter.Presenter;

public interface PresenterFactory<T extends Presenter> {
    T createPresenter();
}
