package nucleus.view;

import nucleus.presenter.Presenter;

public class NucleusFragmentTest extends BaseViewTest<NucleusFragmentTestActivity> {
    public NucleusFragmentTest() {
        super(NucleusFragmentTestActivity.class);
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
