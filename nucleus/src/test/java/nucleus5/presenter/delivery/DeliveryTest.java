package nucleus5.presenter.delivery;

import org.junit.Test;

import io.reactivex.Notification;
import io.reactivex.functions.BiConsumer;
import nucleus5.presenter.delivery.Delivery;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class DeliveryTest {

    private void testWithOnNextOnError(BiConsumer<BiConsumer, BiConsumer> test) throws Exception {
        BiConsumer onNext = mock(BiConsumer.class);
        BiConsumer onError = mock(BiConsumer.class);

        test.accept(onNext, onError);

        verifyNoMoreInteractions(onNext);
        verifyNoMoreInteractions(onError);
    }

    @Test
    public void testSplitOnNext() throws Exception {
        testWithOnNextOnError(new BiConsumer<BiConsumer, BiConsumer>() {
            @Override
            public void accept(BiConsumer onNext, BiConsumer onError) throws Exception {
                new Delivery(1, Notification.createOnNext(2)).split(onNext, onError);
                verify(onNext, times(1)).accept(1, 2);
            }
        });
    }

    @Test
    public void testSplitOnError() throws Exception {
        testWithOnNextOnError(new BiConsumer<BiConsumer, BiConsumer>() {
            @Override
            public void accept(BiConsumer onNext, BiConsumer onError) throws Exception {
                Throwable throwable = new Throwable();
                new Delivery(1, Notification.createOnError(throwable)).split(onNext, onError);
                verify(onError, times(1)).accept(1, throwable);
            }
        });
    }

    @Test
    public void testSplitOnComplete() throws Exception {
        testWithOnNextOnError(new BiConsumer<BiConsumer, BiConsumer>() {
            @Override
            public void accept(BiConsumer onNext, BiConsumer onError) throws Exception {
                new Delivery(1, Notification.createOnComplete()).split(onNext, onError);
            }
        });
    }
}
