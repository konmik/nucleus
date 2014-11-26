package nucleus.view;

import android.app.Activity;
import android.app.Application;
import android.view.View;
import android.view.ViewParent;

import nucleus.presenter.Presenter;

public class PresenterFinder {

    private static Presenter root = new Presenter();

    public static void setRootPresenter(Presenter root) {
        PresenterFinder.root = root;
    }

    public static Presenter findParentPresenter(NucleusLayout view) {

        ViewParent parent = view.getParent();
        while (parent != null && parent instanceof View) {
            if (parent instanceof PresenterProvider)
                return ((PresenterProvider)parent).getPresenter();

            parent = parent.getParent();
        }

        Activity activity = (Activity)view.getContext();
        if (activity instanceof PresenterProvider)
            return ((PresenterProvider)activity).getPresenter();

        return findParentPresenter(activity);
    }

    public static Presenter findParentPresenter(Activity activity) {

        Application application = activity.getApplication();
        if (application instanceof PresenterProvider)
            return ((PresenterProvider)application).getPresenter();

        return root;
    }
}
