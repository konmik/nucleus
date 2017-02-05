package nucleus.example.base;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import nucleus.example.main.MainPresenter;
import retrofit.RestAdapter;

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
    @MainThread
    Scheduler provideMainScheduler() {
        return AndroidSchedulers.mainThread();
    }
}
