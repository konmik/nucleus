package nucleus.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import nucleus.presenter.Presenter;
import nucleus.presenter.PresenterManager;

public class NucleusLayout<PresenterType extends Presenter> extends FrameLayout implements PresenterProvider<PresenterType> {

    public enum OnDetachedAction {NONE, DESTROY_PRESENTER, DESTROY_PRESENTER_IF_FINISHING}

    private static final String PRESENTER_STATE_KEY = "presenter_state";
    private static final String PARENT_STATE_KEY = "parent_state";

    private PresenterType presenter;

    private Activity activity;

    private OnDetachedAction onDetachedAction = OnDetachedAction.DESTROY_PRESENTER_IF_FINISHING;

    public NucleusLayout(Context context) {
        super(context);
    }

    public NucleusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NucleusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public PresenterType getPresenter() {
        return presenter;
    }

    public void setOnDetachedAction(OnDetachedAction onDetachedAction) {
        this.onDetachedAction = onDetachedAction;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle)state;
        super.onRestoreInstanceState(bundle.getParcelable(PARENT_STATE_KEY));
        presenter = PresenterManager.getInstance().provide(this, bundle.getBundle(PRESENTER_STATE_KEY));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            activity = (Activity)getContext();
            if (presenter == null)
                presenter = PresenterManager.getInstance().provide(this, null);
            //noinspection unchecked
            presenter.takeView(this);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putBundle(PRESENTER_STATE_KEY, PresenterManager.getInstance().save(presenter));
        bundle.putParcelable(PARENT_STATE_KEY, super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView();
        if (onDetachedAction == OnDetachedAction.DESTROY_PRESENTER ||
            (onDetachedAction == OnDetachedAction.DESTROY_PRESENTER_IF_FINISHING && activity.isFinishing()))
            destroyPresenter();
    }

    // could be called for a view with a life cycle different to Activity
    public void destroyPresenter() {
        if (presenter != null) {
            presenter.destroy();
            presenter = null;
        }
    }
}
