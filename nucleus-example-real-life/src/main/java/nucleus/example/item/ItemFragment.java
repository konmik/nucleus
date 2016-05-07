package nucleus.example.item;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import nucleus.example.R;
import nucleus.example.base.BaseFragment;
import nucleus.example.network.ServerAPI;
import nucleus.example.util.Delayed;
import nucleus.factory.RequiresPresenter;

@RequiresPresenter(ItemPresenter.class)
public class ItemFragment extends BaseFragment<ItemPresenter> {

    @Bind(android.R.id.text1) TextView textView;

    private Delayed<Integer> id = new Delayed<>(() -> getArguments().getInt("id"));
    private Delayed<String> name = new Delayed<>(() -> getArguments().getString("name"));

    public static ItemFragment create(int id, String name) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("name", name);
        ItemFragment fragment = new ItemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
            getPresenter().requestItem(id.get(), name.get());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item, container, false);
    }

    void onItem(ServerAPI.Item item) {
        textView.setText(item.toString());
    }

    void onNetworkError(Throwable throwable) {
        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
    }
}
