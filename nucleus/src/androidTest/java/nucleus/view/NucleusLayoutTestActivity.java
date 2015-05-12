package nucleus.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.ViewGroup;

import nucleus.factory.RequiresPresenter;
import nucleus.presenter.Presenter;

public class NucleusLayoutTestActivity extends Activity {

    public TestView view;
    public boolean detached;
    public boolean detachAttachCompleted;

    @RequiresPresenter(Presenter.class)
    public static class TestView extends NucleusLayout {
        public TestView(Context context) {
            super(context);
            //noinspection ResourceType
            setId(1024);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a dummy wrapper to guarantee that NucleusLayout properly
        // unwraps contexts before casting them to activities
        setContentView(view = new TestView(new ContextWrapper(this)));
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        detached = true;
    }

    public void detachAttach() {
        final ViewGroup parent = (ViewGroup)view.getParent();
        parent.removeView(view);
        parent.post(new Runnable() {
            @Override
            public void run() {
                parent.addView(view);
                detachAttachCompleted = true;
            }
        });
    }
}
