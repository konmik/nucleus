package nucleus.example.network;

import javax.inject.Inject;

import nucleus.example.base.App;
import nucleus.model.Loader;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class RetrofitLoader<ResponseT> extends Loader<ResponseT> {

    @Inject protected ServerAPI api;

    protected int request;

    public RetrofitLoader() {
    }

    protected void request() {
        final int r = ++request;

        doRequest(new Callback<ResponseT>() {
            @Override
            public void success(ResponseT responseT, Response response) {
                if (r == request) // ignore all requests except the last one
                    notifyReceivers(responseT);
            }

            @Override
            public void failure(RetrofitError error) {
                App.reportError(error.getMessage());
            }
        });
    }

    protected abstract void doRequest(Callback<ResponseT> callback);
}
