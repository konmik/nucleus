package nucleus.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import nucleus.presenter.Presenter;
import nucleus.presenter.PresenterCreator;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_LAUNCHER;

@SuppressWarnings("unchecked")
public abstract class NucleusActivity<PresenterType extends Presenter> extends Activity implements PresenterProvider<PresenterType>, PresenterCreator<PresenterType> {

    private static final String PRESENTER_STATE_KEY = "presenter_state";

    private PresenterType presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (finishIfWrongInstance())
            return;

        presenter = (PresenterType)findParentPresenter()
            .provide(this, savedInstanceState == null ? null : savedInstanceState.getBundle(PRESENTER_STATE_KEY));
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        presenter.dropView(this);

        if (isFinishing())
            presenter.destroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(PRESENTER_STATE_KEY, presenter.save());
    }

    @Override
    public PresenterType getPresenter() {
        return presenter;
    }

    protected Presenter findParentPresenter() {
        return PresenterFinder.findParentPresenter(this);
    }

    private boolean finishIfWrongInstance() {
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            boolean isMainAction = intent.getAction() != null && intent.getAction().equals(ACTION_MAIN);
            if (intent.hasCategory(CATEGORY_LAUNCHER) && isMainAction)
                finish();
        }
        return false;
    }
}
