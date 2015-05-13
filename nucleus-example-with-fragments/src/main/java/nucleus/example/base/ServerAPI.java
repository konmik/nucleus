package nucleus.example.base;

import android.text.Html;

import com.google.gson.annotations.SerializedName;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface ServerAPI {

    String ENDPOINT = "http://api.icndb.com";

    class Item {
        @SerializedName("joke")
        public String text;

        @Override
        public String toString() {
            return Html.fromHtml(text).toString();
        }
    }

    class Response {
        @SerializedName("value")
        public Item[] items;
    }

    @GET("/jokes/random/10")
    Observable<Response> getItems(@Query("firstName") String firstName, @Query("lastName") String lastName);
}
