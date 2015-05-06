package nucleus.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import nucleus.presenter.Presenter;
import nucleus.factory.PresenterFactory;
import nucleus.factory.ReflectionPresenterFactory;

/**
 * This view is an example of how a view should control it's presenter.
 * You can inherit from this class or copy/paste this class's code to
 * create your own view implementation.
 *
 * @param <PresenterType> a type of presenter to return with {@link #getPresenter}.
 */
public abstract class NucleusLayout<PresenterType extends Presenter> extends FrameLayout {

    private static final String PARENT_STATE_KEY = "parent_state";

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
        bundle.putBundle(PRESENTER_STATE_KEY, helper.savePresenter());
        bundle.putParcelable(PARENT_STATE_KEY, super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle)state;
        super.onRestoreInstanceState(bundle.getParcelable(PARENT_STATE_KEY));
        helper.requestPresenter(getPresenterFactory(), bundle.getBundle(PRESENTER_STATE_KEY));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode())
            helper.takeView(this, getPresenterFactory(), (Activity)getContext());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
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
     * onAttachedToWindow/onDetachedFromWindow calls.
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