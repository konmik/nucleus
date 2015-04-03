package nucleus.manager;

import android.os.Bundle;
import android.util.Printer;

import java.util.HashMap;
import java.util.Map;

import nucleus.presenter.Presenter;

/**
 * This is the default implementation of {@link nucleus.manager.PresenterManager}.
 */
public class DefaultPresenterManager extends PresenterManager {
    private static final String PRESENTER_ID_KEY = "id";
    private static final String PRESENTER_STATE_KEY = "state";

    private HashMap<String, Presenter> idToPresenter = new HashMap<>();
    private HashMap<Presenter, String> presenterToId = new HashMap<>();
    private int nextId;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Presenter> T provide(Class<T> presenterClass, Bundle savedState) {
        String id = providePresenterId(presenterClass, savedState);
        if (idToPresenter.containsKey(id))
            return (T)idToPresenter.get(id);

        Presenter presenter = instantiatePresenter(presenterClass, id);
        presenter.create(savedState == null ? null : savedState.getBundle(PRESENTER_STATE_KEY));
        return (T)presenter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle save(Presenter presenter) {
        Bundle bundle = new Bundle();
        bundle.putString(PRESENTER_ID_KEY, presenterToId.get(presenter));
        Bundle presenterState = new Bundle();
        presenter.save(presenterState);
        bundle.putBundle(PRESENTER_STATE_KEY, presenterState);
        return bundle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy(Presenter presenter) {
        presenter.destroy();
        idToPresenter.remove(presenterToId.remove(presenter));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void print(Printer printer) {
        for (Map.Entry<String, Presenter> entry : idToPresenter.entrySet()) {
            Object view = entry.getValue().getView();
            printer.println("id: " + entry.getKey() + (view == null ? "" : " => view: " + view.toString()));
        }
    }

    private String providePresenterId(Class<? extends Presenter> presenterClass, Bundle savedState) {
        return savedState != null ? savedState.getString(PRESENTER_ID_KEY) :
            presenterClass.getSimpleName() + " (" + nextId++ + "/" + System.nanoTime() + "/" + (int)(Math.random() * Integer.MAX_VALUE) + ")";
    }

    private Presenter instantiatePresenter(Class<? extends Presenter> presenterClass, String id) {
        Presenter presenter;
        try {
            presenter = presenterClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        idToPresenter.put(id, presenter);
        presenterToId.put(presenter, id);
        return presenter;
    }
}
