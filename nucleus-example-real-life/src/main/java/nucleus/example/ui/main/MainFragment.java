package nucleus.example.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.OnClick;
import nucleus.example.R;
import nucleus.example.network.ServerAPI;
import nucleus.example.ui.base.BaseFragment;
import nucleus.example.ui.item.ItemFragment;
import nucleus.example.util.OnScrollPaging;
import nucleus.example.util.PageBundle;
import nucleus.example.util.RxPager;
import nucleus.example.util.adapters.ClassViewHolderType;
import nucleus.example.util.adapters.SimpleListAdapter;
import nucleus.example.util.adapters.SimpleViewHolder;
import nucleus.factory.RequiresPresenter;

import static java.util.Arrays.asList;

@RequiresPresenter(MainPresenter.class)
public class MainFragment extends BaseFragment<MainPresenter> {

    @Bind(R.id.check1) CheckedTextView check1;
    @Bind(R.id.check2) CheckedTextView check2;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    private String itemsName;
    private SimpleListAdapter<ServerAPI.Item> adapter;
    private RxPager pager;

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

        pager = new RxPager(10, page -> {
            adapter.showProgress();
            getPresenter().requestNext(page);
        });

        adapter = new SimpleListAdapter<>(R.layout.recycler_view_progress,
            new ClassViewHolderType<>(ServerAPI.Item.class, R.layout.item, v -> new SimpleViewHolder<>(v, this::onItemClick)));
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new OnScrollPaging(layoutManager, adapter, pager::next));
    }

    void onItems(PageBundle<ServerAPI.Response> page, String name) {
        this.itemsName = name;

        ServerAPI.Item[] items = page.data.items;
        pager.received(items.length);

        check1.setChecked(name.equals(MainPresenter.NAME_1));
        check2.setChecked(name.equals(MainPresenter.NAME_2));

        adapter.hideProgress();
        if (page.page != 0)
            adapter.add(asList(items));
        else {
            recyclerView.scrollToPosition(0);
            adapter.set(asList(items));
        }
    }

    void onNetworkError(Throwable throwable) {
        adapter.hideProgress();
        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    @OnClick({R.id.check1, R.id.check2})
    void onCheckClick(View view) {
        pager.reset();
        adapter.showProgress();
        getPresenter().request(view.getId() == R.id.check1 ? MainPresenter.NAME_1 : MainPresenter.NAME_2);
    }

    private void onItemClick(ServerAPI.Item item) {
        ((MainActivity) getActivity()).push(ItemFragment.create(item.id, itemsName));
    }
}
