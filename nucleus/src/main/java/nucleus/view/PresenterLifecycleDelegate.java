package nucleus.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import nucleus.factory.PresenterFactory;
import nucleus.presenter.Presenter;

/**
 * This class delivers View events to Presenter.
 *
 * @param <P> a type of the presenter.
 */
public final class PresenterLifecycleDelegate<P extends Presenter> {

    @Nullable private PresenterFactory<P> presenterFactory;
    @Nullable private P presenter;
    @Nullable private Bundle presenterState;

    public PresenterLifecycleDelegate(@Nullable PresenterFactory<P> presenterFactory) {
        this.presenterFactory = presenterFactory;
    }

    @Nullable
    public PresenterFactory<P> getPresenterFactory() {
        return presenterFactory;
    }

    public void setPresenterFactory(@Nullable PresenterFactory<P> presenterFactory) {
        if (presenter != null)
            throw new IllegalArgumentException("setPresenterFactory() should be called before onResume()");
        this.presenterFactory = presenterFactory;
    }

    public P getPresenter() {
        if (presenter == null && presenterFactory != null) {
            presenter = presenterFactory.providePresenter(presenterState);
            presenterState = null;
        }
        return presenter;
    }

    public Bundle onSaveInstanceState() {
        Bundle presenterBundle = new Bundle();
        if (presenterFactory != null && presenter != null)
            presenterFactory.savePresenter(presenter, presenterBundle);
        return presenterBundle;
    }

    public void onRestoreInstanceState(Bundle presenterState) {
        if (presenter != null)
            throw new IllegalArgumentException("onRestoreInstanceState() should be called before onResume()");
        this.presenterState = presenterState;
    }

    public void onResume(Object view) {
        getPresenter();
        if (presenter != null)
            //noinspection unchecked
            presenter.onTakeView(view);
    }

    public void onPause(boolean destroy) {
        if (presenter != null) {
            presenter.onDropView();
            if (destroy) {
                presenter.onDestroy();
                presenter = null;
            }
        }
    }
}
