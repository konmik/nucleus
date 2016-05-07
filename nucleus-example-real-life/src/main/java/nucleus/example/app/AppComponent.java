package nucleus.example.app;

import javax.inject.Singleton;

import dagger.Component;
import nucleus.example.io.item.ItemPresenter;
import nucleus.example.io.main.MainActivity;
import nucleus.example.io.main.MainPresenter;
import nucleus.example.network.NetworkModule;

@Singleton
@Component(modules = {NetworkModule.class, AppModule.class})
public interface AppComponent {
    void inject(MainPresenter x);
    void inject(ItemPresenter x);
    void inject(MainActivity x);
}
