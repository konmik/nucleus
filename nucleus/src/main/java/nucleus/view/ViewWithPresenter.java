package nucleus.view;

import nucleus.factory.PresenterFactory;
import nucleus.factory.ReflectionPresenterFactory;
import nucleus.presenter.Presenter;

public interface ViewWithPresenter<P extends Presenter> {

    /**
     * Sets a presenter factory. Call it before onCreate/onFinishInflate to override default {@link ReflectionPresenterFactory} presenter factory.
     * Use this method for testing purposes or for presenters dependency injection.
     */
    void setPresenterFactory(PresenterFactory<P> presenterFactory);

    /**
     * Returns a current presenter.
     */
    PresenterFactory<P> getPresenterFactory();

    /**
     * Returns a current attached presenter.
     * This method is guaranteed to return a non-null value between
     * onResume/onPause and onAttachedToWindow/onDetachedFromWindow calls
     * if the presenter factory returns a non-null value.
     *
     * @return a currently attached presenter or null.
     */
    P getPresenter();
}
