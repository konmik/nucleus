package mocks;

import android.os.Parcel;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class ParcelMock {

    public static Parcel mock() {

        Parcel parcel = Mockito.mock(Parcel.class);
        final ArrayList<Object> objects = new ArrayList<>();
        final AtomicInteger position = new AtomicInteger();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                objects.add(invocation.getArguments()[0]);
                position.incrementAndGet();
                return null;
            }
        }).when(parcel).writeValue(any());

        when(parcel.marshall()).thenReturn(new byte[0]);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                position.set((Integer) invocation.getArguments()[0]);
                return null;
            }
        }).when(parcel).setDataPosition(anyInt());

        when(parcel.readValue(any(ClassLoader.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return objects.get(position.getAndIncrement());
            }
        });

        return parcel;
    }
}
