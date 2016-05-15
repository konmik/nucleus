package nucleus.example.util.adapters;

import android.view.View;

public class EmptyViewHolder extends BaseViewHolder<Object> {

    public EmptyViewHolder(View view) {
        super(view);
    }

    @Override
    public void bind(Object item) {

    }
}
