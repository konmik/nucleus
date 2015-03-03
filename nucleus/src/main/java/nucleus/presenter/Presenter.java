package nucleus.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.CopyOnWriteArrayList;

public class Presenter<ViewType> {

    /**
     * Callback to be invoked when a presenter is about to be destroyed.
     */
    public interface OnDestroyListener {
        /**
         * Called before presenter's destruction.
         */
        void onDestroy();
    }

    private ViewType view;
    private CopyOnWriteArrayList<OnDestroyListener> onDestroyListeners = new CopyOnWriteArrayList<>();

    /**
     * Returns a current attached to presenter view.
     *
     * @return a current attached view.
     */
    public ViewType getView() {
        return view;
    }

    /**
     * Destroys a presenter.
     * This method is called from {@link nucleus.presenter.PresenterManager#destroy(Presenter)} and should not be called directly.
     */
    public void destroy() {
        for (OnDestroyListener listener : onDestroyListeners)
            listener.onDestroy();
        onDestroy();
    }

    /**
     * Attaches a view to presenter. Call it from a view, after a view has been initialized and its state has been restored.
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
     * Detaches presenter from a view. Call it for a view, at the beginning of the destruction phase.
     * Good places for calling {@link #dropView} are:
     * {@link android.app.Activity#onPause}, {@link android.view.View#onDetachedFromWindow}, {@link android.app.Fragment#onPause}
     */
    public void dropView() {
        onDropView();
        this.view = null;
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
     * @param listener a listener to add.
     */
    public void removeOnDestroyListener(OnDestroyListener listener) {
        onDestroyListeners.remove(listener);
    }

    /**
     * This method is intended for overriding. It is being called by {@link PresenterManager#provide}
     * method after construction complete.
     *
     * @param savedState If the presenter is being re-initialized then this Bundle contains the data it most
     *                   recently supplied in {@link #onSave}.
     */
    protected void onCreate(@Nullable Bundle savedState) {
    }

    /**
     * This method is intended for overriding. It is being called by {@link #destroy}
     * after {@link nucleus.presenter.Presenter.OnDestroyListener} listeners are notified about it
     * but before actual destruction happens but .
     */
    protected void onDestroy() {
    }

    /**
     * This method is intended for overriding.
     * It is being called by {@link nucleus.presenter.PresenterManager#save} to save presenter's instance state.
     * Later the state can be passed in {@link #onCreate} for a new presenter.
     *
     * @param state a non-null bundle which should be used to put presenter's state info.
     */
    protected void onSave(@NonNull Bundle state) {
    }

    /**
     * This method is intended for overriding. It is being called by parent presenter class, when view
     * calls {@link nucleus.presenter.Presenter#takeView}
     *
     * @param view a view that should be taken
     */
    protected void onTakeView(ViewType view) {
    }

    /**
     * This method is intended for overriding. Use it to be notified about view going to be destroyed.
     */
    protected void onDropView() {
    }
}
