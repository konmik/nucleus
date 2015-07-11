package nucleus.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;

import nucleus.factory.PresenterFactory;
import nucleus.factory.ReflectionPresenterFactory;
import nucleus.presenter.Presenter;

/**
 * This class is an example of how an activity could controls it's presenter.
 * You can inherit from this class or copy/paste this class's code to
 * create your own view implementation.
 *
 * @param <P> a type of presenter to return with {@link #getPresenter}.
 */
public class NucleusActivity<P extends Presenter> extends Activity implements ViewWithPresenter<P> {

    private static final String PRESENTER_STATE_KEY = "presenter_state";

    private PresenterLifecycleDelegate<P> presenterDelegate =
        new PresenterLifecycleDelegate<>(ReflectionPresenterFactory.<P>fromViewClass(getClass()));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            presenterDelegate.onRestoreInstanceState(savedInstanceState.getBundle(PRESENTER_STATE_KEY));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(PRESENTER_STATE_KEY, presenterDelegate.onSaveInstanceState());
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenterDelegate.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenterDelegate.onPause(isFinishing());
    }

    // The following section can be copy & pasted into any View class, just update their description if needed.

    @Override
    public void setPresenterFactory(PresenterFactory<P> presenterFactory) {
        presenterDelegate.setPresenterFactory(presenterFactory);
    }

    /**
     * The factory class used to create the presenter. Defaults to {@link ReflectionPresenterFactory} to create the presenter
     * using a no arg constructor.
     * <p/>
     * Subclasses can override this to provide presenters in other ways, like via their dependency injector.
     *
     * @return The {@link PresenterFactory} that can build a {@link Presenter}, or null.
     */
    public PresenterFactory<P> getPresenterFactory() {
        return presenterDelegate.getPresenterFactory();
    }

    /**
     * Returns a current attached presenter.
     * This method is guaranteed to return a non-null value between
     * onResume/onPause and onAttachedToWindow/onDetachedFromWindow calls
     * if the presenter factory returns a non-null value.
     *
     * @return a currently attached presenter or null.
     */
    public P getPresenter() {
        return presenterDelegate.getPresenter();
    }
}
