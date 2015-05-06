package nucleus.view;

import nucleus.presenter.Presenter;

public class NucleusActionBarActivityTest extends BaseViewTest<NucleusActionBarActivityTestActivity> {

    public NucleusActionBarActivityTest() {
        super(NucleusActionBarActivityTestActivity.class);
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
