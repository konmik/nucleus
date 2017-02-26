package nucleus.example.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class OnScrollPaging extends RecyclerView.OnScrollListener {

    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private Runnable requestNext;

    public OnScrollPaging(LinearLayoutManager layoutManager, RecyclerView.Adapter adapter, Runnable requestNext) {
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
            requestNext.run();
        }
    }
}
