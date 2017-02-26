package nucleus.example.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.ButterKnife;
import icepick.Icepick;
import nucleus.example.util.Injector;
import nucleus5.factory.PresenterFactory;
import nucleus5.presenter.Presenter;
import nucleus5.view.NucleusSupportFragment;

public class BaseFragment<P extends Presenter> extends NucleusSupportFragment<P> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        final PresenterFactory<P> superFactory = super.getPresenterFactory();
        setPresenterFactory(superFactory == null ? null : (PresenterFactory<P>) () -> {
            P presenter = superFactory.createPresenter();
            ((Injector) getActivity().getApplication()).inject(presenter);
            return presenter;
        });
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Icepick.saveInstanceState(this, bundle);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
