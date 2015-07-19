package nucleus.example.main;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import nucleus.example.R;
import nucleus.example.base.ServerAPI;
import nucleus.view.NucleusActivity;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberMatcher.methods;
import static org.powermock.api.support.membermodification.MemberModifier.replace;
import static org.powermock.api.support.membermodification.MemberModifier.stub;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;


@RunWith(PowerMockRunner.class)
@PrepareForTest({MainActivity.class, NucleusActivity.class})
public class MainActivityTest {

    public static final String TEXT = "test string";

    private static final Class BASE_VIEW_CLASS = NucleusActivity.class;

    @Mock MainPresenter mainPresenter;
    @Mock ArrayAdapter arrayAdapter;
    @Mock CheckedTextView check1;
    @Mock CheckedTextView check2;
    @Mock ListView listView;
    MainActivity activity;

    @Before
    public void before() throws Exception {
        suppress(methods(BASE_VIEW_CLASS, "onCreate"));
        suppress(methods(BASE_VIEW_CLASS, "setContentView"));
        stub(method(BASE_VIEW_CLASS, "getPresenter")).toReturn(mainPresenter);
        replace(method(BASE_VIEW_CLASS, "findViewById")).with(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                switch ((int)args[0]) {
                    case R.id.listView:
                        return listView;
                    case R.id.check1:
                        return check1;
                    case R.id.check2:
                        return check2;
                }
                return null;
            }
        });
        activity = spy(MainActivity.class);
        PowerMockito.whenNew(ArrayAdapter.class).withArguments(activity, R.layout.item).thenReturn(arrayAdapter);
    }

    @Test
    public void testOnCreate() throws Exception {
        activity.onCreate(null);

        verify(check1).setText(MainPresenter.NAME_1);
        verify(check2).setText(MainPresenter.NAME_2);

        verify(mainPresenter).request(MainPresenter.NAME_1);
    }

    @Test
    public void testClicks() throws Exception {
        AtomicReference<View.OnClickListener> click1 = requireOnClick(activity, R.id.check1, check1);
        AtomicReference<View.OnClickListener> click2 = requireOnClick(activity, R.id.check2, check2);
        activity.onCreate(null);

        click1.get().onClick(check1);
        verify(mainPresenter, atLeastOnce()).request(MainPresenter.NAME_1);
        click2.get().onClick(check1);
        verify(mainPresenter, atLeastOnce()).request(MainPresenter.NAME_1);
    }

    @Test
    public void testOnItems() throws Exception {
        MainActivity activity = spy(MainActivity.class);
        PowerMockito.whenNew(ArrayAdapter.class).withArguments(activity, R.layout.item).thenReturn(arrayAdapter);
        activity.onCreate(null);

        ServerAPI.Item[] items = {new ServerAPI.Item(TEXT)};
        activity.onItems(items, "");

        InOrder inOrder = inOrder(arrayAdapter);
        inOrder.verify(arrayAdapter, times(1)).clear();
        inOrder.verify(arrayAdapter, times(1)).addAll(items);
    }

    public AtomicReference<View.OnClickListener> requireOnClick(Activity activityMock, int viewId, View viewMock) {
        final AtomicReference<View.OnClickListener> listenerRef = new AtomicReference<>();
        when(activityMock.findViewById(viewId)).thenReturn(viewMock);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                listenerRef.set((View.OnClickListener)invocation.getArguments()[0]);
                return null;
            }
        }).when(viewMock).setOnClickListener(any(View.OnClickListener.class));
        return listenerRef;
    }
}
