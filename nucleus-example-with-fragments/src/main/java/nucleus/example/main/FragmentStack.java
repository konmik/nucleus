package nucleus.example.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import nucleus.view.NucleusFragment;

/**
 * This is an example fragment stack handler.
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
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        manager.executePendingTransactions();
    }

    /**
     * Pops the topmost fragment from the stack.
     */
    public boolean pop() {
        if (manager.getBackStackEntryCount() == 0)
            return false;
        Fragment top = peek();
        manager.popBackStackImmediate();
        destroyPresenter(top);
        return true;
    }

    /**
     * Replaces entire stack contents with just one fragment.
     */
    public void replace(Fragment fragment) {
        while (pop()) ;
        Fragment top = peek();
        manager.beginTransaction()
            .replace(containerId, fragment)
            .commit();
        manager.executePendingTransactions();
        if (top != null)
            destroyPresenter(top);
    }

    /**
     * Returns the topmost fragment in the stack.
     */
    public Fragment peek() {
        return manager.findFragmentById(containerId);
    }

    private void destroyPresenter(Fragment top) {
        if (top instanceof NucleusFragment)
            ((NucleusFragment)top).destroyPresenter();
    }
}
