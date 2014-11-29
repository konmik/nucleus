package nucleus.example.main;

import android.widget.TextView;
import nucleus.example.R;
import nucleus.presenter.PresenterCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml")
public class MainActivityTest {

    @Test
    public void testPublishCounter() throws Exception {

        MainActivity.presenterCreator = new PresenterCreator<MainPresenter>() {
            @Override
            public MainPresenter createPresenter() {
                return Mockito.mock(MainPresenter.class);
            }
        };

        ActivityController<MainActivity> activityController = Robolectric.buildActivity(MainActivity.class);
        activityController.setup();
        MainActivity activity = activityController.get();

        activity.publishCounter(100500);
        TextView textView = (TextView)activity.findViewById(R.id.counter);
        assertEquals(textView.getText().toString(), "100500");
    }
}
