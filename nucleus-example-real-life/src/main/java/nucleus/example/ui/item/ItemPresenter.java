package nucleus.example.ui.item;

import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import icepick.State;
import nucleus.example.network.ServerAPI;
import nucleus.example.ui.base.BasePresenter;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

public class ItemPresenter extends BasePresenter<ItemFragment> {

    public static final int GET_ITEM_REQUEST = 1;

    @Inject ServerAPI api;
    @Inject SharedPreferences pref;

    @State int id;
    @State String name;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_ITEM_REQUEST,
            () -> api.getItem(id, name.split("\\s+")[0], name.split("\\s+")[1])
                .map(it -> it.item)
                .delay(pref.getInt("delay", 0), TimeUnit.SECONDS)
                .subscribeOn(io())
                .observeOn(mainThread()),
            ItemFragment::onItem,
            ItemFragment::onNetworkError);
    }

    void requestItem(int id, String name) {
        this.id = id;
        this.name = name;
        start(GET_ITEM_REQUEST);
    }
}
