package nucleus.example.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import nucleus.example.R;
import nucleus.view.NucleusAppCompatActivity;

public class MainActivity extends NucleusAppCompatActivity<MainPresenter> {

    FragmentStack fragmentStack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentStack = new FragmentStack(getSupportFragmentManager(), R.id.fragmentContainer);

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
