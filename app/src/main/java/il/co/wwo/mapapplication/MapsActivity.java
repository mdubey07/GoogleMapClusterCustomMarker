package il.co.wwo.mapapplication;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import il.co.wwo.mapapplication.models.MarkerClusterRenderer;
import il.co.wwo.mapapplication.models.MyItem;
import il.co.wwo.mapapplication.models.PostResponse;
import il.co.wwo.mapapplication.models.WPPost;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    // Declare a variable for the cluster manager.
    private ClusterManager<MyItem> mClusterManager;
    private ArrayList<WPPost> postList = new ArrayList<>();
    private ArrayList<PostResponse> wpPostList = new ArrayList<>();
    double lat = 32.899049;
    double lng = 35.447474;
    //private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        //mapView = findViewById(R.id.map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    private void setupMarkers(){

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<>(this, mMap);

        mClusterManager.setRenderer(new MarkerClusterRenderer(this, mMap, mClusterManager));

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        if(wpPostList != null){

            for(PostResponse post: wpPostList){
                MyItem offsetItem = new MyItem(post.getLatitude(), post.getLongitude(), post.getTitle(),"test snippet");
                offsetItem.setKmaway(post.getDistance());

                offsetItem.setImgPath(post.getThumbnail());
                mClusterManager.addItem(offsetItem);
            }
        }
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

        //When Map Loads Successfully
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                //loadPost();
                //setUpClusterer();
                loadWpPosts();

            }
        });


    }

    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name, String km) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        CircleImageView markerImage = marker.findViewById(R.id.user_dp);
        markerImage.setImageResource(resource);
        TextView txt_name = marker.findViewById(R.id.post_name);
        txt_name.setText(_name);
        TextView kmaway = marker.findViewById(R.id.kmaway);
        kmaway.setText(km);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    private void loadPost() {

//     double lat = 26.856690;
//     double lng = 80.939850;

        double km = 1.5;
        String url = "http://wntechs.com/";
        String imgPath = "http://wntechs.com/assets/img/testimonial/pro_1.jpg";

        //LatLngBound will cover all your marker on Google Maps
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        MarkerOptions markerOptions = new MarkerOptions();


        for (int i = 0; i < 10; i++) {

            double offset = .01621;

            WPPost post = new WPPost();
            post.setTitle("Title_" + i);
            post.setId(String.valueOf(i));
            post.setLatitude(lat);
            lat += offset;
            post.setLongitude(lng);
            lng += offset;
            post.setUrl(url);
            post.setImgPath(imgPath);
            post.setKmaway(km += .21);
            double roundOff = Math.round(post.getKmaway() * 100.0) / 100.0;

            Log.e("lat", String.valueOf(lat));
            Log.e("long", String.valueOf(lng));
            //assigning the new Lat and Lot values
            LatLng customMarkerLocation = new LatLng(post.getLatitude(), post.getLongitude());


            mMap.addMarker(markerOptions.position(customMarkerLocation).
                    icon(BitmapDescriptorFactory.fromBitmap(
                            createCustomMarker(MapsActivity.this, R.drawable.g1, post.getTitle(), roundOff + " KM"))))
                    .setTitle(post.getTitle());
            Marker m = mMap.addMarker(markerOptions);
            m.showInfoWindow();

            builder.include(customMarkerLocation);

        }

        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        mMap.moveCamera(cu);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

    }

    //For Marker Clustering

    private void setUpClusterer() {


        //mClusterManager.setAnimation(false);
        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, mMap);

        mClusterManager.setRenderer(new MarkerClusterRenderer(this, mMap, mClusterManager));


        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        //addItems();
        //loadPost();
    }

    private void addItems() {

        // Set some lat/lng coordinates to start with.
//        double lat = 51.5145160;
//        double lng = -0.1270060;

        double km = 1.5;
        String url = "http://wntechs.com/";
        String imgPath = "http://wntechs.com/assets/img/testimonial/pro_1.jpg";

        final double min = .1;
        final double max = 50;


        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 30; i++) {
            double offset = i / 60d;
            //final double random = new Random().nextDouble();
//            Random r = new Random();
//            double random = min + (max - min) * r.nextDouble();
//            offset = offset + random;
            lat = lat + offset;
            lng = lng + offset;
            //Log.e("random", String.valueOf(random));

            // Set the lat/long coordinates for the marker.
//            double lat = 51.5009;
//            double lng = -0.122;

//            // Set the title and snippet strings.
//            String title = "This is the Title "+i;
//            String snippet = "This is the snippet "+i;
//
//            // Create a cluster item for the marker and set the title and snippet using the constructor.
//            MyItem infoWindowItem = new MyItem(lat, lng, title, snippet);
//
//            // Add the cluster item (marker) to the cluster manager.
//            mClusterManager.addItem(infoWindowItem);


            MyItem offsetItem = new MyItem(lat, lng);
            offsetItem.setKmaway(km += .21);
            mClusterManager.addItem(offsetItem);


        }


    }


}
