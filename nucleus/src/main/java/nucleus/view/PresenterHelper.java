package nucleus.view;

import android.app.Activity;
import android.os.Bundle;

import nucleus.factory.PresenterFactory;
import nucleus.manager.PresenterManager;
import nucleus.presenter.Presenter;

/**
 * A helper class to control presenter's lifecycle.
 *
 * @param <PresenterType>
 */
public class PresenterHelper<PresenterType extends Presenter> {

    private PresenterType presenter;
    private Activity activity;

    public PresenterType getPresenter() {
        return presenter;
    }

    /**
     * Destroys a presenter that is currently attached to the View.
     */
    public void destroyPresenter() {
        if (presenter != null) {
            PresenterManager.getInstance().destroy(presenter);
            presenter = null;
        }
    }

    public void requestPresenter(PresenterFactory<PresenterType> presenterFactory, Bundle presenterState) {
        if (presenter == null && presenterFactory != null)
            presenter = PresenterManager.getInstance().provide(presenterFactory, presenterState);
    }

    public Bundle savePresenter() {
        return presenter == null ? null : PresenterManager.getInstance().save(presenter);
    }

    public void takeView(Object view, PresenterFactory<PresenterType> presenterFactory, Activity activity) {
        requestPresenter(presenterFactory, null);
        if (presenter != null)
            //noinspection unchecked
            presenter.takeView(view);
        this.activity = activity;
    }

    public void dropView() {
        if (presenter != null)
            presenter.dropView();
        if (activity.isFinishing())
            destroyPresenter();
        activity = null;
    }
}
