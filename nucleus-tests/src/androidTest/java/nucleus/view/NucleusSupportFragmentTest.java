package nucleus.view;

import nucleus.presenter.Presenter;

public class NucleusSupportFragmentTest extends BaseViewTest<NucleusSupportFragmentTestActivity> {
    public NucleusSupportFragmentTest() {
        super(NucleusSupportFragmentTestActivity.class);
    }

    @Override
    protected Class<? extends Presenter> getPresenterClass() {
        return Presenter.class;
    }

    @Override
    protected Object getView() {
        return getActivity().fragment;
    }

    @Override
    protected Presenter getViewPresenter() {
        return getActivity().fragment.getPresenter();
    }
}
