package nucleus.example.ui.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.CheckBox;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nucleus.example.R;
import nucleus.example.util.Injector;
import nucleus.view.NucleusAppCompatActivity;

public class MainActivity extends NucleusAppCompatActivity<MainPresenter> {

    @Inject SharedPreferences pref;

    @Bind(R.id.button_delay) CheckBox delay;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        ((Injector) getApplication()).inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        delay.setChecked(pref.getInt("delay", 0) != 0);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new MainFragment())
                .commit();
    }

    public void push(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commit();
    }

    public void replace(Fragment fragment) {
        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit();
    }

    @OnClick(R.id.button_delay)
    void onButtonDelay() {
        pref.edit()
            .putInt("delay", delay.isChecked() ? 5 : 0)
            .apply();
    }
}
