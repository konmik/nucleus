package nucleus.view;

import android.os.Bundle;
import nucleus.manager.PresenterManager;
import nucleus.presenter.Presenter;

/**
 * A helper class to control presenter's lifecycle.
 *
 * @param <PresenterType>
 */
public class PresenterHelper<PresenterType extends Presenter> {

    private PresenterType presenter;

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

    public void requestPresenter(Class viewClass, Bundle presenterState) {
        if (presenter == null) {
            Class<PresenterType> presenterClass = findPresenterClass(viewClass);
            if (presenterClass != null)
                presenter = PresenterManager.getInstance().provide(presenterClass, presenterState);
        }
    }

    public Bundle savePresenter() {
        return presenter == null ? null : PresenterManager.getInstance().save(presenter);
    }

    public void takeView(Object view) {
        requestPresenter(view.getClass(), null);
        if (presenter != null)
            //noinspection unchecked
            presenter.takeView(view);
    }

    public void dropView(boolean destroy) {
        if (presenter != null)
            presenter.dropView();
    }

    private Class<PresenterType> findPresenterClass(Class<?> viewClass) {
        RequiresPresenter annotation = viewClass.getAnnotation(RequiresPresenter.class);
        //noinspection unchecked
        return annotation == null ? null : (Class<PresenterType>)annotation.value();
    }
}
