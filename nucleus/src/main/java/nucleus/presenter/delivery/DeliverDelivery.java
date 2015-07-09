package nucleus.presenter.delivery;

import android.support.annotation.Nullable;

import rx.Observer;
import rx.functions.Action2;

public final class DeliverDelivery<ViewType, T> implements Observer<Delivery<ViewType, T>> {

    private final Action2<ViewType, T> onNext;
    @Nullable private final Action2<ViewType, Throwable> onError;

    public DeliverDelivery(Action2<ViewType, T> onNext, @Nullable Action2<ViewType, Throwable> onError) {
        this.onNext = onNext;
        this.onError = onError;
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onNext(Delivery<ViewType, T> tDelivery) {
        tDelivery.split(onNext, onError);
    }
}
