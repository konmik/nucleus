package nucleus5.view;

import android.os.Bundle;
import android.support.annotation.NonNull;

import androidx.fragment.app.FragmentActivity;
import nucleus5.factory.PresenterFactory;
import nucleus5.factory.ReflectionPresenterFactory;
import nucleus5.presenter.Presenter;

/**
 * This class is an example of how an activity could controls it's presenter.
 * You can inherit from this class or copy/paste this class's code to
 * create your own view implementation.
 *
 * @param <P> a type of presenter to return with {@link #getPresenter}.
 */
public abstract class NucleusXFragmentActivity<P extends Presenter> extends FragmentActivity implements ViewWithPresenter<P> {

    private static final String PRESENTER_STATE_KEY = "presenter_state";

    private PresenterLifecycleDelegate<P> presenterDelegate =
            new PresenterLifecycleDelegate<>(ReflectionPresenterFactory.<P>fromViewClass(getClass()));

    /**
     * Returns a current presenter factory.
     */
    public PresenterFactory<P> getPresenterFactory() {
        return presenterDelegate.getPresenterFactory();
    }

    /**
     * Sets a presenter factory.
     * Call this method before onCreate/onFinishInflate to override default {@link ReflectionPresenterFactory} presenter factory.
     * Use this method for presenter dependency injection.
     */
    @Override
    public void setPresenterFactory(PresenterFactory<P> presenterFactory) {
        presenterDelegate.setPresenterFactory(presenterFactory);
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
        presenterDelegate.onDropView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenterDelegate.onDestroy(!isChangingConfigurations());
    }
}
