package nucleus.example.main;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import nucleus.example.R;

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

    public interface OnBackPressedHandlingFragment {
        boolean onBackPressed();
    }

    public interface OnFragmentRemovedListener {
        void onFragmentRemoved(Fragment fragment);
    }

    private Activity activity;
    private FragmentManager manager;
    private int containerId;
    @Nullable private OnFragmentRemovedListener onFragmentRemovedListener;

    private ArrayList<Fragment> fragments = new ArrayList<>();

    public FragmentStack(Activity activity, FragmentManager manager, int containerId, @Nullable OnFragmentRemovedListener onFragmentRemovedListener) {
        this.activity = activity;
        this.manager = manager;
        this.containerId = containerId;
        this.onFragmentRemovedListener = onFragmentRemovedListener;
    }

    /**
     * Returns the number of fragments in the stack.
     *
     * @return the number of fragments in the stack.
     */
    public int size() {
        return fragments.size();
    }

    /**
     * Pushes a fragment to the top of the stack.
     */
    public void push(Fragment fragment) {

        Fragment top = peek();
        if (top != null) {
            manager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .remove(top)
                .add(containerId, fragment)
                .addToBackStack(null)
                .commit();
        }
        else {
            manager.beginTransaction()
                .add(containerId, fragment)
                .commit();
        }

        manager.executePendingTransactions();
        fragments.add(fragment);
    }

    /**
     * Pops the top item if the stack.
     * If the fragment implements {@link OnBackPressedHandlingFragment}, calls {@link OnBackPressedHandlingFragment#onBackPressed()} instead.
     * If {@link OnBackPressedHandlingFragment#onBackPressed()} returns false the fragment gets popped.
     *
     * @return true if a fragment has been popped or if {@link OnBackPressedHandlingFragment#onBackPressed()} returned true;
     */
    public boolean back() {
        Fragment top = peek();
        if (top instanceof OnBackPressedHandlingFragment) {
            if (((OnBackPressedHandlingFragment)top).onBackPressed())
                return true;
        }
        return pop();
    }

    /**
     * Pops the topmost fragment from the stack.
     * The lowest fragment can't be popped, it can only be replaced.
     *
     * @return false if the stack can't pop or true if a top fragment has been popped.
     */
    public boolean pop() {
        if (manager.getBackStackEntryCount() == 0)
            return false;
        Fragment top = peek();
        manager.popBackStackImmediate();
        fragments.remove(top);
        if (onFragmentRemovedListener != null)
            onFragmentRemovedListener.onFragmentRemoved(top);
        return true;
    }

    /**
     * Replaces stack contents with just one fragment.
     */
    public void replace(Fragment fragment) {
        List<Fragment> before = new ArrayList<>(fragments);

        manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        manager.beginTransaction()
            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
            .replace(containerId, fragment)
            .commit();
        manager.executePendingTransactions();
        fragments.clear();
        fragments.add(fragment);

        if (onFragmentRemovedListener != null) {
            for (Fragment fragment1 : before)
                onFragmentRemovedListener.onFragmentRemoved(fragment1);
        }
    }

    /**
     * Returns the topmost fragment in the stack or null.
     */
    public Fragment peek() {
        return fragments.size() == 0 ? null : fragments.get(fragments.size() - 1);
    }

    /**
     * Returns a back fragment if the fragment is of given class.
     * If such fragment does not exist and activity implements the given class then the activity will be returned.
     *
     * @param fragment     a fragment to search from.
     * @param callbackType a class of type for callback to search.
     * @param <T>          a type of callback.
     * @return a back fragment or activity.
     */
    @SuppressWarnings("unchecked")
    public <T> T findCallback(Fragment fragment, Class<T> callbackType) {

        Fragment back = getBackFragment(fragment);

        if (back != null && callbackType.isAssignableFrom(back.getClass()))
            return (T)back;

        if (callbackType.isAssignableFrom(activity.getClass()))
            return (T)activity;

        return null;
    }

    private Fragment getBackFragment(Fragment fragment) {
        for (int f = fragments.size() - 1; f >= 0; f--) {
            if (fragments.get(f) == fragment && f > 0)
                return fragments.get(f - 1);
        }
        return null;
    }
}
