package nucleus.example.main;

import android.os.Bundle;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import nucleus.example.base.App;
import nucleus.example.base.MainThread;
import nucleus.example.base.ServerAPI;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.Scheduler;
import rx.functions.BiConsumer;
import rx.functions.SilentCallable;

public class MainPresenter extends RxPresenter<MainActivity> {

    public static final String NAME_1 = "Chuck Norris";
    public static final String NAME_2 = "Jackie Chan";
    public static final String DEFAULT_NAME = NAME_1;

    private static final int REQUEST_ITEMS = 1;

    private static final String NAME_KEY = "name";

    @Inject ServerAPI api;
    @Inject @MainThread Scheduler scheduler;

    private String name = DEFAULT_NAME;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        App.inject(this);

        if (savedState != null)
            name = savedState.getString(NAME_KEY);

        restartableLatestCache(REQUEST_ITEMS,
            new SilentCallable<Observable<ServerAPI.Response>>() {
                @Override
                public Observable<ServerAPI.Response> call() {
                    return api
                        .getItems(name.split("\\s+")[0], name.split("\\s+")[1])
                        .observeOn(scheduler);
                }
            },
            new BiConsumer<MainActivity, ServerAPI.Response>() {
                @Override
                public void call(MainActivity activity, ServerAPI.Response response) {
                    activity.onItems(response.items, name);
                }
            },
            new BiConsumer<MainActivity, Throwable>() {
                @Override
                public void call(MainActivity activity, Throwable throwable) {
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