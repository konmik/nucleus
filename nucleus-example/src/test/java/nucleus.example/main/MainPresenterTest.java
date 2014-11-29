package nucleus.example.main;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import nucleus.example.base.Injector;
import nucleus.example.network.ItemsLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MainPresenterTest {

    @Module(injects = MainPresenter.class)
    public static class MainPresenterModule {
        @Provides
        ItemsLoader provideItemsLoader() {
            return Mockito.mock(ItemsLoader.class);
        }
    }

    @Test
    public void testCounter() throws Exception {
        Injector.setGraph(ObjectGraph.create(new MainPresenterModule()));
        MainPresenter presenter = new MainPresenter();
        presenter.onCreate(null);

        MainActivity activity = Mockito.mock(MainActivity.class);
        presenter.takeView(activity);
        presenter.dropView(activity);

        verify(activity, times(1)).publishCounter(1);

        presenter.takeView(activity);
        presenter.dropView(activity);

        verify(activity, times(1)).publishCounter(2);
        verify(activity, times(2)).publishCounter(anyInt());
    }
}