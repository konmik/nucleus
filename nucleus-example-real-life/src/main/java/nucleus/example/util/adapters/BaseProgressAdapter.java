package nucleus.example.util.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

public abstract class BaseProgressAdapter extends BaseRecyclerViewAdapter {

    private final int progressViewId;
    private boolean progress;

    public BaseProgressAdapter(int progressViewId, ViewHolderType... types) {
        super(types);
        this.progressViewId = progressViewId;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == progressViewId ?
            new EmptyViewHolder(LayoutInflater.from(parent.getContext()).inflate(progressViewId, parent, false)) :
            super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (position != super.getItemCount()) {
            holder.bind(getItems().get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == super.getItemCount() ? progressViewId :
            super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (progress ? 1 : 0);
    }

    public void showProgress() {
        if (!progress) {
            progress = true;
            notifyItemInserted(getItemCount());
        }
    }

    public void hideProgress() {
        if (progress) {
            progress = false;
            notifyItemRemoved(getItemCount());
        }
    }
}
