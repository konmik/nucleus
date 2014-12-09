package nucleus.example.utils;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class ViewFn {

    public static void fadeIn(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0);
            view.animate().alpha(1).setDuration(300).start();
        }
    }
}
