package nucleus.example.main;

import android.os.Bundle;
import android.view.View;

import nucleus.example.R;
import nucleus.view.NucleusActivity;

public class MainActivity extends NucleusActivity<MainPresenter> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, new MainFragment(), null)
                .commit();
        }
    }
}
