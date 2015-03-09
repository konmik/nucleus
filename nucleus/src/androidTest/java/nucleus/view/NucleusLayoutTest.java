package nucleus.view;

import nucleus.presenter.Presenter;

public class NucleusLayoutTest extends BaseViewTest<NucleusLayoutTestActivity> {

    public NucleusLayoutTest() {
        super(NucleusLayoutTestActivity.class);
    }

    @Override
    protected NucleusLayoutTestActivity.TestView getView() {
        return getActivity().view;
    }

    @Override
    protected Presenter getViewPresenter() {
        return getView().getPresenter();
    }
}
