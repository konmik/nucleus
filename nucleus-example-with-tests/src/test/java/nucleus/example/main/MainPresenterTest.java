package nucleus.example.main;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.TestScheduler;
import nucleus.example.base.App;
import nucleus.example.base.MainThread;
import nucleus.example.base.ServerAPI;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainPresenterTest {

    private static final String TEST_TEXT = "test text";
    public static final String FIRST_NAME = "Marilyn";
    public static final String LAST_NAME = "Manson";

    @Module(injects = MainPresenter.class)
    public class MainPresenterTestModule {
        @Singleton
        @Provides
        ServerAPI provideServerAPI() {
            return serverAPIMock;
        }

        @Singleton
        @Provides
        @MainThread
        Scheduler provideScheduler() {
            return testScheduler;
        }
    }

    ServerAPI serverAPIMock;
    TestScheduler testScheduler;

    @Test
    public void testRequest() throws Throwable {
        createServerApiMock();
        createTestScheduler();
        App.setObjectGraph(ObjectGraph.create(new MainPresenterTestModule()));

        MainPresenter presenter = new MainPresenter();
        presenter.onCreate(null);
        presenter.request(FIRST_NAME + " " + LAST_NAME);

        MainActivity mainActivity = mock(MainActivity.class);
        presenter.takeView(mainActivity);

        testScheduler.triggerActions();

        verify(serverAPIMock).getItems(FIRST_NAME, LAST_NAME);
        verify(mainActivity).onItems(argThat(new ArgumentMatcher<ServerAPI.Item[]>() {
            @Override
            public boolean matches(Object argument) {
                return ((ServerAPI.Item[])argument)[0].text.equals(TEST_TEXT);
            }
        }), anyString());
    }

    private void createServerApiMock() {
        serverAPIMock = mock(ServerAPI.class);
        when(serverAPIMock.getItems(anyString(), anyString())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ServerAPI.Response response = new ServerAPI.Response();
                response.items = new ServerAPI.Item[]{new ServerAPI.Item(TEST_TEXT)};
                return Observable.just(response);
            }
        });
    }

    private void createTestScheduler() {
        testScheduler = new TestScheduler();
    }
}