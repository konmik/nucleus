package nucleus.presenter.delivery;

import android.support.annotation.Nullable;

import rx.functions.Action1;
import rx.functions.Action2;

public final class DeliverDelivery<ViewType, T> implements Action1<Delivery<ViewType, T>> {

    private final Action2<ViewType, T> onNext;
    @Nullable private final Action2<ViewType, Throwable> onError;

    public DeliverDelivery(Action2<ViewType, T> onNext) {
        this(onNext, null);
    }

    public DeliverDelivery(Action2<ViewType, T> onNext, @Nullable Action2<ViewType, Throwable> onError) {
        this.onNext = onNext;
        this.onError = onError;
    }

    @Override
    public void call(Delivery<ViewType, T> delivery) {
        delivery.split(onNext, onError);
    }
}
