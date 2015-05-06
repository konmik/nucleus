package nucleus.view;

import nucleus.presenter.Presenter;

public class NucleusLayoutTest extends BaseViewTest<NucleusLayoutTestActivity> {

    private NucleusLayoutTestActivity activity;

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

    @Override
    public void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    @Override
    protected void waitForDestructionComplete() {
        super.waitForDestructionComplete();
        waitFor(new Condition() {
            @Override
            public boolean call() {
                return activity.detached;
            }
        });
    }

    // https://github.com/konmik/nucleus/issues/3
    // can't reproduce
    public void testDetachAttachShouldNotLeak() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().detachAttach();
            }
        });
        waitFor(new Condition() {
            @Override
            public boolean call() {
                return getActivity().detachAttachCompleted;
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                assertProvideOnce();
                assertNotNull(getView().getPresenter());
            }
        });
    }
}
