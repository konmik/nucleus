package nucleus.example.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import nucleus.example.R;
import nucleus.example.base.BaseFragment;
import nucleus.example.network.ServerAPI;
import nucleus.example.item.ItemFragment;
import nucleus.factory.RequiresPresenter;

@RequiresPresenter(MainPresenter.class)
public class MainFragment extends BaseFragment<MainPresenter> {

    @Bind(R.id.check1) CheckedTextView check1;
    @Bind(R.id.check2) CheckedTextView check2;
    @Bind(R.id.listView) ListView listView;

    private String itemsName;
    private ArrayAdapter<ServerAPI.Item> adapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null)
            getPresenter().request(MainPresenter.DEFAULT_NAME);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        check1.setText(MainPresenter.NAME_1);
        check2.setText(MainPresenter.NAME_2);

        listView.setAdapter(adapter = new ArrayAdapter<>(getActivity(), R.layout.item));
    }

    void onItems(ServerAPI.Item[] items, String name) {
        this.itemsName = name;

        check1.setChecked(name.equals(MainPresenter.NAME_1));
        check2.setChecked(name.equals(MainPresenter.NAME_2));

        adapter.clear();
        adapter.addAll(items);
    }

    void onNetworkError(Throwable throwable) {
        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    @OnClick({R.id.check1, R.id.check2})
    void onCheckClick(View view) {
        getPresenter().request(view.getId() == R.id.check1 ? MainPresenter.NAME_1 : MainPresenter.NAME_2);
    }

    @OnItemClick(R.id.listView)
    void onItemClick(int position) {
        ServerAPI.Item item = adapter.getItem(position);
        ((MainActivity) getActivity()).push(ItemFragment.create(item.id, itemsName));
    }
}
