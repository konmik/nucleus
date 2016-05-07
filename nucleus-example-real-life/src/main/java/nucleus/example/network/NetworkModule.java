package nucleus.example.network;

import android.util.Log;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module
public class NetworkModule {
    @Singleton
    @Provides
    ServerAPI provideServerApi() {
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
