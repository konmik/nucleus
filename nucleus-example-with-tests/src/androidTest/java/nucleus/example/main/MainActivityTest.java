package nucleus.example.main;

import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import nucleus.example.base.ServerAPI;
import nucleus.factory.PresenterFactory;
import nucleus.manager.PresenterManager;
import nucleus.presenter.Presenter;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public static final String TEXT = "test string";

    public MainActivityTest() {
        super(MainActivity.class);
    }

    MainPresenter mainPresenterMock;

    public void testOnItems() throws Throwable {

        // mock presenter

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                substitutePresenter(mainPresenterMock = mockPresenter());
            }
        });

        // run

        getActivity();

        // check

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                verify(mainPresenterMock).request(anyString());
            }
        });
        Solo solo = new Solo(getInstrumentation(), getActivity());
        assertTrue(solo.waitForText(TEXT, 1, 1000));
    }

    private MainPresenter mockPresenter() {
        MainPresenter mock = mock(MainPresenter.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                MainActivity activity = (MainActivity)invocation.getArguments()[0];
                activity.onItems(new ServerAPI.Item[]{new ServerAPI.Item(TEXT)}, "");
                return null;
            }
        }).when(mock).takeView(any(MainActivity.class));
        return mock;
    }

    private void substitutePresenter(final Presenter mockPresenter) {
        PresenterManager mockManager = mock(PresenterManager.class);
        when(mockManager.provide(any(PresenterFactory.class), any(Bundle.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return mockPresenter;
            }
        });
        PresenterManager.setInstance(mockManager);
    }
}