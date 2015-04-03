package nucleus.presenter;

import android.os.Bundle;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This is a base class for all presenters. Subclasses can override
 * {@link #onCreate}, {@link #onDestroy}, {@link #onSave},
 * {@link #onTakeView}, {@link #onDropView}.
 * <p/>
 * {@link nucleus.presenter.Presenter.OnDestroyListener} can also be used by external classes
 * to be notified about the need of freeing resources.
 *
 * @param <ViewType> a type of view to return with {@link #getView()}.
 */
public class Presenter<ViewType> {

    /**
     * This method is intended for overriding.
     * It is being called by {@link nucleus.manager.PresenterManager#provide}
     * method after construction complete.
     *
     * @param savedState If the presenter is being re-instantiated after a process restart then this Bundle
     *                   contains the data it supplied in {@link #onSave}.
     */
    protected void onCreate(Bundle savedState) {
    }

    /**
     * This method is intended for overriding.
     * It is being called by {@link #destroy}
     * after {@link nucleus.presenter.Presenter.OnDestroyListener} listeners are notified about presenter destruction.
     */
    protected void onDestroy() {
    }

    /**
     * This method is intended for overriding.
     * It is being called by {@link nucleus.manager.PresenterManager#save} to save presenter's instance state.
     * Later the state can be passed to {@link #onCreate} for a new presenter instance after a process restart.
     *
     * @param state a non-null bundle which should be used to put presenter's state info.
     */
    protected void onSave(Bundle state) {
    }

    /**
     * This method is intended for overriding.
     * It is being called by parent presenter class, when a view
     * calls {@link nucleus.presenter.Presenter#takeView}
     *
     * @param view a view that should be taken
     */
    protected void onTakeView(ViewType view) {
    }

    /**
     * This method is intended for overriding. Use it to be notified about a view is going to be destroyed.
     */
    protected void onDropView() {
    }

    /**
     * A callback to be invoked when a presenter is about to be destroyed.
     */
    public interface OnDestroyListener {
        /**
         * Called before {@link Presenter#onDestroy()}.
         */
        void onDestroy();
    }

    /**
     * Adds a listener observing {@link #onDestroy}.
     *
     * @param listener a listener to add.
     */
    public void addOnDestroyListener(OnDestroyListener listener) {
        onDestroyListeners.add(listener);
    }

    /**
     * Removed a listener observing {@link #onDestroy}.
     *
     * @param listener a listener to remove.
     */
    public void removeOnDestroyListener(OnDestroyListener listener) {
        onDestroyListeners.remove(listener);
    }

    private ViewType view;
    private CopyOnWriteArrayList<OnDestroyListener> onDestroyListeners = new CopyOnWriteArrayList<>();

    /**
     * Returns a current view attached to presenter.
     *
     * @return a current attached view.
     */
    public ViewType getView() {
        return view;
    }

    /**
     * Creates a presenter.
     * This method is called from {@link nucleus.manager.DefaultPresenterManager#provide} and should not be called directly.
     */
    public void create(Bundle bundle) {
        onCreate(bundle);
    }

    /**
     * Destroys a presenter.
     * This method is called from {@link nucleus.manager.DefaultPresenterManager#destroy(Presenter)} and should not be called directly.
     */
    public void destroy() {
        for (OnDestroyListener listener : onDestroyListeners)
            listener.onDestroy();
        onDestroy();
    }

    /**
     * Saves a presenter.
     * This method is called from {@link nucleus.manager.DefaultPresenterManager#save} and should not be called directly.
     */
    public void save(Bundle state) {
        onSave(state);
    }

    /**
     * Attaches a view to a presenter. Call it from the view, after it has been initialized and its state has been restored.
     * Good places for calling {@link #takeView} are:
     * {@link android.app.Activity#onResume}, {@link android.view.View#onAttachedToWindow}, {@link android.app.Fragment#onResume}
     *
     * @param view a view to attach.
     */
    public void takeView(ViewType view) {
        this.view = view;
        onTakeView(view);
    }

    /**
     * Detaches a presenter from a view. Call it for a view, at the beginning of the destruction phase.
     * Good places for calling {@link #dropView} are:
     * {@link android.app.Activity#onPause}, {@link android.view.View#onDetachedFromWindow}, {@link android.app.Fragment#onPause}
     */
    public void dropView() {
        onDropView();
        this.view = null;
    }

    /**
     * Returns a number of onDestroy listeners.
     *
     * @return a number of onDestroy listeners.
     * @hide testing facility
     */
    public int onDestroyListenerCount() {
        return onDestroyListeners.size();
    }
}
