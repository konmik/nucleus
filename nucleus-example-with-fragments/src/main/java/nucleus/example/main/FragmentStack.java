package nucleus.example.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

import nucleus.presenter.Presenter;
import nucleus.view.ViewWithPresenter;

/**
 * Why this class is needed.
 *
 * FragmentManager does not supply a developer with a fragment stack.
 * It gives us a fragment *transaction* stack.
 *
 * To be sane, we need *fragment* stack.
 *
 * This implementation also handles NucleusSupportFragment presenter`s lifecycle correctly.
 */
public class FragmentStack {

    private FragmentManager manager;
    private int containerId;

    public FragmentStack(FragmentManager manager, int containerId) {
        this.manager = manager;
        this.containerId = containerId;
    }

    /**
     * Pushes a fragment to the top of the stack.
     */
    public void push(Fragment fragment) {

        Fragment top = peek();
        if (top != null) {
            manager.beginTransaction()
                .remove(top)
                .add(containerId, fragment, indexToTag(manager.getBackStackEntryCount() + 1))
                .addToBackStack(null)
                .commit();
        }
        else {
            manager.beginTransaction()
                .add(containerId, fragment, indexToTag(0))
                .commit();
        }

        manager.executePendingTransactions();
    }

    /**
     * Pops the topmost fragment from the stack.
     * The lowest fragment can't be popped, it can only be replaced.
     *
     * @return false if the stack can't pop or true if a top fragment has been popped.
     */
    public boolean pop() {
        if (manager.getBackStackEntryCount() == 0) {
            return false;
        }
        Fragment top = peek();
        manager.popBackStackImmediate();
        destroyPresenter(top);
        return true;
    }

    /**
     * Replaces entire stack contents with just one fragment.
     */
    public void replace(Fragment fragment) {
        clear();
        manager.beginTransaction()
            .replace(containerId, fragment, indexToTag(0))
            .commit();
        manager.executePendingTransactions();
    }

    /**
     * Returns the topmost fragment in the stack.
     */
    public Fragment peek() {
        return manager.findFragmentById(containerId);
    }

    private void clear() {
        ArrayList<Presenter> presenters = new ArrayList<>();
        for (int i = 0; i < manager.getBackStackEntryCount(); i++) {
            Fragment fragment = manager.findFragmentByTag(indexToTag(i));
            if (fragment != null && fragment instanceof ViewWithPresenter) {
                Presenter presenter = ((ViewWithPresenter)fragment).getPresenter();
                if (presenter != null) {
                    presenters.add(presenter);
                }
            }
        }
        manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        for (Presenter presenter : presenters) {
            presenter.destroy();
        }
    }

    private void destroyPresenter(Fragment top) {
        if (top instanceof ViewWithPresenter) {
            ((ViewWithPresenter)top).getPresenter().destroy();
        }
    }

    private String indexToTag(int index) {
        return Integer.toString(index);
    }
}