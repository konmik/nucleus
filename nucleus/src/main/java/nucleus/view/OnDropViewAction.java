package nucleus.view;

/**
 * An action that should be performed during onDetachedFromWindow call.
 */
public enum OnDropViewAction {
    /**
     * No action should be taken. Manage presenter's destruction by external means.
     * Call view's destroyPresenter() to destroy a presenter manually.
     */
    NONE,

    /**
     * Destroys a presenter during onDetachedFromWindow call.
     */
    DESTROY_PRESENTER,

    /**
     * Destroys a presenter during onDetachedFromWindow call if activity is finishing.
     * (The default behavior)
     */
    DESTROY_PRESENTER_IF_FINISHING
}
