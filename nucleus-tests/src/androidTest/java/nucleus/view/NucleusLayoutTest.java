package nucleus.view;

import nucleus.presenter.Presenter;

public class NucleusLayoutTest extends BaseViewTest<NucleusLayoutTestActivity> {

    private NucleusLayoutTestActivity activity;

    public NucleusLayoutTest() {
        super(NucleusLayoutTestActivity.class);
    }

    @Override
    protected Class<? extends Presenter> getPresenterClass() {
        return Presenter.class;
    }

    @Override
    protected NucleusLayoutTestActivity.TestView getView() {
        return getActivity().view;
    }

    @Override
    protected Presenter getViewPresenter() {
        return getView().getPresenter();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    @Override
    protected void waitForDestructionComplete() {
        super.waitForDestructionComplete();
        waitFor(new Expression() {
            @Override
            public boolean call() {
                return activity.detached;
            }
        });
    }
}
