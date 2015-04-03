package nucleus.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;

import nucleus.manager.PresenterManager;
import nucleus.presenter.Presenter;

/**
 * This class is an example of how an activity could controls it's presenter.
 * You can inherit from this class or copy/paste this class's code to
 * create your own view implementation.
 *
 * @param <PresenterType> a type of presenter to return with {@link #getPresenter}.
 */
public abstract class NucleusActionBarActivity<PresenterType extends Presenter> extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPresenter(savedInstanceState == null ? null : savedInstanceState.getBundle(PRESENTER_STATE_KEY));
    }

    @Override
    protected void onDestroy() {
        if (isFinishing())
            destroyPresenter();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(PRESENTER_STATE_KEY, savePresenter());
    }

    @Override
    protected void onResume() {
        super.onResume();
        takeView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dropView(this);
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
