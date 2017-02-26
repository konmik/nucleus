package nucleus.example.main;

import android.os.Bundle;
import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import nucleus.example.base.App;
import nucleus.example.base.ServerAPI;
import nucleus5.presenter.Factory;
import nucleus5.presenter.RxPresenter;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

public class MainPresenter extends RxPresenter<MainActivity> {

    public static final String NAME_1 = "Chuck Norris";
    public static final String NAME_2 = "Jackie Chan";
    public static final String DEFAULT_NAME = NAME_1;

    private static final int REQUEST_ITEMS = 1;

    private static final String NAME_KEY = MainPresenter.class.getName() + "#name";

    private String name = DEFAULT_NAME;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (savedState != null)
            name = savedState.getString(NAME_KEY);

        restartableLatestCache(REQUEST_ITEMS,
            new Factory<Observable<ServerAPI.Response>>() {
                @Override
                public Observable<ServerAPI.Response> create() {
                    return App.getServerAPI()
                        .getItems(name.split("\\s+")[0], name.split("\\s+")[1])
                        .subscribeOn(io())
                        .observeOn(mainThread());
                }
            },
            new BiConsumer<MainActivity, ServerAPI.Response>() {
                @Override
                public void accept(MainActivity activity, ServerAPI.Response response) throws Exception {
                    activity.onItems(response.items, name);
                }
            },
            new BiConsumer<MainActivity, Throwable>() {
                @Override
                public void accept(MainActivity activity, Throwable throwable) throws Exception {
                    activity.onNetworkError(throwable);
                }
            });

        if (savedState == null)
            start(REQUEST_ITEMS);
    }

    @Override
    public void onSave(@NonNull Bundle state) {
        super.onSave(state);
        state.putString(NAME_KEY, name);
    }

    public void request(String name) {
        this.name = name;
        start(REQUEST_ITEMS);
    }
}
