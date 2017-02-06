package nucleus.view;

import android.support.annotation.Nullable;

public class OptionalView<V> {

    @Nullable
    public final V view;

    public OptionalView(@Nullable V view) {
        this.view = view;
    }

    @Override
    public String toString() {
        return "OptionalView{" +
            "view=" + view +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionalView<?> that = (OptionalView<?>) o;

        return view != null ? view.equals(that.view) : that.view == null;
    }

    @Override
    public int hashCode() {
        return view != null ? view.hashCode() : 0;
    }
}
