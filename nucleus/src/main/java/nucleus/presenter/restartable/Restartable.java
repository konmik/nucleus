package nucleus.presenter.restartable;

import rx.Subscription;

public abstract class Restartable {
    // the implementing class is obligated to unsubscribe all subscriptions once this subscription in unsubscribed
    public abstract Subscription start();
}
