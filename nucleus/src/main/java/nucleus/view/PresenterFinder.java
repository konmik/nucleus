package nucleus.view;

import android.app.Activity;
import android.app.Application;
import android.view.View;
import android.view.ViewParent;
import nucleus.presenter.Presenter;

public class PresenterFinder {

    private static PresenterFinder instance = new PresenterFinder();

    public static PresenterFinder getInstance() {
        return instance;
    }

    public static void setInstance(PresenterFinder instance) {
        PresenterFinder.instance = instance;
    }

    public Presenter findParentPresenter(Object view) {

        if (view instanceof View) {

            View v = (View)view;
            ViewParent parent = v.getParent();
            while (parent != null && parent instanceof View) {
                if (parent instanceof PresenterProvider)
                    return ((PresenterProvider)parent).getPresenter();

                parent = parent.getParent();
            }

            Activity activity = (Activity)v.getContext();
            if (activity instanceof PresenterProvider)
                return ((PresenterProvider)activity).getPresenter();

            view = activity;
        }

        if (view instanceof Activity) {

            Activity activity = (Activity)view;
            Application application = activity.getApplication();
            if (application instanceof PresenterProvider)
                return ((PresenterProvider)application).getPresenter();
        }

        return Presenter.getRootPresenter();
    }
};
