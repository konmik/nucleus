package nucleus.example.util;

import io.reactivex.functions.Consumer;

public class RxPager {

    public static final int NOT_REQUESTED = -1;

    private final int pageSize;
    private int size = 0;
    private int requested = NOT_REQUESTED;
    private Consumer<Integer> onRequest;

    public RxPager(int pageSize, Consumer<Integer> onRequest) {
        this.pageSize = pageSize;
        this.onRequest = onRequest;
    }

    public void next() {
        if (size % pageSize == 0 && requested != size) {
            requested = size;
            onRequest.accept(size / pageSize);
        }
    }

    public void received(int itemCount) {
        size += itemCount;
    }

    public void reset() {
        size = 0;
        requested = NOT_REQUESTED;
    }
}
