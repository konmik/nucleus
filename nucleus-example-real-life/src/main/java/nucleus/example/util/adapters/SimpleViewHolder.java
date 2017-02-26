package nucleus.example.util.adapters;

import android.view.View;
import android.widget.TextView;

import nucleus.example.util.functions.Action1;

public class SimpleViewHolder<T> extends BaseViewHolder<T> {

    private final TextView textView;
    private T item;

    public SimpleViewHolder(View view, Action1<T> onClick) {
        super(view);
        this.textView = (TextView) view.findViewById(android.R.id.text1);
        view.setOnClickListener(v -> onClick.call(item));
    }

    @Override
    public void bind(T item) {
        this.item = item;
        textView.setText(item.toString());
    }
}
