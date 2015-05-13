package nucleus.example.main;

import android.app.Fragment;
import android.os.Bundle;

import nucleus.example.R;
import nucleus.view.NucleusActivity;

public class MainActivity extends NucleusActivity<MainPresenter> {

    FragmentStack fragmentStack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentStack = new FragmentStack(getFragmentManager(), R.id.fragmentContainer);

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
