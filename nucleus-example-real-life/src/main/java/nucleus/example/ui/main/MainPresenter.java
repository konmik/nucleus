package nucleus.example.ui.main;

import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import icepick.State;
import io.reactivex.subjects.PublishSubject;
import nucleus.example.network.ServerAPI;
import nucleus.example.ui.base.BasePresenter;
import nucleus.example.util.PageBundle;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

public class MainPresenter extends BasePresenter<MainFragment> {

    public static final String NAME_1 = "Chuck Norris";
    public static final String NAME_2 = "Jackie Chan";
    public static final String DEFAULT_NAME = NAME_1;

    private static final int REQUEST_ITEMS = 1;

    @Inject ServerAPI api;
    @Inject SharedPreferences pref;

    @State String name;

    private PublishSubject<Integer> pageRequests = PublishSubject.create();

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableReplay(REQUEST_ITEMS,
            () -> pageRequests.startWith(0)
                .concatMap(page ->
                    api.getItems(name.split("\\s+")[0], name.split("\\s+")[1], page)
                        .map(data -> new PageBundle<>(page, data))
                        .delay(pref.getInt("delay", 0), TimeUnit.SECONDS)
                        .subscribeOn(io())
                        .observeOn(mainThread())),
            (activity, page) -> activity.onItems(page, name),
            MainFragment::onNetworkError);
    }

    void request(String name) {
        this.name = name;
        start(REQUEST_ITEMS);
    }

    void requestNext(int page) {
        pageRequests.onNext(page);
    }
}
