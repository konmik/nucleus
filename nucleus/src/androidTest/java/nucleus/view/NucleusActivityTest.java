package nucleus.view;

import nucleus.presenter.Presenter;

public class NucleusActivityTest extends BaseViewTest<NucleusActivityTestActivity> {

    public NucleusActivityTest() {
        super(NucleusActivityTestActivity.class);
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
