package nucleus.example.base;

import android.util.Log;
import dagger.Provides;
import nucleus.example.main.MainActivity;
import nucleus.example.main.MainPresenter;
import nucleus.example.network.ServerAPI;
import nucleus.example.network.ItemsLoader;
import retrofit.RestAdapter;

import javax.inject.Singleton;

@dagger.Module(injects = {
    MainPresenter.class,
    MainActivity.class,
    ItemsLoader.class
})
public class AppModule {

    App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    ServerAPI provideServerAPI() {
        return new RestAdapter.Builder()
            .setEndpoint(ServerAPI.ENDPOINT)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setLog(new RestAdapter.Log() {
                @Override
                public void log(String message) {
                    Log.v("Retrofit", message);
                }
            })
            .build().create(ServerAPI.class);
    }
}
