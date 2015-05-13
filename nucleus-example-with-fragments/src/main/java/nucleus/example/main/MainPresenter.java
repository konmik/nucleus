package nucleus.example.main;

import android.os.Bundle;
import android.support.annotation.NonNull;

import nucleus.example.base.App;
import nucleus.example.base.ServerAPI;
import nucleus.example.logging.LoggingPresenter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;

public class MainPresenter extends LoggingPresenter<MainFragment> {

    public static final String NAME_1 = "Chuck Norris";
    public static final String NAME_2 = "Jackie Chan";
    public static final String DEFAULT_NAME = NAME_1;

    private static final int REQUEST_ITEMS = 1;

    private static final String NAME_KEY = "name";

    private String name = DEFAULT_NAME;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (savedState != null)
            name = savedState.getString(NAME_KEY);

        registerRestartable(REQUEST_ITEMS, new Func0<Subscription>() {
            @Override
            public Subscription call() {
                final String name1 = name;
                return App.getServerAPI()
                    .getItems(name.split("\\s+")[0], name.split("\\s+")[1])
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(MainPresenter.this.<ServerAPI.Response>deliverLatestCache())
                    .subscribe(new Action1<ServerAPI.Response>() {
                        @Override
                        public void call(ServerAPI.Response response) {
                            getView().onItems(response.items, name1);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            getView().onNetworkError(throwable);
                        }
                    });
            }
        });
    }

    @Override
    protected void onSave(@NonNull Bundle state) {
        super.onSave(state);
        state.putString(NAME_KEY, name);
    }

    public void request(String name) {
        this.name = name;
        subscribeRestartable(REQUEST_ITEMS);
    }
}
