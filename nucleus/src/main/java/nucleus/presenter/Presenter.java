package nucleus.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This is a base class for all presenters. Subclasses can override
 * {@link #onCreate}, {@link #onDestroy}, {@link #onSave},
 * {@link #onTakeView}, {@link #onDropView}.
 *
 * A developer should normally not use this class directly, use {@link RxPresenter} instead.
 *
 * @param <ViewType> a type of view to receive with {@link #onTakeView}}.
 */
public class Presenter<ViewType> {

    private CopyOnWriteArrayList<OnDestroyListener> onDestroyListeners = new CopyOnWriteArrayList<>();

    /**
     * A callback to be invoked when a presenter is about to be destroyed.
     */
    public interface OnDestroyListener {
        /**
         * Called before {@link Presenter#onDestroy}.
         */
        void onDestroy();
    }

    /**
     * This method is intended for overriding.
     * It is being called by {@link nucleus.factory.PresenterFactory}
     * method after construction completes.
     *
     * @param savedState If the presenter is being re-instantiated after a process restart then this Bundle
     *                   contains the data it supplied in {@link #onSave}.
     */
    public void onCreate(@Nullable Bundle savedState) {
    }

    /**
     * This method is intended for overriding.
     * This method should be called by View during it's final destruction.
     * The method can also be called by a view manager in case if the presenter will never become attached again.
     */
    public void onDestroy() {
        for (OnDestroyListener listener : onDestroyListeners)
            listener.onDestroy();
    }

    /**
     * This method is intended for overriding.
     * The returned state is the state that will be passed to {@link #onCreate} for a new presenter instance after a process restart.
     *
     * @param state a non-null bundle which should be used to put presenter's state into.
     */
    public void onSave(Bundle state) {
    }

    /**
     * This method is intended for overriding.
     * It is being called by parent presenter class, when a view
     * calls {@link nucleus.presenter.Presenter#onTakeView}
     *
     * @param view a view that should be taken
     */
    public void onTakeView(ViewType view) {
    }

    /**
     * This method is intended for overriding. Use it to be notified about a view is going to be destroyed.
     */
    public void onDropView() {
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
}
