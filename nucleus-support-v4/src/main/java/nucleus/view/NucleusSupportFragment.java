package nucleus.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import nucleus.factory.PresenterFactory;
import nucleus.factory.ReflectionPresenterFactory;
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
        if (bundle != null)
            helper.setPresenterState(bundle.getBundle(PRESENTER_STATE_KEY));
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
     * The factory class used to create the presenter. Defaults to {@link ReflectionPresenterFactory} to create the presenter
     * using a no arg constructor.
     * <p/>
     * Subclasses can override this to provide presenters in other ways, like via their dependency injector.
     *
     * @return The {@link PresenterFactory} that can build a {@link Presenter}, or null.
     */
    public PresenterFactory<PresenterType> getPresenterFactory() {
        return ReflectionPresenterFactory.fromViewClass(getClass());
    }

    /**
     * Returns a current attached presenter.
     * This method is guaranteed to return a non-null value between
     * onResume/onPause and onAttachedToWindow/onDetachedFromWindow calls
     * if the presenter factory returns a non-null value.
     *
     * @return a currently attached presenter or null.
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
    private PresenterHelper<PresenterType> helper = new PresenterHelper<>(getPresenterFactory());
}
