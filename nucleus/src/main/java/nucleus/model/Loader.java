package nucleus.model;

import java.util.ArrayList;

/**
 * Formalizes any background data loading, implementing the Adapter pattern.
 *
 * @param <ResultType> is a type of data {@link nucleus.model.Loader} loads.
 */
public abstract class Loader<ResultType> {

    /**
     * The interface for receiving data updates.
     *
     * @param <ResultType> a data type that {@link nucleus.model.Loader} returns.
     */
    public interface Receiver<ResultType> {
        void onLoadComplete(Loader<ResultType> loader, ResultType data);
    }

    private ArrayList<Receiver<ResultType>> receivers = new ArrayList<Receiver<ResultType>>();

    private boolean notifying;
    private ArrayList<Receiver<ResultType>> unregistered = new ArrayList<Receiver<ResultType>>();

    /**
     * Registers a receiver for data updates.
     *
     * @param receiver the Receiver of loading updates.
     */
    public void register(Receiver<ResultType> receiver) {
        receivers.add(receiver);
    }

    /**
     * Unregister {@link nucleus.model.Loader.Receiver} which has been
     * registered with {@link nucleus.model.Loader#register} method.
     *
     * @param receiver the Receiver registered with {@link nucleus.model.Loader#register} method
     */
    public void unregister(Receiver<ResultType> receiver) {
        receivers.remove(receiver);

        if (notifying)
            unregistered.add(receiver);
    }

    /**
     * Notify all registered receivers with a data update. This method is change-proof which means that it will handle
     * {@link nucleus.model.Loader#unregister} during {@link nucleus.model.Loader.Receiver#onLoadComplete} properly.
     *
     * @param data Data object emitted by the {@link nucleus.model.Loader}.
     */
    protected void notifyReceivers(ResultType data) {
        notifying = true;

        for (Receiver receiver : receivers.toArray(new Receiver[receivers.size()])) {
            if (!unregistered.contains(receiver))
                //noinspection unchecked
                receiver.onLoadComplete(this, data);
        }

        unregistered.clear();
    }
}
