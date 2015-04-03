package nucleus.view;

import android.os.Bundle;

import nucleus.manager.RequiresPresenter;
import nucleus.presenter.Presenter;

@RequiresPresenter(Presenter.class)
public class NucleusActivityTestActivity extends NucleusActivity<Presenter> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
