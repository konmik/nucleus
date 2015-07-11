package nucleus.presenter.restartable;

import junit.framework.TestCase;

import rx.Notification;
import rx.functions.Action2;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class DeliveryTest extends TestCase {

    private void testWithOnNextOnError(Action2<Action2, Action2> test) {
        Action2 onNext = mock(Action2.class);
        Action2 onError = mock(Action2.class);

        test.call(onNext, onError);

        verifyNoMoreInteractions(onNext);
        verifyNoMoreInteractions(onError);
    }

    public void testSplitOnNext() throws Exception {
        testWithOnNextOnError(new Action2<Action2, Action2>() {
            @Override
            public void call(Action2 onNext, Action2 onError) {
                new Delivery(1, Notification.createOnNext(2)).split(onNext, onError);
                verify(onNext, times(1)).call(1, 2);
            }
        });
    }

    public void testSplitOnError() throws Exception {
        testWithOnNextOnError(new Action2<Action2, Action2>() {
            @Override
            public void call(Action2 onNext, Action2 onError) {
                Throwable throwable = new Throwable();
                new Delivery(1, Notification.createOnError(throwable)).split(onNext, onError);
                verify(onError, times(1)).call(1, throwable);
            }
        });
    }

    public void testSplitOnComplete() throws Exception {
        testWithOnNextOnError(new Action2<Action2, Action2>() {
            @Override
            public void call(Action2 onNext, Action2 onError) {
                new Delivery(1, Notification.createOnCompleted()).split(onNext, onError);
            }
        });
    }
}
