package nucleus.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import nucleus.factory.PresenterFactory;
import nucleus.factory.PresenterStorage;
import nucleus.presenter.Presenter;

/**
 * This class delivers View events to Presenter.
 *
 * @param <P> a type of the presenter.
 */
public final class PresenterLifecycleDelegate<P extends Presenter> {

    private static final String PRESENTER_KEY = "presenter";
    private static final String PRESENTER_ID_KEY = "presenter_id";

    @Nullable private PresenterFactory<P> presenterFactory;
    @Nullable private P presenter;
    @Nullable private Bundle bundle;

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
        if (presenterFactory != null) {
            if (presenter == null && bundle != null)
                presenter = PresenterStorage.INSTANCE.get(bundle.getString(PRESENTER_ID_KEY));

            if (presenter == null) {
                presenter = presenterFactory.createPresenter();
                PresenterStorage.INSTANCE.add(presenter);
                presenter.create(bundle == null ? null : bundle.getBundle(PRESENTER_KEY));
            }
            bundle = null;
        }
        return presenter;
    }

    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        if (presenterFactory != null && presenter != null) {
            Bundle presenterBundle = new Bundle();
            presenter.save(presenterBundle);
            bundle.putBundle(PRESENTER_KEY, presenterBundle);
            bundle.putString(PRESENTER_ID_KEY, PresenterStorage.INSTANCE.getPresenterId(presenter));
        }
        return bundle;
    }

    public void onRestoreInstanceState(Bundle presenterState) {
        if (presenter != null)
            throw new IllegalArgumentException("onRestoreInstanceState() should be called before onResume()");
        this.bundle = presenterState;
    }

    public void onResume(Object view) {
        getPresenter();
        if (presenter != null)
            //noinspection unchecked
            presenter.takeView(view);
    }

    public void onPause(boolean destroy) {
        if (presenter != null) {
            presenter.dropView();
            if (destroy) {
                presenter.destroy();
                presenter = null;
            }
        }
    }
}
