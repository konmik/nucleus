package nucleus.view;

import android.os.Bundle;

import nucleus.presenter.Presenter;

@RequiresPresenter(Presenter.class)
public class NucleusFragmentActivityTestActivity extends NucleusFragmentActivity<Presenter> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
