package nucleus.view;

import android.os.Bundle;

import nucleus.manager.RequiresPresenter;
import nucleus.presenter.Presenter;

@RequiresPresenter(Presenter.class)
public class NucleusActionBarActivityTestActivity extends NucleusActionBarActivity<Presenter> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
