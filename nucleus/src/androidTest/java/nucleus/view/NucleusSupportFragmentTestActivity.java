package nucleus.view;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import nucleus.manager.RequiresPresenter;
import nucleus.presenter.Presenter;

public class NucleusSupportFragmentTestActivity extends FragmentActivity {
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
    }
}
