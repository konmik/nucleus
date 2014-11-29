package nucleus.example.main;

import android.os.Bundle;
import nucleus.example.base.Injector;
import nucleus.example.network.ItemsLoader;
import nucleus.presenter.Presenter;
import nucleus.presenter.broker.LoaderBroker;
import nucleus.presenter.broker.Broker;

import javax.inject.Inject;

public class MainPresenter extends Presenter<MainActivity> {

    private static final String NAME_KEY = "name";
    private static final String COUNTER_KEY = "counter";

    private int counter;

    @Inject ItemsLoader itemsLoader;

    private String name = MainActivity.DEFAULT_NAME;

    @Override
    protected void onCreate(Bundle savedState) {
        if (savedState != null) {
            name = savedState.getString(NAME_KEY);
            counter = savedState.getInt(COUNTER_KEY);
        }

        Injector.inject(this);

        addPresenterBroker(new LogBroker());

        addViewBroker(new LogBroker());

        addViewBroker(new Broker<MainActivity>() {
            @Override
            public void onTakeTarget(MainActivity view) {
                view.publishCounter(++counter);
            }
        });

        addViewBroker(new LoaderBroker<MainActivity>(itemsLoader) {
            @Override
            protected void onPresent(MainActivity target, boolean complete) {
                target.publishItems(!complete ? null : getData(itemsLoader).items, name);
            }
        });

        itemsLoader.request(name);
    }

    @Override
    public Bundle onSave() {
        Bundle bundle = new Bundle();
        bundle.putString(NAME_KEY, name);
        bundle.putInt(COUNTER_KEY, counter);
        return bundle;
    }

    public void toggleTo(String name) {
        this.name = name;
        itemsLoader.request(name);
    }
}