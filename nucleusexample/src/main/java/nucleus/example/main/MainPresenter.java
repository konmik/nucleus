package nucleus.example.main;

import android.os.Bundle;
import android.support.annotation.NonNull;

import nucleus.example.base.App;
import nucleus.example.base.ServerAPI;
import nucleus.presenter.RxPresenter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;

public class MainPresenter extends RxPresenter<MainActivity> {

    public static final String NAME_1 = "Chuck Norris";
    public static final String NAME_2 = "Jackie Chan";
    public static final String DEFAULT_NAME = NAME_1;

    private static final String NAME_KEY = "name";
    private static final String COUNTER_KEY = "counter";

    private static final int QUERY_ITEMS = 1;

    private int counter;

    private String name = DEFAULT_NAME;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        if (savedState != null) {
            name = savedState.getString(NAME_KEY);
            counter = savedState.getInt(COUNTER_KEY);
        }

        registerQuery(QUERY_ITEMS, new Func0<Subscription>() {
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
                            getView().publishItems(response.items, name1);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {

                        }
                    });
            }
        });

        if (savedState == null)
            subscribeQuery(QUERY_ITEMS);
    }

    @Override
    protected void onSave(@NonNull Bundle state) {
        super.onSave(state);
        state.putString(NAME_KEY, name);
        state.putInt(COUNTER_KEY, counter);
    }

    public void toggleTo(String name) {
        this.name = name;
        subscribeQuery(QUERY_ITEMS);
    }

    @Override
    protected void onTakeView(MainActivity view) {
        super.onTakeView(view);
        getView().publishCounter(++counter);
    }
}