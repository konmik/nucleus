package nucleus.example.network;

import retrofit.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ItemsLoader extends RetrofitLoader<ServerAPI.Response> {

    private String name;

    @Inject
    public ItemsLoader() {
    }

    public void request(String name) {
        this.name = name;
        request();
    }

    @Override
    protected void doRequest(Callback<ServerAPI.Response> callback) {
        api.getItems(name.split("\\s+")[0], name.split("\\s+")[1], callback);
    }
}
