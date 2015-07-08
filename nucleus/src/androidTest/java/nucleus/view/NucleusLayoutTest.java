package nucleus.view;

public class NucleusLayoutTest extends BaseViewTest<NucleusLayoutTestActivity> {

    public NucleusLayoutTest() {
        super(NucleusLayoutTestActivity.class);
    }

    @Override
    protected NucleusLayoutTestActivity.TestView getView() {
        return getActivity().view;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @Override
    protected void waitForDestructionComplete() {
        super.waitForDestructionComplete();
        final NucleusLayoutTestActivity activity = getActivity();
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
                assertNotNull(getView().getPresenter());
            }
        });
    }
}
