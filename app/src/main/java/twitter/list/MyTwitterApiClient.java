package twitter.list;

import com.google.gson.JsonElement;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by agoyal3 on 2/27/16.
 */
public class MyTwitterApiClient extends TwitterApiClient {
    private final String TAG = getClass().getSimpleName();
    private TwitterSession mSession;

    public MyTwitterApiClient(TwitterSession session) {
        super(session);
        mSession = session;
    }

    public CustomService getCustomService() {
        return getService(CustomService.class);
    }

}

interface CustomService {
    @POST("/1.1/lists/create.json")
    void createList(@Query("name") String name, @Query("mode") String mode, Callback<JsonElement> cb);

    @GET("/1.1/lists/show.json")
    void getList(@Query("slug") String slug, @Query("owner_screen_name") String owner_screen_name, Callback<JsonElement> cb);

    @GET("/1.1/lists/members.json")
    void getListMembers(@Query("slug") String slug, @Query("owner_screen_name") String owner_screen_name, Callback<JsonElement> cb);

    @POST("/1.1/lists/members/create.json")
    void createListMember(@Query("slug") String slug, @Query("owner_screen_name") String owner_screen_name,
                          @Query("screen_name") String screen_name, Callback<JsonElement> cb);

    @POST("/1.1/lists/members/destroy.json")
    void removeListMember(@Query("slug") String slug, @Query("owner_screen_name") String owner_screen_name,
                          @Query("screen_name") String screen_name, Callback<JsonElement> cb);

//    @GET("/1.1/lists/statuses.json")
//    void getListStatuses(@Query("slug") String slug, @Query("owner_screen_name") String owner_screen_name, Callback<JsonElement> cb);
}
