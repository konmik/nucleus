package nucleus.view;

public class NucleusFragmentTest extends BaseViewTest<NucleusFragmentTestActivity> {
    public NucleusFragmentTest() {
        super(NucleusFragmentTestActivity.class);
    }

    @Override
    protected ViewWithPresenter getView() {
        return getActivity().fragment;
    }
}
