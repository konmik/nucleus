package nucleus.view;

import android.os.Bundle;

import nucleus.PresenterFactoryMock;
import nucleus.factory.RequiresPresenter;
import nucleus.presenter.Presenter;

@RequiresPresenter(Presenter.class)
public class NucleusActivityTestActivity extends NucleusActivity<Presenter> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setPresenterFactory(PresenterFactoryMock.mock());
        super.onCreate(savedInstanceState);
    }
}
