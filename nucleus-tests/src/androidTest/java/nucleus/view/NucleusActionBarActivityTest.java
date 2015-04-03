package nucleus.view;

import nucleus.presenter.Presenter;

public class NucleusActionBarActivityTest extends BaseViewTest<NucleusActionBarActivityTestActivity> {

    public NucleusActionBarActivityTest() {
        super(NucleusActionBarActivityTestActivity.class);
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
