package nucleus.example.base;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nucleus.example.main.Main;
import nucleus.example.main.MainPresenter;
import retrofit.RestAdapter;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

import static android.util.Log.v;

@Module(injects = MainPresenter.class)
public class AppModule {
    @Singleton
    @Provides
    ServerAPI provideServerAPI() {
        return new RestAdapter.Builder()
            .setEndpoint(ServerAPI.ENDPOINT)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setLog(new RestAdapter.Log() {
                @Override
                public void log(String message) {
                    v("Retrofit", message);
                }
            })
            .build().create(ServerAPI.class);
    }

    @Provides
    @Singleton
    @Main
    Scheduler provideMainScheduler() {
        return AndroidSchedulers.mainThread();
    }
}
