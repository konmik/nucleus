package nucleus.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;

import nucleus.presenter.Presenter;
import nucleus.factory.PresenterFactory;
import nucleus.factory.ReflectionPresenterFactory;

/**
 * This class is an example of how an activity could controls it's presenter.
 * You can inherit from this class or copy/paste this class's code to
 * create your own view implementation.
 *
 * @param <PresenterType> a type of presenter to return with {@link #getPresenter}.
 */
public abstract class NucleusActivity<PresenterType extends Presenter> extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper.requestPresenter(getPresenterFactory(),
                savedInstanceState == null ? null
                        : savedInstanceState.getBundle(PRESENTER_STATE_KEY));
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
        helper.takeView(this, getPresenterFactory(), this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        helper.dropView();
    }

    // The following section can be copy & pasted into any View class, just update their description if needed.

    /**
     * The factory class used to create the presenter. Defaults to
     * {@link ReflectionPresenterFactory} to create the presenter
     * using a no arg constructor.
     *
     * Subclasses can override this to provide presenters in other
     * ways, like via their dependency injector.
     *
     * @return The {@link PresenterFactory} that can build a {@link Presenter}, or null.
     */
    public PresenterFactory<PresenterType> getPresenterFactory() {
        Class<PresenterType> presenterClass = findPresenterClass(getClass());
        return presenterClass == null ? null : new ReflectionPresenterFactory<>(presenterClass);
    }

    private Class<PresenterType> findPresenterClass(Class<?> viewClass) {
        RequiresPresenter annotation = viewClass.getAnnotation(RequiresPresenter.class);
        //noinspection unchecked
        return annotation == null ? null : (Class<PresenterType>)annotation.value();
    }

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
