package nucleus.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import nucleus.manager.PresenterManager;
import nucleus.manager.RequiresPresenter;
import nucleus.presenter.Presenter;

/**
 * This view is an example of how a view should control it's presenter.
 * You can inherit from this class or copy/paste this class's code to
 * create your own view implementation.
 *
 * @param <PresenterType> a type of presenter to return with {@link #getPresenter}.
 */
public class NucleusSupportFragment<PresenterType extends Presenter> extends Fragment {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestPresenter(bundle == null ? null : bundle.getBundle(PRESENTER_STATE_KEY));
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBundle(PRESENTER_STATE_KEY, savePresenter());
    }

    @Override
    public void onResume() {
        super.onResume();
        takeView();
    }

    @Override
    public void onPause() {
        super.onPause();
        dropView(getActivity());
    }

    // The following section can be copy & pasted into any View class, just update their description if needed.

    private static final String PRESENTER_STATE_KEY = "presenter_state";
    private PresenterType presenter;

    /**
     * Returns a current attached presenter.
     * This method is guaranteed to return a non-null value between
     * onResume/onPause calls.
     *
     * @return a current attached presenter or null.
     */
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

    private void requestPresenter(Bundle presenterState) {
        if (presenter == null) {
            Class<PresenterType> presenterClass = findPresenterClass();
            if (presenterClass != null)
                presenter = PresenterManager.getInstance().provide(presenterClass, presenterState);
        }
    }

    private Bundle savePresenter() {
        return presenter == null ? null : PresenterManager.getInstance().save(presenter);
    }

    private void takeView() {
        requestPresenter(null);
        if (presenter != null)
            //noinspection unchecked
            presenter.takeView(this);
    }

    private void dropView(Activity activity) {
        if (presenter != null)
            presenter.dropView();
        if (activity.isFinishing())
            destroyPresenter();
    }

    private Class<PresenterType> findPresenterClass() {
        RequiresPresenter annotation = getClass().getAnnotation(RequiresPresenter.class);
        //noinspection unchecked
        return annotation == null ? null : (Class<PresenterType>)annotation.value();
    }
}
