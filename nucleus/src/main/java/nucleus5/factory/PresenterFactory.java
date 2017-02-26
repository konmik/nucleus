package nucleus5.factory;

import nucleus5.presenter.Presenter;

public interface PresenterFactory<P extends Presenter> {
    P createPresenter();
}
