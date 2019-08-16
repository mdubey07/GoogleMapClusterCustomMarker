package il.co.wwo.mapapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import il.co.wwo.mapapplication.models.PostResponse;
import mumayank.com.airlocationlibrary.AirLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static  GoogleMap mMap;
    private AirLocation airLocation;
    private ArrayList<PostResponse> wpPostList = new ArrayList<>();
    double lat = 32.899049;
    double lng = 35.447474;
    private ArrayList<Bitmap> markerLayoutList = new ArrayList<>();
    HashMap<Integer, PostResponse> hashMapMarker = new HashMap<>();
    private SupportMapFragment mapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        airLocation = new AirLocation(this, true, true, new AirLocation.Callbacks() {
            @Override
            public void onSuccess( Location location) {
                // do something
                lat = location.getLatitude();
                lng = location.getLongitude();
                /* lat = 32.899049;
                 lng = 35.447474;*/
                mapFragment.getMapAsync(MainActivity.this);
            }

            @Override
            public void onFailed( AirLocation.LocationFailedEnum locationFailedEnum) {
                // do something
                Toast.makeText(MainActivity.this,"Current location cann't be determined.", Toast.LENGTH_LONG).show();
            }
        });

    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng currentLocation = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                PostResponse post = hashMapMarker.get(marker.hashCode());
                if(post != null){
                    Toast.makeText(MainActivity.this,post.getTitle(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
                    intent.putExtra("url", post.getLink());
                    startActivity(intent);
                }
                return true;
            }
        });
        loadWpPosts();

    }


    private void setupMarkers(){
        if(wpPostList != null){
            final LatLngBounds.Builder builder = new LatLngBounds.Builder();
            markerLayoutList.clear();
            for(final PostResponse post : wpPostList){

                final LatLng location = new LatLng(post.getLatitude(), post.getLongitude());
                builder.include(location);
                Picasso.get()
                        .load(post.getThumbnail())
                        .resize(50,50)
                        .error(R.drawable.g1)
                        .fetch(new com.squareup.picasso.Callback(){
                            @Override
                            public void onSuccess(){
                                Log.e("marker", "loaded  marker " + post.getId());
                                MarkerOptions options = new MarkerOptions();
                                options.position(location);
                                options.title(post.getTitle());

                                Bitmap icon = createCustomMarker(MainActivity.this,post.getThumbnail(),post.getTitle(),post.getDistance() + " Km", location);
                                markerLayoutList.add(icon);
                                options.icon(BitmapDescriptorFactory.fromBitmap(icon));
                                Marker marker = mMap.addMarker(options);
                                hashMapMarker.put(marker.hashCode(),post);
                            }
                            @Override
                            public void onError(Exception e){
                                Log.e("marker", "error loading  marker " + post.getId());
                            }
                        });
            }

           /* LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
            mMap.moveCamera(cu);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);*/
        }
    }

    public static Bitmap createCustomMarker(Context context, String imgPath, String _name, String km, LatLng opt) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_style_2, null);

        final CircleImageView markerImage = marker.findViewById(R.id.user_dp);

        Picasso.get()
                .setLoggingEnabled(true);
        Picasso.get()
                .load(imgPath)
                .resize(50,50)
                .error(R.drawable.g1)
                .into(markerImage);
        TextView txt_name = marker.findViewById(R.id.post_name);
        txt_name.setText(_name);
        TextView kmaway = marker.findViewById(R.id.kmaway);
        kmaway.setText(km);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((MainActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    private void loadWpPosts(){

        Call<List<PostResponse>> apiRequest = APIClient.getClient().getPostsRequest(lat, lng);
        apiRequest.enqueue(new Callback<List<PostResponse>>(){

            @Override
            public void onResponse(Call<List<PostResponse>> call, Response<List<PostResponse>> response){
                if(response.isSuccessful()){
                    wpPostList.clear();
                    if(response.body() != null) {
                        wpPostList.addAll(response.body());
                        setupMarkers();
                    }
                    else
                        Toast.makeText(getApplicationContext(),"No items found nearby", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PostResponse>> call, Throwable t){

                Toast.makeText(getApplicationContext(),"Some error in webservice call", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // override and call airLocation object's method by the same name
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        airLocation.onActivityResult(requestCode, resultCode, data);
    }

    // override and call airLocation object's method by the same name
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
