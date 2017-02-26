package nucleus.example.base;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import nucleus.example.main.MainPresenter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(injects = MainPresenter.class)
public class AppModule {
    @Singleton
    @Provides
    ServerAPI provideServerAPI() {
        return new Retrofit.Builder()
            .baseUrl(ServerAPI.ENDPOINT)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServerAPI.class);
    }

    @Provides
    @Singleton
    @MainThread
    Scheduler provideMainScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @Singleton
    @IoThread
    Scheduler provideIoScheduler() {
        return Schedulers.io();
    }
}
