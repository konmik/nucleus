package nucleus.example.network;

import android.text.Html;
import com.google.gson.annotations.SerializedName;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ServerAPI {

    public static final String ENDPOINT = "http://api.icndb.com";

    public static class Item {
        @SerializedName("joke")
        public String text;

        @Override
        public String toString() {
            return Html.fromHtml(text).toString();
        }
    }

    public static class Response {
        @SerializedName("value")
        public Item[] items;
    }

    @GET("/jokes/random/10")
    void getItems(@Query("firstName") String firstName, @Query("lastName") String lastName, Callback<Response> callback);
}
