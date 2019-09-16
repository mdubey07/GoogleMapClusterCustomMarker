package il.co.wwo.mapapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emreeran.locationlivedata.LocationLiveData;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import il.co.wwo.mapapplication.models.PostResponse;
import mumayank.com.airlocationlibrary.AirLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener {

    private static  GoogleMap mMap;
    private AirLocation airLocation;
    private HashMap<Integer,Marker> hashMapm = new HashMap<>();
    private ArrayList<PostResponse> wpPostList = new ArrayList<>();
    double lat = 31.891630;
    double lng = 34.794020;
    PrefManager prefManager;
    int search_radius, max_result;
    LatLng currentLatLng;
    private ArrayList<Bitmap> markerLayoutList = new ArrayList<>();
    HashMap<Integer, PostResponse> hashMapMarker = new HashMap<>();
    private SupportMapFragment mapFragment;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    Marker userMarker;
    LocationLiveData locationLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefManager = new PrefManager(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        TextView titleText = findViewById(R.id.tvTitle);
        titleText.setText(R.string.app_name);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        airLocation = new AirLocation(this, true, true, new AirLocation.Callbacks() {
            @Override
            public void onSuccess( Location location) {

                lat = location.getLatitude();
                lng = location.getLongitude();
                currentLatLng = new LatLng(lat, lng);
                mapFragment.getMapAsync(MainActivity.this);
            }
            @Override
            public void onFailed( AirLocation.LocationFailedEnum locationFailedEnum) {
                // do something
                Toast.makeText(MainActivity.this,"Current location can't be determined.", Toast.LENGTH_LONG).show();
            }
        });

         locationLiveData = LocationLiveData.create(
                this,
                500L,                                       // Interval
                100L,                                       // Fastest interval
                PRIORITY_HIGH_ACCURACY,     // Priority
                10F,                                        // Smallest displacement
                10000L,                                     // Expiration time
                10000L,                                     // Max wait time
                10,                                         // Number of updates
                new LocationLiveData.OnErrorCallback() {    // Error callbacks
                    @Override
                    public void onLocationSettingsException(@NotNull ApiException e) {
                        if (e instanceof ResolvableApiException) {
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(MainActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            }
                        }
                    }

                    @Override
                    public void onPermissionsMissing() {
                        // Show message
                    }
                }
        );





    }

    private void startLocationObserver(){
        locationLiveData.observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                if(location.isFromMockProvider()) {
                    LatLng tempLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    if(!location.isFromMockProvider()) {
                        if (currentLatLng.latitude != tempLocation.latitude && currentLatLng.longitude != tempLocation.longitude) {
                            Log.e("locationChangeOld", lat + ", " + lng);
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                            Log.e("locationChangeNew", lat + ", " + lng);
                            currentLatLng = tempLocation;
                            MarkerOptions uMOpt = new MarkerOptions();
                            uMOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_user));
                            uMOpt.position(currentLatLng);
                            userMarker = mMap.addMarker(uMOpt);
                            loadWpPosts();
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f), new GoogleMap.CancelableCallback() {
                                @Override
                                public void onFinish() {

                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                            Log.e("new Location", lat + ", " + lng);
                        } else {
                            Log.e("locationChange", "No change in location");
                        }
                    }
                }else{
                    //Log.e("locationChange", "No change in location" );
                }
            }
        });
    }
    @Override
    public void onCameraIdle(){
       // setupMarkers();
        loadWpPosts();
    }

    @Override
    public void onCameraMoveStarted(int i) {
       // Log.e("onCameraMoveStarted", "true");
    }

    @Override
    public void onCameraMove(){

       // Log.e("onCameraMove", "true");
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

       // googleMap.getUiSettings().setScrollGesturesEnabled(false);
        //googleMap.getUiSettings().setZoomControlsEnabled(true);
       // googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap = googleMap;
        try {
            mMap.setMyLocationEnabled(true);
        }catch (SecurityException e){
            e.printStackTrace();
        }
        final LatLng currentLocation = new LatLng(lat, lng);
        MarkerOptions uMOpt = new MarkerOptions();
        uMOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_user));
        uMOpt.position(currentLocation);
        userMarker = mMap.addMarker(uMOpt);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                startLocationObserver();
                loadWpPosts();
            }

            @Override
            public void onCancel() {

            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                final PostResponse post = hashMapMarker.get(marker.hashCode());
                if(post != null){
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.dialog_post);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                             dialog.dismiss();
                        }
                    });
                    ImageView img = dialog.findViewById(R.id.thumb_image);
                    Picasso.get().load(post.getThumbnail())
                            .fit()
                            .into(img);
                    TextView txtDistance = dialog.findViewById(R.id.distance);
                    txtDistance.setText(getString(R.string.km,String.valueOf(post.getDistance() ) ));
                    TextView txtTitle = dialog.findViewById(R.id.title);
                    txtTitle.setText(post.getTitle());
                    TextView txtDescription = dialog.findViewById(R.id.description);
                    txtDescription.setText(post.getTitle());
                    Button btn_link = dialog.findViewById(R.id.btn_link);
                    btn_link.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
                            intent.putExtra("url", post.getLink());
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                }
                return true;
            }
        });

        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
    }
    private void repositionMarkers(LatLng currentLocation , LatLng location, final PostResponse post){

        BoundaryLocation boundaryLocation = new BoundaryLocation();
        boundaryLocation.get( currentLocation , location,mMap);

        Marker m = hashMapm.get(Integer.valueOf(post.getId()));
        if(m != null) {
            m.setPosition(boundaryLocation.location);
            m.setAnchor(boundaryLocation.markerAnchor.X, boundaryLocation.markerAnchor.Y);
            Bitmap icon = createCustomMarker(MainActivity.this, post.getThumbnail(), boundaryLocation.anchor);
            m.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }else{
            Log.e("Reposition", "Marker is null for post id " + post.getId());
        }

    }
    private void calculateIntersection(LatLng currentLocation , LatLng location, final PostResponse post){
        BoundaryLocation boundaryLocation = new BoundaryLocation();
        boundaryLocation.get( currentLocation , location,mMap);

        final LatLng _location = boundaryLocation.location;
        final String _anchor = boundaryLocation.anchor;
        final BoundaryLocation.MarkerAnchor _markerAnchor = boundaryLocation.markerAnchor;
        MarkerOptions options = new MarkerOptions();
        options.position(_location);
        options.title(post.getTitle());
        options.anchor(_markerAnchor.X, _markerAnchor.Y);
        Picasso.get()
                .load(post.getThumbnail())
                .resize(50,50)
                .error(R.drawable.g1)
                .fetch(new com.squareup.picasso.Callback(){
                    @Override
                    public void onSuccess(){
                        //Log.e("marker", "loaded  marker " + post.getId());
                        //Log.e("side ", _anchor);

                        Bitmap icon = createCustomMarker(MainActivity.this,post.getThumbnail(),_anchor);
                        markerLayoutList.add(icon);
                        options.icon(BitmapDescriptorFactory.fromBitmap(icon));
                        Marker marker = mMap.addMarker(options);
                        hashMapm.put(Integer.valueOf(post.getId()), marker);
                        hashMapMarker.put(marker.hashCode(),post);
                    }
                    @Override
                    public void onError(Exception e){
                        Log.e("marker", "error loading  marker " + post.getId());
                        Bitmap icon = createCustomMarker(MainActivity.this,R.drawable.no_image,_anchor);
                        markerLayoutList.add(icon);
                        options.icon(BitmapDescriptorFactory.fromBitmap(icon));
                        Marker marker = mMap.addMarker(options);
                        hashMapm.put(Integer.valueOf(post.getId()), marker);
                        hashMapMarker.put(marker.hashCode(),post);
                    }
                });


    }

    private void setupMarkers(){
        if(wpPostList != null){
            final LatLngBounds.Builder builder = new LatLngBounds.Builder();
            VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
            markerLayoutList.clear();
            hashMapMarker.clear();
            hashMapm.clear();
            mMap.clear();
            MarkerOptions uMOpt = new MarkerOptions();
            uMOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_user));
            uMOpt.position(currentLatLng);
            userMarker = mMap.addMarker(uMOpt);
            for(final PostResponse post : wpPostList){
                LatLng currentLocation = new LatLng(lat, lng);
                final LatLng location = new LatLng(post.getLatitude(), post.getLongitude());

                if(!visibleRegion.latLngBounds.contains(location)) {
                     calculateIntersection(currentLocation, location, post);

                }else{
                    MarkerOptions options = new MarkerOptions();
                    options.position(location);
                    options.title(post.getTitle());
                    Picasso.get()
                            .load(post.getThumbnail())
                            .resize(50,50)
                            .error(R.drawable.g1)
                            .fetch(new com.squareup.picasso.Callback(){
                                @Override
                                public void onSuccess(){
                                   // Log.e("marker", "loaded  marker " + post.getId());


                                    Bitmap icon = createCustomMarker(MainActivity.this,post.getThumbnail(), "bottom");
                                    markerLayoutList.add(icon);
                                    options.icon(BitmapDescriptorFactory.fromBitmap(icon));
                                    Marker marker = mMap.addMarker(options);
                                    hashMapm.put(Integer.valueOf(post.getId()), marker);
                                    hashMapMarker.put(marker.hashCode(),post);
                                }
                                @Override
                                public void onError(Exception e){
                                    Log.e("marker", "error loading  marker " + post.getId());
                                    Bitmap icon = createCustomMarker(MainActivity.this,R.drawable.no_image, "bottom");
                                    markerLayoutList.add(icon);
                                    options.icon(BitmapDescriptorFactory.fromBitmap(icon));
                                    Marker marker = mMap.addMarker(options);
                                    hashMapm.put(Integer.valueOf(post.getId()), marker);
                                    hashMapMarker.put(marker.hashCode(),post);
                                }
                            });
                }
                builder.include(location);
            }

        }
    }

    public static Bitmap createCustomMarker(Context context, String imgPath, String anchor) {

        View marker = null;
        if(anchor.equals("top"))
            marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_top, null);
        else if(anchor.equals("left"))
            marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_left, null);
        else if(anchor.equals("bottom"))
            marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_bottom, null);
        else
            marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_right, null);

        final CircleImageView markerImage = marker.findViewById(R.id.user_dp);

        Picasso.get()
                .setLoggingEnabled(true);
        Picasso.get()
                .load(imgPath)
                .resize(50,50)
                .error(R.drawable.g1)
                .into(markerImage);
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

    public static Bitmap createCustomMarker(Context context, int imgPath, String anchor) {

        View marker = null;
        if(anchor.equals("top"))
            marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_top, null);
        else if(anchor.equals("left"))
            marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_left, null);
        else if(anchor.equals("bottom"))
            marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_bottom, null);
        else
            marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_right, null);

        final CircleImageView markerImage = marker.findViewById(R.id.user_dp);

        Picasso.get()
                .setLoggingEnabled(true);
        Picasso.get()
                .load(imgPath)
                .resize(50,50)
                .error(R.drawable.g1)
                .into(markerImage);
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
        search_radius = prefManager.getSearchRadius();
        max_result = prefManager.getMaxResult();
        Snackbar snackbar = Snackbar.make(findViewById(R.id.map), R.string.wait_message, Snackbar.LENGTH_LONG);
        snackbar.show();
        Call<List<PostResponse>> apiRequest = APIClient.getClient().getPostsRequest(lat, lng, search_radius, max_result );
        apiRequest.enqueue(new Callback<List<PostResponse>>(){

            @Override
            public void onResponse(Call<List<PostResponse>> call, Response<List<PostResponse>> response){
                if(response.isSuccessful()){
                    wpPostList.clear();
                    if(response.body() != null) {
                        wpPostList.addAll(response.body());
                        Log.e("list", wpPostList.toString());
                        setupMarkers();
                        //setupAdapters();
                    }
                    else
                        Toast.makeText(getApplicationContext(),"No items found nearby", Toast.LENGTH_SHORT).show();
                }
                snackbar.dismiss();
            }

            @Override
            public void onFailure(Call<List<PostResponse>> call, Throwable t){

                Toast.makeText(getApplicationContext(),"Some error in webservice call", Toast.LENGTH_SHORT).show();
                snackbar.dismiss();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.setting) {

            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
