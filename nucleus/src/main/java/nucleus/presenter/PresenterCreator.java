package nucleus.presenter;

public interface PresenterCreator<T extends Presenter> {
    T createPresenter();
}
