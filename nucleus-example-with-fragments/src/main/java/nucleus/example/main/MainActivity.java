package nucleus.example.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import nucleus.example.R;
import nucleus.view.NucleusAppCompatActivity;
import nucleus.view.ViewWithPresenter;

public class MainActivity extends NucleusAppCompatActivity<MainPresenter> {

    private FragmentStack fragmentStack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentStack = new FragmentStack(this, getSupportFragmentManager(), R.id.fragmentContainer, new FragmentStack.OnFragmentRemovedListener() {
            @Override
            public void onFragmentRemoved(Fragment fragment) {
                if (fragment instanceof ViewWithPresenter)
                    ((ViewWithPresenter)fragment).getPresenter().destroy();
            }
        });

        if (savedInstanceState == null)
            fragmentStack.replace(new MainFragment());
    }

    public void push(Fragment fragment) {
        fragmentStack.push(fragment);
    }

    @Override
    public void onBackPressed() {
        if (!fragmentStack.pop())
            super.onBackPressed();
    }

    public void replace(Fragment fragment) {
        fragmentStack.replace(fragment);
    }
}
