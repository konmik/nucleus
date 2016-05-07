package nucleus.example.main;

import android.os.Bundle;

import javax.inject.Inject;

import icepick.State;
import nucleus.example.base.BasePresenter;
import nucleus.example.base.ServerAPI;
import rx.android.schedulers.AndroidSchedulers;

public class MainPresenter extends BasePresenter<MainFragment> {

    public static final String NAME_1 = "Chuck Norris";
    public static final String NAME_2 = "Jackie Chan";
    public static final String DEFAULT_NAME = NAME_1;

    private static final int REQUEST_ITEMS = 1;

    @Inject ServerAPI api;

    @State String name;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(REQUEST_ITEMS,
            () -> api
                .getItems(name.split("\\s+")[0], name.split("\\s+")[1])
                .observeOn(AndroidSchedulers.mainThread()),
            (activity, response) -> activity.onItems(response.items, name),
            MainFragment::onNetworkError);
    }

    void request(String name) {
        this.name = name;
        start(REQUEST_ITEMS);
    }
}
