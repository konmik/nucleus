package nucleus.presenter.delivery;

public enum DeliveryRule {
    /**
     * Only the first notification will be delivered and only once.
     */
    ONCE,

    /**
     * Only the latest notification will be saved in memory and delivered each time a new view gets attached.
     * If a new item is received while a view is attached it will be delivered immediately.
     */
    CACHE,

    /**
     * All notifications will be saved in memory and replayed every time a view is being attached.
     */
    REPLAY
}
