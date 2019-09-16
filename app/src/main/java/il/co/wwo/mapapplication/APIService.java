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

    @GET("pins/{latitude}/{longitude}/{search_radius}/{max_result}")
    Call<List<PostResponse>> getPostsRequest(@Path("latitude") double latitude, @Path("longitude") double longitude,@Path("search_radius") int radius, @Path("max_result") int max_result);

}
