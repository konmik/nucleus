package nucleus.example.app;

import android.app.Application;

import nucleus.example.network.NetworkModule;
import nucleus.example.util.ComponentReflectionInjector;
import nucleus.example.util.Injector;

public class App extends Application implements Injector {

    private ComponentReflectionInjector<AppComponent> injector;

    @Override
    public void onCreate() {
        super.onCreate();
        AppComponent component = DaggerAppComponent.builder()
            .networkModule(new NetworkModule())
            .build();
        injector = new ComponentReflectionInjector<>(AppComponent.class, component);
    }

    @Override
    public void inject(Object target) {
        injector.inject(target);
    }
}
