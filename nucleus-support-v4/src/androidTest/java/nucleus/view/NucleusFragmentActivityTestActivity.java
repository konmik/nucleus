package nucleus.view;

import android.os.Bundle;

import nucleus.presenter.Presenter;
import nucleus.factory.RequiresPresenter;

@RequiresPresenter(Presenter.class)
public class NucleusFragmentActivityTestActivity extends NucleusFragmentActivity<Presenter> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
