package nucleus.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import nucleus.manager.PresenterManager;
import nucleus.presenter.Presenter;

/**
 * This view is an example of how a view should control it's presenter.
 * You can inherit from this class or copy/paste this class's code to
 * create your own view implementation.
 *
 * @param <PresenterType> a type of presenter to return with {@link #getPresenter}.
 */
public class NucleusSupportFragment<PresenterType extends Presenter> extends Fragment {

    private static final String PRESENTER_STATE_KEY = "presenter_state";

    private PresenterType presenter;
    private OnDropViewAction onDropViewAction = OnDropViewAction.DESTROY_PRESENTER_IF_FINISHING;

    /**
     * Returns a current attached presenter.
     * This method is guaranteed to return a non-null value between
     * onAttachedToWindow/onDetachedFromWindow calls.
     *
     * @return a current attached presenter or null.
     */
    public PresenterType getPresenter() {
        return presenter;
    }

    /**
     * Sets an action that should be performed during onDetachedFromWindow call.
     *
     * @param onDropViewAction the action to perform.
     */
    public void setOnDropViewAction(OnDropViewAction onDropViewAction) {
        this.onDropViewAction = onDropViewAction;
    }

    /**
     * Destroys a presenter that is currently attached to the view.
     * Use this method if you set {@link #setOnDropViewAction(nucleus.view.OnDropViewAction)} to
     * {@link nucleus.view.OnDropViewAction#NONE}.
     */
    public void destroyPresenter() {
        if (presenter != null) {
            presenter.destroy();
            presenter = null;
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        presenter = PresenterManager.getInstance().provide(this, bundle == null ? null : bundle.getBundle(PRESENTER_STATE_KEY));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter == null)
            presenter = PresenterManager.getInstance().provide(this, null);
        //noinspection unchecked
        presenter.takeView(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.dropView();
        if (onDropViewAction == OnDropViewAction.DESTROY_PRESENTER ||
            (onDropViewAction == OnDropViewAction.DESTROY_PRESENTER_IF_FINISHING && getActivity().isFinishing()))
            destroyPresenter();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBundle(PRESENTER_STATE_KEY, PresenterManager.getInstance().save(presenter));
    }
}
