package nucleus.example.app;

import javax.inject.Singleton;

import dagger.Component;
import nucleus.example.item.ItemPresenter;
import nucleus.example.main.MainActivity;
import nucleus.example.main.MainPresenter;
import nucleus.example.network.NetworkModule;

@Singleton
@Component(modules = {NetworkModule.class, AppModule.class})
public interface AppComponent {
    void inject(MainPresenter x);
    void inject(ItemPresenter x);
    void inject(MainActivity x);
}
