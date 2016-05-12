package nucleus.example.util.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ClassViewHolderType<T> implements ViewHolderType<T> {

    public interface ViewHolderFactory<T> {
        BaseViewHolder<T> call(View view);
    }

    private final Class<T> tClass;
    private final int layoutId;
    private final ViewHolderFactory<T> holderFactory;

    public ClassViewHolderType(Class<T> tClass, int layoutId, ViewHolderFactory<T> holderFactory) {
        this.tClass = tClass;
        this.layoutId = layoutId;
        this.holderFactory = holderFactory;
    }

    @Override
    public boolean isOfItem(Object item) {
        return tClass.isAssignableFrom(item.getClass());
    }

    @Override
    public BaseViewHolder<T> create(ViewGroup parent) {
        return holderFactory.call(
            LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false));
    }
}
