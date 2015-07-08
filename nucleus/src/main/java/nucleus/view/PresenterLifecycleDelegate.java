package nucleus.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import nucleus.factory.PresenterFactory;
import nucleus.presenter.Presenter;

public final class PresenterLifecycleDelegate<PresenterType extends Presenter> {

    @Nullable private PresenterFactory<PresenterType> presenterFactory;
    @Nullable private PresenterType presenter;
    @Nullable private Bundle presenterState;

    public PresenterLifecycleDelegate(@Nullable PresenterFactory<PresenterType> presenterFactory) {
        this.presenterFactory = presenterFactory;
    }

    @Nullable
    public PresenterFactory<PresenterType> getPresenterFactory() {
        return presenterFactory;
    }

    public void setPresenterFactory(@Nullable PresenterFactory<PresenterType> presenterFactory) {
        if (presenter != null)
            throw new IllegalArgumentException("setPresenterFactory() should be called before onResume()");
        this.presenterFactory = presenterFactory;
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

    public PresenterType getPresenter() {
        if (presenter == null && presenterFactory != null) {
            presenter = presenterFactory.providePresenter(presenterState);
            presenterState = null;
        }
        return presenter;
    }
}
