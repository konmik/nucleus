package mock;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Matchers.anyChar;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public final class BundleMock {

    public static Bundle mock() {
        final HashMap<String, Object> map = new HashMap<>();

        Answer unsupported = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new UnsupportedOperationException();
            }
        };
        Answer put = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                map.put((String)invocation.getArguments()[0], invocation.getArguments()[1]);
                return null;
            }
        };
        Answer<Object> get = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return map.get(invocation.getArguments()[0]);
            }
        };
        Answer<Object> getOrDefault = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object key = invocation.getArguments()[0];
                return map.containsKey(key) ? map.get(key) : invocation.getArguments()[1];
            }
        };

        Bundle bundle = Mockito.mock(Bundle.class);

        doAnswer(unsupported).when(bundle).putAll(any(Bundle.class));
        when(bundle.hasFileDescriptors()).thenAnswer(unsupported);

        doAnswer(put).when(bundle).putByte(anyString(), anyByte());
        when(bundle.getByte(anyString())).thenAnswer(get);
        when(bundle.getByte(anyString(), anyByte())).thenAnswer(getOrDefault);

        doAnswer(put).when(bundle).putChar(anyString(), anyChar());
        when(bundle.getChar(anyString())).thenAnswer(get);
        when(bundle.getChar(anyString(), anyChar())).thenAnswer(getOrDefault);

        doAnswer(put).when(bundle).putShort(anyString(), anyShort());
        when(bundle.getShort(anyString())).thenAnswer(get);
        when(bundle.getShort(anyString(), anyShort())).thenAnswer(getOrDefault);

        doAnswer(put).when(bundle).putFloat(anyString(), anyFloat());
        when(bundle.getFloat(anyString())).thenAnswer(get);
        when(bundle.getFloat(anyString(), anyFloat())).thenAnswer(getOrDefault);

        doAnswer(put).when(bundle).putCharSequence(anyString(), any(CharSequence.class));
        when(bundle.getCharSequence(anyString())).thenAnswer(get);
        when(bundle.getCharSequence(anyString(), any(CharSequence.class))).thenAnswer(getOrDefault);

        doAnswer(put).when(bundle).putBundle(anyString(), any(Bundle.class));
        when(bundle.getBundle(anyString())).thenAnswer(get);

        doAnswer(put).when(bundle).putParcelable(anyString(), any(Parcelable.class));
        when(bundle.getParcelable(anyString())).thenAnswer(get);

        doAnswer(put).when(bundle).putParcelableArray(anyString(), any(Parcelable[].class));
        when(bundle.getParcelableArray(anyString())).thenAnswer(get);

        doAnswer(put).when(bundle).putParcelableArrayList(anyString(), any(ArrayList.class));
        when(bundle.getParcelableArrayList(anyString())).thenAnswer(get);

        doAnswer(put).when(bundle).putSparseParcelableArray(anyString(), any(SparseArray.class));
        when(bundle.getSparseParcelableArray(anyString())).thenAnswer(get);

        doAnswer(put).when(bundle).putSerializable(anyString(), any(Serializable.class));
        when(bundle.getSerializable(anyString())).thenAnswer(get);

        doAnswer(put).when(bundle).putIntegerArrayList(anyString(), any(ArrayList.class));
        when(bundle.getIntegerArrayList(anyString())).thenAnswer(get);

        doAnswer(put).when(bundle).putStringArrayList(anyString(), any(ArrayList.class));
        when(bundle.getStringArrayList(anyString())).thenAnswer(get);

        doAnswer(put).when(bundle).putCharSequenceArrayList(anyString(), any(ArrayList.class));
        when(bundle.getCharSequenceArrayList(anyString())).thenAnswer(get);

        doAnswer(put).when(bundle).putCharArray(anyString(), any(char[].class));
        when(bundle.getCharArray(anyString())).thenAnswer(get);

        doAnswer(put).when(bundle).putByteArray(anyString(), any(byte[].class));
        when(bundle.getByteArray(anyString())).thenAnswer(get);

        doAnswer(put).when(bundle).putShortArray(anyString(), any(short[].class));
        when(bundle.getShortArray(anyString())).thenAnswer(get);

        doAnswer(put).when(bundle).putFloatArray(anyString(), any(float[].class));
        when(bundle.getFloatArray(anyString())).thenAnswer(get);

        doAnswer(put).when(bundle).putCharSequenceArray(anyString(), any(CharSequence[].class));
        when(bundle.getCharSequenceArray(anyString())).thenAnswer(get);

        return bundle;
    }
}
