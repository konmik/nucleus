package nucleus.example.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import rx.functions.Action0;

public class OnScrollPaging extends RecyclerView.OnScrollListener {

    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private Action0 requestNext;

    public OnScrollPaging(LinearLayoutManager layoutManager, RecyclerView.Adapter adapter, Action0 requestNext) {
        this.layoutManager = layoutManager;
        this.adapter = adapter;
        this.requestNext = requestNext;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int visibleItemCount = recyclerView.getChildCount();
        int loadedItemCount = adapter.getItemCount();
        int firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition();

        if (loadedItemCount - firstVisibleItem - visibleItemCount < 3) {
            requestNext.call();
        }
    }
}
