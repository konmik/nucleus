package nucleus.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import nucleus.manager.PresenterManager;
import nucleus.presenter.Presenter;

/**
 * This view is an example of how a view should control it's presenter.
 * You can inherit from this class or copy/paste this class's code to
 * create your own view implementation.
 *
 * @param <PresenterType> a type of presenter to return with {@link #getPresenter}.
 */
public abstract class NucleusLayout<PresenterType extends Presenter> extends FrameLayout {

    private static final String PARENT_STATE_KEY = "parent_state";

    private Activity activity;

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
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putBundle(PRESENTER_STATE_KEY, savePresenter());
        bundle.putParcelable(PARENT_STATE_KEY, super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle)state;
        super.onRestoreInstanceState(bundle.getParcelable(PARENT_STATE_KEY));
        requestPresenter(bundle.getBundle(PRESENTER_STATE_KEY));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            activity = (Activity)getContext();
            takeView();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dropView(activity);
    }

    // The following section can be copy & pasted into any View class, just update their description if needed.

    private static final String PRESENTER_STATE_KEY = "presenter_state";
    private PresenterType presenter;

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