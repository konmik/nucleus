package nucleus.presenter;

import android.os.Bundle;
import android.util.Printer;

import java.util.HashMap;
import java.util.Map;

/**
 * A singleton that manages presenter's creation and state persistence.
 */
public class PresenterManager {

    private static final String PRESENTER_ID_KEY = "id";
    private static final String PRESENTER_STATE_KEY = "state";

    private HashMap<String, Presenter> presenters = new HashMap<>();
    private HashMap<Presenter, String> ids = new HashMap<>();
    private HashMap<Class<? extends Presenter>, Presenter> overrides = new HashMap<>();

    private PresenterManager() {
    }

    private static PresenterManager instance = new PresenterManager();

    /**
     * Returns a singleton instance of {@link nucleus.presenter.PresenterManager}
     *
     * @return a singleton instance of {@link nucleus.presenter.PresenterManager}
     */
    public static PresenterManager getInstance() {
        return instance;
    }

    /**
     * Overrides a presenter for the sake of testing.
     *
     * @param presenterClass a presenter class to substitute
     * @param presenter      presenter to provide
     */
    public <T extends Presenter> void overridePresenter(Class<? extends T> presenterClass, T presenter) {
        overrides.put(presenterClass, presenter);
    }

    /**
     * Finds a Presenter for a given view or restores it from the saved state.
     * There can be three cases when this method is being called:
     * 1. First creation of a view;
     * 2. Restoring of a view when the process has NOT been destroyed (configuration change, activity recreation because of memory limitation);
     * 3. Restoring of a view when the process has been destroyed.
     * <p/>
     * This method searches a passed view for {@link RequiresPresenter} annotation to instantiate a presenter and to attach it to view.
     *
     * @return Successively: an overridden, found, restored or created presenter.
     * A RuntimeException will be thrown if no {@link RequiresPresenter} annotation can not be found.
     */
    @SuppressWarnings("unchecked")
    public <T extends Presenter> T provide(Object view, Bundle savedState) {
        Class viewClass = view.getClass();
        Class<? extends Presenter> presenterClass = findPresenterClass(viewClass);

        // overridden

        if (overrides.containsKey(presenterClass))
            return (T)overrides.get(presenterClass);

        String id = providePresenterId(presenterClass, viewClass, savedState);

        // found

        if (presenters.containsKey(id))
            return (T)presenters.get(id);

        // restored or created

        Presenter presenter = instantiatePresenter(presenterClass, id);
        presenter.onCreate(savedState == null ? null : savedState.getBundle(PRESENTER_STATE_KEY));
        return (T)presenter;
    }

    private String providePresenterId(Class<? extends Presenter> presenterClass, Class viewClass, Bundle savedState) {
        return savedState != null ? savedState.getString(PRESENTER_ID_KEY) :
            presenterClass.getSimpleName() + " -> " + viewClass.getSimpleName() +
                " (" + System.nanoTime() + "/" + (int)(Math.random() * Integer.MAX_VALUE) + ")";
    }

    private Presenter instantiatePresenter(Class<? extends Presenter> presenterClass, String id) {
        Presenter presenter;
        try {
            presenter = presenterClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        presenters.put(id, presenter);
        ids.put(presenter, id);
        return presenter;
    }

    private Class<? extends Presenter> findPresenterClass(Class viewClass) {
        if (!viewClass.isAnnotationPresent(RequiresPresenter.class))
            throw new RuntimeException(RequiresPresenter.class.getName() + " annotation must present on " + viewClass.getName());

        RequiresPresenter annotation = (RequiresPresenter)viewClass.getAnnotation(RequiresPresenter.class);
        return annotation.value();
    }

    /**
     * Creates a bundle that can be used to re-instantiate a presenter. Pass this bundle to {@link #provide}.
     *
     * @param presenter a presenter to obtain restoration bundle for.
     * @return a Bundle that can be used to re-instantiate a presenter.
     */
    public Bundle save(Presenter presenter) {
        Bundle bundle = new Bundle();
        bundle.putString(PRESENTER_ID_KEY, ids.get(presenter));
        Bundle presenterState = new Bundle();
        presenter.onSave(presenterState);
        bundle.putBundle(PRESENTER_STATE_KEY, presenterState);
        return bundle;
    }

    /**
     * Destroys a presenter, removing all references to it.
     *
     * @param presenter a presenter to destroy.
     */
    public void destroy(Presenter presenter) {
        presenter.destroy();
        presenters.remove(ids.remove(presenter));
    }

    /**
     * Prints a list of presenters and attached views.
     *
     * @param printer a target for printing.
     */
    public void print(Printer printer) {
        for (Map.Entry<String, Presenter> entry : presenters.entrySet()) {
            Object view = entry.getValue().getView();
            printer.println("id: " + entry.getKey() + (view == null ? "" : " => view: " + view.toString()));
        }
    }

    /**
     * Testing utility
     */
    static void clear() {
        instance = new PresenterManager();
    }
}
