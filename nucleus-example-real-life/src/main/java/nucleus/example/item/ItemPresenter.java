package nucleus.example.item;

import android.os.Bundle;

import javax.inject.Inject;

import icepick.State;
import nucleus.example.base.BasePresenter;
import nucleus.example.base.ServerAPI;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

public class ItemPresenter extends BasePresenter<ItemFragment> {

    public static final int GET_ITEM_REQUEST = 1;

    @Inject ServerAPI api;

    @State int id;
    @State String name;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_ITEM_REQUEST,
            () -> api.getItem(name.split("\\s+")[0], name.split("\\s+")[1], id)
                .map(it -> it.item)
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
