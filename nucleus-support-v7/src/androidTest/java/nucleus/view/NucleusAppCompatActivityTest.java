package nucleus.view;

import nucleus.presenter.Presenter;

public class NucleusAppCompatActivityTest extends BaseViewTest<NucleusAppCompatActivityTestActivity> {

    public NucleusAppCompatActivityTest() {
        super(NucleusAppCompatActivityTestActivity.class);
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
