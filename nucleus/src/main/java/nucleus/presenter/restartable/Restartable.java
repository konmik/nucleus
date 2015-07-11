package nucleus.presenter.restartable;

import rx.Subscription;
import rx.functions.Func0;

public interface Restartable extends Func0<Subscription> {
    // the implementing class is obligated to unsubscribe all subscriptions once returned subscription in unsubscribed
}
