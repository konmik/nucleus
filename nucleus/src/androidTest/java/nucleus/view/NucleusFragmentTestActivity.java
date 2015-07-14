package nucleus.view;

import android.app.Activity;
import android.os.Bundle;

import nucleus.PresenterFactoryMock;
import nucleus.factory.RequiresPresenter;
import nucleus.presenter.Presenter;

public class NucleusFragmentTestActivity extends Activity {
    public TestFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
            getFragmentManager().beginTransaction().add(android.R.id.content, fragment = new TestFragment(), null).commit();
        else
            fragment = (TestFragment)getFragmentManager().findFragmentById(android.R.id.content);
    }

    @RequiresPresenter(Presenter.class)
    public static class TestFragment extends NucleusFragment {
        @Override
        public void onCreate(Bundle bundle) {
            setPresenterFactory(PresenterFactoryMock.mock());
            super.onCreate(bundle);
        }
    }
}
