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
    public Subscriber<? super T> call(final Subscriber<? super T> child) {
        this.destinationSubscriber = child;
        return this.createdSubscriber = new Subscriber<T>() {
            @Override
            public void onStart() {
                super.onStart();
                child.add(this);
            }

            @Override
            public void onCompleted() {
                onCompleted = true;
                child.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                onError = e;
                child.onError(e);
            }

            @Override
            public void onNext(T t) {
                onNext.add(t);
                child.onNext(t);
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
