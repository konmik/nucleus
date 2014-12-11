package nucleus.presenter.broker;

import java.util.HashMap;

import nucleus.model.Loader;

@SuppressWarnings("unchecked") // too many of them but all are safe
public abstract class LoaderBroker<TargetType> extends Broker<TargetType> implements Loader.Receiver {

    private HashMap<Loader, Object> loaders = new HashMap<Loader, Object>();

    public LoaderBroker(Loader... loaders) {
        for (Loader loader : loaders)
            this.loaders.put(loader, null);

        for (Loader loader : loaders)
            loader.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Loader loader : loaders.keySet())
            loader.unregister(this);
    }

    @Override
    public void onTakeTarget(TargetType target) {
        super.onTakeTarget(target);
        present();
    }

    public void present() {
        TargetType target = getTarget();
        if (target != null)
            onPresent(target);
    }

    protected abstract void onPresent(TargetType target);

    @Override
    public void onLoadComplete(Loader loader, Object data) {
        loaders.put(loader, data);

        if (isLoadingComplete())
            present();
    }

    public boolean isLoadingComplete() {
        return !loaders.values().contains(null);
    }

    public <T> T getData(Loader<T> loader) {
        return (T)loaders.get(loader);
    }
}
