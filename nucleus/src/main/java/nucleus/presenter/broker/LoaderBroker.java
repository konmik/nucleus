package nucleus.presenter.broker;

import nucleus.model.Loader;

import java.util.HashMap;

@SuppressWarnings("unchecked") // too many of them but all are safe
public abstract class LoaderBroker<TargetType> extends Broker<TargetType> implements Loader.Receiver {

    private HashMap<Loader, Object> loaders = new HashMap<Loader, Object>();
    private TargetType target;

    public LoaderBroker(Loader... loaders) {
        for (Loader loader : loaders)
            this.loaders.put(loader, null);

        for (Loader loader : loaders)
            loader.register(this);
    }

    @Override
    public void onDestroy() {
        for (Loader loader : loaders.keySet())
            loader.unregister(this);
    }

    @Override
    public void onTakeTarget(TargetType target) {
        this.target = target;
        onPresent(target, isLoadingComplete());
    }

    @Override
    public void onDropTarget(TargetType target) {
        this.target = null;
    }

    protected abstract void onPresent(TargetType target, boolean complete);

    @Override
    public void onLoadComplete(Loader loader, Object data) {
        loaders.put(loader, data);

        if (isLoadingComplete() && target != null)
            onPresent(target, true);
    }

    private boolean isLoadingComplete() {
        return !loaders.values().contains(null);
    }

    public <T> T getData(Loader<T> loader) {
        return (T)loaders.get(loader);
    }
}
