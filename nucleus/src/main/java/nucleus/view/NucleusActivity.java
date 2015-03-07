package nucleus.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;

import nucleus.presenter.Presenter;
import nucleus.presenter.PresenterManager;

public abstract class NucleusActivity<PresenterType extends Presenter> extends Activity {

    private static final String PRESENTER_STATE_KEY = "presenter_state";

    private PresenterType presenter;

    public PresenterType getPresenter() {
        return presenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = PresenterManager.getInstance().provide(this, savedInstanceState == null ? null : savedInstanceState.getBundle(PRESENTER_STATE_KEY));
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            PresenterManager.getInstance().destroy(presenter);
            presenter = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(PRESENTER_STATE_KEY, PresenterManager.getInstance().save(presenter));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //noinspection unchecked
        presenter.takeView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.dropView();
    }
}
