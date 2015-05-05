package nucleus.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;

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
        helper.requestPresenter(getClass(), bundle == null ? null : bundle.getBundle(PRESENTER_STATE_KEY));
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBundle(PRESENTER_STATE_KEY, helper.savePresenter());
    }

    @Override
    public void onResume() {
        super.onResume();
        helper.takeView(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        helper.dropView(getActivity().isFinishing());
    }

    // The following section can be copy & pasted into any View class, just update their description if needed.

    /**
     * Returns a current attached presenter.
     * This method is guaranteed to return a non-null value between
     * onResume/onPause calls.
     *
     * @return a current attached presenter or null.
     */
    public PresenterType getPresenter() {
        return helper.getPresenter();
    }

    /**
     * Destroys a presenter that is currently attached to the View.
     */
    public void destroyPresenter() {
        helper.destroyPresenter();
    }

    private static final String PRESENTER_STATE_KEY = "presenter_state";
    private PresenterHelper<PresenterType> helper = new PresenterHelper<>();
}
