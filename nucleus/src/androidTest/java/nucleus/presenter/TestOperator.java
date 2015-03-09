package nucleus.presenter;

import android.util.Log;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;

public class TestOperator<T> implements Observable.Operator<T, T> {

    public Subscriber<? super T> destinationSubscriber;
    public Subscriber<T> createdSubscriber;
    public ArrayList<T> onNext = new ArrayList<>();
    public Throwable onError;
    public boolean onCompleted;

    @Override
    public Subscriber<? super T> call(final Subscriber<? super T> subscriber) {
        this.destinationSubscriber = subscriber;
        return this.createdSubscriber = new Subscriber<T>() {
            @Override
            public void onCompleted() {
                onCompleted = true;
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                onError = e;
                subscriber.onError(e);
            }

            @Override
            public void onNext(T t) {
                onNext.add(t);
                subscriber.onNext(t);
            }
        };
    }

    public void print(String tag) {
        Log.v(tag, String.format("destination is subscribed: %b, created is subscribed: %b, onNext: %d, onError: %s, onCompleted: %b",
            !destinationSubscriber.isUnsubscribed(), !createdSubscriber.isUnsubscribed(), onNext.size(), onError != null, onCompleted));
    }

    @Override
    public String toString() {
        return "TestOperator{" +
            "destinationSubscriber=" + destinationSubscriber +
            ", createdSubscriber=" + createdSubscriber +
            ", onNext=" + onNext +
            ", onError=" + onError +
            ", onCompleted=" + onCompleted +
            '}';
    }
}
