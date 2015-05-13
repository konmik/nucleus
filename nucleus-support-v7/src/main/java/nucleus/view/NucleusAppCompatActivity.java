package nucleus.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import nucleus.factory.PresenterFactory;
import nucleus.factory.ReflectionPresenterFactory;
import nucleus.presenter.Presenter;

/**
 * This class is an example of how an activity could controls it's presenter.
 * You can inherit from this class or copy/paste this class's code to
 * create your own view implementation.
 *
 * @param <PresenterType> a type of presenter to return with {@link #getPresenter}.
 */
public abstract class NucleusAppCompatActivity<PresenterType extends Presenter> extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            helper.setPresenterState(savedInstanceState.getBundle(PRESENTER_STATE_KEY));
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
