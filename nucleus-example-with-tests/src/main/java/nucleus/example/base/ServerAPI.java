package nucleus.example.base;

import android.text.Html;

import com.google.gson.annotations.SerializedName;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface ServerAPI {

    public static final String ENDPOINT = "http://api.icndb.com";

    public static class Item {
        @SerializedName("joke")
        public String text;

        public Item() {
        }

        public Item(String text) {
            this.text = text;
        }

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
    Observable<Response> getItems(@Query("firstName") String firstName, @Query("lastName") String lastName);
}
