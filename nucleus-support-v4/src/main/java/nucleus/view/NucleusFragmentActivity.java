package nucleus.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import nucleus.presenter.Presenter;

/**
 * This class is an example of how an activity could controls it's presenter.
 * You can inherit from this class or copy/paste this class's code to
 * create your own view implementation.
 *
 * @param <PresenterType> a type of presenter to return with {@link #getPresenter}.
 */
public abstract class NucleusFragmentActivity<PresenterType extends Presenter> extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper.requestPresenter(getClass(), savedInstanceState == null ? null : savedInstanceState.getBundle(PRESENTER_STATE_KEY));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing())
            destroyPresenter();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(PRESENTER_STATE_KEY, helper.savePresenter());
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.takeView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        helper.dropView(isFinishing());
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
