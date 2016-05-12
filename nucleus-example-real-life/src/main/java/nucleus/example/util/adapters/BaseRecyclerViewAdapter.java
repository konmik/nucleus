package nucleus.example.util.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<ViewHolderType> types;

    public BaseRecyclerViewAdapter(ViewHolderType... types) {
        this.types = Collections.unmodifiableList(asList(types));
    }

    protected abstract List<?> getItems();

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return types.get(viewType).create(parent);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.bind(getItems().get(position));
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItems().get(position);
        for (int t = 0, size = types.size(); t < size; t++) {
            if (types.get(t).isOfItem(item))
                return t;
        }
        throw new IllegalStateException("No view holder is registered for item: " + item + " at position: " + position);
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }
}
