package nucleus.presenter;

import android.os.Bundle;
import android.util.Printer;
import nucleus.presenter.broker.Broker;

import java.util.ArrayList;

public class Presenter<ViewType> {

    private static final String PRESENTER_ID_KEY = "id";
    private static final String PRESENTER_STATE_KEY = "state";

    private Presenter parent;
    private String id;

    private ViewType view;
    private ArrayList<Presenter> presenters = new ArrayList<Presenter>();

    private ArrayList<Broker<ViewType>> viewBrokers = new ArrayList<Broker<ViewType>>();
    private ArrayList<Broker<Presenter>> presenterBrokers = new ArrayList<Broker<Presenter>>();

    public Presenter getParent() {
        return parent;
    }

    public String getId() {
        return id;
    }

    public ViewType getView() {
        return view;
    }

    public Presenter getPresenter(String id) {

        for (Presenter presenter : presenters) {
            if (presenter.id.equals(id))
                return presenter;
        }

        return null;
    }

    /**
     * Finds a Presenter or restores it from the saved state.
     *
     * @param creator    a callback for creating an instance of the Presenter class
     * @param savedState saved state of the required {@link nucleus.presenter.Presenter}, that been created with
     *                   {@link nucleus.presenter.Presenter#save} or null
     * @param <T>        Type of the required presenter
     * @return found or created with {@link nucleus.presenter.PresenterCreator} presenter
     */
    public <T extends Presenter> T provide(PresenterCreator<T> creator, Bundle savedState) {

        String id = null;

        if (savedState != null) {
            id = savedState.getString(PRESENTER_ID_KEY);

            for (Presenter presenter : presenters) {
                if (presenter.id.equals(id))
                    //noinspection unchecked
                    return (T)presenter; // it should always be of the same type if the caller will not cheat us
            }
        }

        T presenter = creator.createPresenter();
        ((Presenter)presenter).parent = this;
        ((Presenter)presenter).id = id != null ? id :
            presenter.getClass().getSimpleName() + " -> " + creator.getClass().getSimpleName() +
                " (" + presenters.size() + "/" + System.nanoTime() + "/" + (int)(Math.random() * Integer.MAX_VALUE) + ")";

        presenter.onCreate(savedState == null ? null : savedState.getBundle(PRESENTER_STATE_KEY));

        takePresenter(presenter);

        return presenter;
    }

    public void destroy() {
        parent.dropPresenter(this);

        onDestroy();

        for (Broker broker : viewBrokers)
            broker.onDestroy();

        viewBrokers.clear();
    }

    public Bundle save() {
        Bundle bundle = new Bundle();
        bundle.putString(PRESENTER_ID_KEY, id);
        bundle.putBundle(PRESENTER_STATE_KEY, onSave());
        return bundle;
    }

    public void takeView(ViewType view) {
        this.view = view;

        onTakeView(view);

        for (Broker<ViewType> broker : viewBrokers)
            broker.onTakeTarget(view);
    }

    public void dropView(ViewType view) {

        onDropView(view);

        for (Broker<ViewType> broker : viewBrokers)
            broker.onDropTarget(view);

        this.view = null;
    }

    public void takePresenter(Presenter presenter) {
        presenters.add(presenter);

        onTakePresenter(presenter);

        for (Broker<Presenter> broker : presenterBrokers)
            broker.onTakeTarget(presenter);
    }

    public void dropPresenter(Presenter presenter) {

        onDropPresenter(presenter);

        for (Broker<Presenter> broker : presenterBrokers)
            broker.onDropTarget(presenter);

        presenters.remove(presenter);
    }

    protected void onCreate(Bundle savedState) {
    }

    protected void onDestroy() {
    }

    protected Bundle onSave() {
        return null;
    }

    protected void onTakeView(ViewType view) {
    }

    protected void onDropView(ViewType view) {
    }

    protected void onTakePresenter(Presenter presenter) {
    }

    protected void onDropPresenter(Presenter presenter) {
    }

    /**
     * This method attaches a {@link Broker} to a {@link Presenter}.
     * Call this during {@link Presenter#onCreate}.
     *
     * @param broker {@link Broker} to create
     * @param <T>    {@link Broker} type
     * @return The same {@link Broker} that has been passed as an argument
     */
    protected <T extends Broker<ViewType>> T addViewBroker(T broker) {
        viewBrokers.add(broker);
        return broker;
    }

    protected <T extends Broker<Presenter>> T addPresenterBroker(T broker) {
        presenterBrokers.add(broker);
        return broker;
    }

    // debug

    private void print(Printer printer, int level) {
        String padding = "";
        for (int p = 0; p < level; p++)
            padding += ".   ";

        ViewType view = getView();
        printer.println(padding + id + (view == null ? "" : " => view: " + view.toString()));

        for (Broker broker : viewBrokers)
            printer.println(padding + " {broker} -> " + broker.getClass().getName());

        for (Broker broker : presenterBrokers)
            printer.println(padding + " {broker} -> " + broker.getClass().getName());

        for (Presenter m : presenters)
            m.print(printer, level + 1);
    }

    public void print(Printer printer) {
        print(printer, 0);
    }
}
