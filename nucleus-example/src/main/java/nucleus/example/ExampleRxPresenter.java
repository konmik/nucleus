package nucleus.example;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import nucleus.presenter.RxPresenter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by rharter on 4/17/15.
 */
public class ExampleRxPresenter extends RxPresenter<ExampleRxActivity> {

    private static final String TAG = ExampleRxPresenter.class.getSimpleName();

    private static final int REQUEST_ITEMS = 1;

    private static final String STATE_COUNTER = "state_counter";

    int onTakeViewCounter;

    @Override protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (savedState != null) {
            onTakeViewCounter = savedState.getInt(STATE_COUNTER);
        }

        registerRestartable(REQUEST_ITEMS, new Func0<Subscription>() {
            @Override public Subscription call() {
                return ExampleDataProvider.getPlanets()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(ExampleRxPresenter.this.<List<String>>deliverLatestCache())
                        .subscribe(new Action1<List<String>>() {
                            @Override public void call(List<String> planets) {
                                onReceivedPlanets(planets);
                            }
                        }, new Action1<Throwable>() {
                            @Override public void call(Throwable throwable) {
                                onError(throwable);
                            }
                        });
            }
        });
    }

    @Override protected void onTakeView(ExampleRxActivity view) {
        super.onTakeView(view);
        view.updateCounter(++onTakeViewCounter);
        subscribeRestartable(REQUEST_ITEMS);
    }

    @Override public void save(Bundle state) {
        super.save(state);
        state.putInt(STATE_COUNTER, onTakeViewCounter);
    }

    /**
     * Called when our subscriber gets a new set of planets.
     */
    private void onReceivedPlanets(List<String> planets) {
        getView().showPlanets(planets);
    }

    /**
     * Called when our subscriber receives an error.
     */
    private void onError(Throwable throwable) {
        Log.e(TAG, "Failed to load planets.", throwable);
        getView().showError("Failed to load planets.");
    }

    public void onPlanetClicked(String planet) {
        Intent intent = new Intent(getView(), ExampleDetailActivity.class);
        intent.putExtra(ExampleDetailActivity.EXTRA_PLANET, planet);
        getView().startActivity(intent);
    }
}
