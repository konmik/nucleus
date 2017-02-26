package nucleus.example.network;

import android.text.Html;

import com.google.gson.annotations.SerializedName;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServerAPI {

    String ENDPOINT = "http://api.icndb.com";

    class Item {

        public int id;

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

    class ItemResponse {
        @SerializedName("value")
        public Item item;
    }

    @GET("/jokes/random/10")
    Observable<Response> getItems(@Query("firstName") String firstName, @Query("lastName") String lastName, @Header("pageNumber") int pageNumberIgnored);

    @GET("/jokes/{id}")
    Observable<ItemResponse> getItem(@Path("id") int id, @Query("firstName") String firstName, @Query("lastName") String lastName);
}
