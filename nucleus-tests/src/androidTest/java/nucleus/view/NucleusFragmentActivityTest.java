package nucleus.view;

import nucleus.presenter.Presenter;

public class NucleusFragmentActivityTest extends BaseViewTest<NucleusFragmentActivityTestActivity> {

    public NucleusFragmentActivityTest() {
        super(NucleusFragmentActivityTestActivity.class);
    }

    @Override
    protected Class<? extends Presenter> getPresenterClass() {
        return Presenter.class;
    }

    @Override
    protected Object getView() {
        return getActivity();
    }

    @Override
    protected Presenter getViewPresenter() {
        return getActivity().getPresenter();
    }
}
