package nucleus.example.main;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import java.util.ArrayList;
import java.util.List;

import nucleus.example.TestActivity;

public class FragmentStackTest extends ActivityInstrumentationTestCase2<TestActivity> {

    private static final int CONTAINER_ID = android.R.id.content;

    private TestActivity activity;

    public FragmentStackTest() {
        super(TestActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        activity = getActivity();
    }

    public static class TestFragment1 extends Fragment {
    }

    public static class TestFragment2 extends Fragment {
    }

    public interface TestCallback {
    }

    public static class TestFragmentC extends Fragment implements TestCallback {
    }

    @UiThreadTest
    public void testPushPop() throws Exception {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentStack stack = new FragmentStack(activity, manager, CONTAINER_ID, null);

        TestFragment1 fragment = new TestFragment1();
        stack.push(fragment);
        assertTopFragment(manager, stack, fragment, 0);

        TestFragment2 fragment2 = new TestFragment2();
        stack.push(fragment2);
        assertFragment(manager, fragment, 0);
        assertTopFragment(manager, stack, fragment2, 1);

        assertFalse(fragment.isAdded());
        assertTrue(fragment2.isAdded());

        assertTrue(stack.pop());
        assertTopFragment(manager, stack, fragment, 0);

        assertNull(manager.findFragmentByTag("1"));

        assertFalse(stack.pop());
        assertTopFragment(manager, stack, fragment, 0);
    }

    @UiThreadTest
    public void testReplace() throws Exception {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentStack stack = new FragmentStack(activity, manager, CONTAINER_ID, null);

        TestFragment1 fragment = new TestFragment1();
        stack.replace(fragment);
        assertTopFragment(manager, stack, fragment, 0);

        TestFragment2 fragment2 = new TestFragment2();
        stack.replace(fragment2);
        assertTopFragment(manager, stack, fragment2, 0);
    }

    @UiThreadTest
    public void testPushReplace() throws Exception {

        final List<Fragment> removed = new ArrayList<>();

        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentStack stack = new FragmentStack(activity, manager, CONTAINER_ID, new FragmentStack.OnFragmentRemovedListener() {
            @Override
            public void onFragmentRemoved(Fragment fragment) {
                removed.add(fragment);
            }
        });

        TestFragment1 fragment0 = new TestFragment1();
        stack.push(fragment0);
        TestFragment2 fragment1 = new TestFragment2();
        stack.push(fragment1);
        TestFragment2 fragment2 = new TestFragment2();
        stack.push(fragment2);

        assertEquals(0, removed.size());

        stack.pop();
        assertEquals(1, removed.size());
        assertTrue(removed.contains(fragment2));

        TestFragment1 fragmentR = new TestFragment1();
        stack.replace(fragmentR);
        assertTopFragment(manager, stack, fragmentR, 0);

        assertEquals(3, removed.size());
        assertTrue(removed.contains(fragment0));
        assertTrue(removed.contains(fragment1));

        assertNull(manager.findFragmentByTag("1"));
    }

    @UiThreadTest
    public void testFindCallback() throws Exception {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentStack stack = new FragmentStack(activity, manager, CONTAINER_ID, null);

        TestFragmentC fragment = new TestFragmentC();
        stack.push(fragment);
        TestFragment2 fragment2 = new TestFragment2();
        stack.push(fragment2);

        assertEquals(fragment, stack.findCallback(fragment2, TestCallback.class));
        assertEquals(activity, stack.findCallback(fragment2, Activity.class));
        assertNull(stack.findCallback(fragment2, String.class));
    }

    @UiThreadTest
    public void testBack() throws Exception {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentStack stack = new FragmentStack(activity, manager, CONTAINER_ID, null);

        assertFalse(stack.back());

        stack.push(new TestFragment1());
        assertEquals(1, stack.size());
        assertFalse(stack.back());

        stack.push(new TestFragment1());
        assertEquals(2, stack.size());
        assertTrue(stack.back());

        assertEquals(1, stack.size());
    }

    private void assertTopFragment(FragmentManager manager, FragmentStack stack, Fragment fragment, int index) {
        assertFragment(manager, fragment, index);
        assertEquals(fragment, manager.findFragmentById(CONTAINER_ID));
        assertEquals(fragment, stack.peek());
        assertEquals(index, manager.getBackStackEntryCount());
    }

    private void assertFragment(FragmentManager manager, Fragment fragment, int index) {
        assertEquals(fragment, manager.findFragmentByTag(Integer.toString(index)));
    }
}
