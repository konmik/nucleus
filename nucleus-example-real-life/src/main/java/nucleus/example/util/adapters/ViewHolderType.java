package nucleus.example.util.adapters;

import android.view.ViewGroup;

public interface ViewHolderType<T> {
    boolean isOfItem(Object item);
    BaseViewHolder<T> create(ViewGroup parent);
}
