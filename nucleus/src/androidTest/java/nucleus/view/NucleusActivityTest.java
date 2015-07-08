package nucleus.view;

public class NucleusActivityTest extends BaseViewTest<NucleusActivityTestActivity> {

    public NucleusActivityTest() {
        super(NucleusActivityTestActivity.class);
    }

    @Override
    protected ViewWithPresenter getView() {
        return getActivity();
    }
}
