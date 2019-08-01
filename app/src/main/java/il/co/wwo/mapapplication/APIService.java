package il.co.wwo.mapapplication;

import java.util.List;

import il.co.wwo.mapapplication.models.PostResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/*
 * File to contain all API URLs
 * */
public interface APIService {

    @GET("pins/{latitude}/{longitude}")
    Call<List<PostResponse>> getPostsRequest(@Path("latitude") double latitude, @Path("longitude") double longitude);

}
