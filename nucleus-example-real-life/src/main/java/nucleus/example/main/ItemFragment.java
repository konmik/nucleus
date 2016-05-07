package nucleus.example.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import icepick.State;
import nucleus.example.R;
import nucleus.example.base.BaseFragment;

public class ItemFragment extends BaseFragment<MainPresenter> {

    @Bind(android.R.id.text1) TextView textView;

    @State String text;

    public ItemFragment() {
    }

    @SuppressLint("ValidFragment")
    public ItemFragment(String text) {
        this.text = text;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView.setText(text);
    }

    @OnClick(R.id.buttonReplace)
    void onButtonReplace() {
        ((MainActivity) getActivity()).replace(new MainFragment());
    }

    @OnClick(R.id.buttonBack)
    void onButtonBack() {
        getActivity().onBackPressed();
    }
}
