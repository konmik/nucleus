package nucleus.view;

import android.support.annotation.Nullable;

public class OptionalView<V> {

    @Nullable
    public final V view;

    public OptionalView(@Nullable V view) {
        this.view = view;
    }
}
