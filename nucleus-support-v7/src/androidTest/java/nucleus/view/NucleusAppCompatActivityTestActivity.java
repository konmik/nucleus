package nucleus.view;

import android.os.Bundle;

import nucleus.factory.RequiresPresenter;
import nucleus.presenter.Presenter;

@RequiresPresenter(Presenter.class)
public class NucleusAppCompatActivityTestActivity extends NucleusAppCompatActivity<Presenter> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
