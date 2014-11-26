package nucleus;

import android.os.Bundle;

public class Mock {
    public static final String VALUE_KEY = "value";

    public static Bundle createTestBundle(long value) {
        Bundle bundle = new Bundle();
        bundle.putLong(VALUE_KEY, value);
        return bundle;
    }

    public static long getTestBundleValue(Bundle bundle) {
        return bundle == null ? 0 : bundle.getLong(VALUE_KEY, 0);
    }
}
