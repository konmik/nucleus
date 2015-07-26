package nucleus.example.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import nucleus.example.TestActivity;
import nucleus.factory.PresenterFactory;
import nucleus.presenter.Presenter;
import nucleus.view.ViewWithPresenter;

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

    public static class TestPresenter extends Presenter {

        public int onDestroy;

        @Override
        protected void onDestroy() {
            super.onDestroy();
            onDestroy++;
        }
    }

    public static class TestFragment1 extends Fragment implements ViewWithPresenter<TestPresenter> {

        public TestPresenter presenter = new TestPresenter();

        @Override
        public PresenterFactory<TestPresenter> getPresenterFactory() {
            return null;
        }

        @Override
        public void setPresenterFactory(PresenterFactory<TestPresenter> presenterFactory) {

        }

        @Override
        public TestPresenter getPresenter() {
            return presenter;
        }
    }

    public static class TestFragment2 extends Fragment {

    }

    @UiThreadTest
    public void testPushPop() throws Exception {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentStack stack = new FragmentStack(manager, CONTAINER_ID);

        TestFragment1 fragment = new TestFragment1();
        stack.push(fragment);
        assertEquals(fragment, manager.findFragmentById(CONTAINER_ID));
        assertEquals(fragment, manager.findFragmentByTag("0"));
        assertEquals(fragment, stack.peek());
        assertEquals(0, manager.getBackStackEntryCount());

        TestFragment2 fragment2 = new TestFragment2();
        stack.push(fragment2);
        assertEquals(fragment2, manager.findFragmentById(CONTAINER_ID));
        assertEquals(fragment2, manager.findFragmentByTag("1"));
        assertEquals(fragment2, stack.peek());
        assertEquals(1, manager.getBackStackEntryCount());

        assertEquals(fragment, manager.findFragmentByTag("0"));

        assertFalse(fragment.isAdded());
        assertTrue(fragment2.isAdded());

        assertTrue(stack.pop());
        assertEquals(fragment, manager.findFragmentById(CONTAINER_ID));
        assertEquals(0, manager.getBackStackEntryCount());

        assertNull(manager.findFragmentByTag("1"));

        assertFalse(stack.pop());
        assertEquals(fragment, manager.findFragmentById(CONTAINER_ID));
        assertEquals(0, manager.getBackStackEntryCount());
    }

    @UiThreadTest
    public void testReplace() throws Exception {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentStack stack = new FragmentStack(manager, CONTAINER_ID);

        TestFragment1 fragment = new TestFragment1();
        stack.replace(fragment);
        assertEquals(fragment, manager.findFragmentById(CONTAINER_ID));
        assertEquals(fragment, stack.peek());
        assertEquals(0, manager.getBackStackEntryCount());

        TestFragment2 fragment2 = new TestFragment2();
        stack.replace(fragment2);
        assertEquals(fragment2, manager.findFragmentById(CONTAINER_ID));
        assertEquals(fragment2, stack.peek());
        assertEquals(0, manager.getBackStackEntryCount());
    }

    @UiThreadTest
    public void testPushReplace() throws Exception {

        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentStack stack = new FragmentStack(manager, CONTAINER_ID);

        TestFragment1 fragment = new TestFragment1();
        stack.push(fragment);
        TestFragment2 fragment2 = new TestFragment2();
        stack.push(fragment2);
        assertEquals(0, fragment.presenter.onDestroy);

        TestFragment1 fragment3 = new TestFragment1();
        stack.replace(fragment3);
        assertEquals(0, manager.getBackStackEntryCount());
        assertEquals(fragment3, manager.findFragmentById(CONTAINER_ID));
        assertEquals(fragment3, manager.findFragmentByTag("0"));

        assertEquals(1, fragment.presenter.onDestroy);
        assertNull(manager.findFragmentByTag("1"));
    }
}
