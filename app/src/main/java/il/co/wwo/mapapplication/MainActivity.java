package il.co.wwo.mapapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import il.co.wwo.mapapplication.adapters.LeftAdapter;
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
    private LeftAdapter leftAdapter, rightAdapter;
    private RecyclerView leftRecycler, rightRecycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leftAdapter = new LeftAdapter(this);
        rightAdapter = new LeftAdapter(this);

        leftRecycler = findViewById(R.id.left_list);
        leftRecycler.setLayoutManager(new LinearLayoutManager(this));
        leftRecycler.setAdapter(leftAdapter);

        rightRecycler = findViewById(R.id.right_list);
        rightRecycler.setLayoutManager(new LinearLayoutManager(this));
        rightRecycler.setAdapter(rightAdapter);
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
                Toast.makeText(MainActivity.this,"Current location can't be determined.", Toast.LENGTH_LONG).show();
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

        final LatLng currentLocation = new LatLng(lat, lng);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocation);
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                //Here you can take the snapshot or whatever you want
                setBoundryMarkers();
                loadWpPosts();
            }

            @Override
            public void onCancel() {

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                //mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                //calculateIntersection(currentLocation, marker.getPosition());

               /* PostResponse post = hashMapMarker.get(marker.hashCode());
                if(post != null){
                    Toast.makeText(MainActivity.this,post.getTitle(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
                    intent.putExtra("url", post.getLink());
                    startActivity(intent);
                }*/
                return true;
            }
        });


    }

    private void setBoundryMarkers(){
        double x1,x2,x3,x4, y1, y2, y3, y4, p1, p2;
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        x1 = visibleRegion.farLeft.latitude;
        y1 = visibleRegion.farLeft.longitude;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(x1,y1));
        mMap.addMarker(markerOptions);
        Log.e("m1", x1 + ", " + y1);


        x2 = visibleRegion.farRight.latitude;
        y2 = visibleRegion.farRight.longitude;
        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(new LatLng(x2,y2));
        mMap.addMarker(markerOptions2);
        Log.e("m2", x2 + ", " + y2);

        x3 = visibleRegion.nearRight.latitude;
        y3 = visibleRegion.nearRight.longitude;
        MarkerOptions markerOptions3 = new MarkerOptions();
        markerOptions3.position(new LatLng(x3,y3));
        mMap.addMarker(markerOptions3);
        Log.e("m3", x3 + ", " + y3);


        x4 = visibleRegion.nearLeft.latitude;
        y4 = visibleRegion.nearLeft.longitude;
        MarkerOptions markerOptions4 = new MarkerOptions();
        markerOptions4.position(new LatLng(x4,y4));
        mMap.addMarker(markerOptions4);
        Log.e("m4", x4 + ", " + y4);



    }

    private LatLng calculateIntersection(LatLng currentLocation , LatLng location){
        LatLng temp = null;
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        double x1,x2,x3,x4, y1, y2, y3, y4, p1, p2, nLat, nLng;
        x1 = x2 = x3 = x4 = y1 = y2 = y3 = y4 = p1 = p2 = 0;
        x1 = lat;
        y1 = lng;
        x2 = location.latitude;
        y2 = location.longitude;


        double dir = SphericalUtil.computeHeading(currentLocation,location);// vehicleBearing(currentLocation,location);
        Log.e("radians ", dir +"");
        Log.e("location ", location.latitude +", " + location.longitude);
        LatLng topLeft, topRight, bottomLeft, bottomRight;
        topLeft = visibleRegion.farLeft;
        topRight = visibleRegion.farRight;
        bottomLeft = visibleRegion.nearLeft;
        bottomRight = visibleRegion.nearRight;
        //Log.e("visible ", location.latitude +", " + location.longitude);
        if( ( (dir > 0 && dir <=45) ||  ( dir < 0 && dir  >= -45) )) {

            Log.e("side ", "top");
            nLat = topLeft.latitude;
            nLng = location.longitude;
            if(nLng < topLeft.longitude)
                nLng = topLeft.longitude;
            if( nLng > topRight.longitude )
                nLng = topRight.longitude;
            temp = new LatLng(nLat, nLng);


        }
        if(dir > 45 && dir <= 135) {

            Log.e("side ", "right");
            nLat = location.latitude;
            if(nLat < bottomRight.latitude)
                nLat = bottomRight.latitude;
            if(nLat > topRight.latitude)
                nLat = topRight.latitude;
            nLng = topRight.longitude;
            temp = new LatLng(nLat, nLng);

        }
        if( ( (dir > 135 && dir <= 180) ||  dir < -135 ) ){


            Log.e("side ", "bottom");
            nLat = bottomLeft.latitude;
            nLng = location.longitude;
            if(nLng > bottomRight.longitude)
                nLng = bottomRight.longitude;
            if(nLng < bottomLeft.longitude)
                nLng = bottomLeft.longitude;
            temp = new LatLng(nLat, nLng);

        }
        if(dir < -45 && dir > -135) {

            Log.e("side ", "left");
            nLat = location.latitude;
            if( nLat < bottomLeft.latitude )
                nLat = bottomLeft.latitude;
            if(nLat > topLeft.latitude)
                nLat = topLeft.latitude;
            nLng = bottomLeft.longitude;
            temp = new LatLng(nLat, nLng);
        }




        if(temp != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(temp);
            mMap.addMarker(markerOptions);
        }
        return temp;
    }

    private void setupAdapters(){
        if(wpPostList.size() > 0){
            leftAdapter.setListItems(wpPostList);
            leftAdapter.notifyDataSetChanged();
        }

        if(wpPostList.size() > 0){
            rightAdapter.setListItems(wpPostList);
            rightAdapter.notifyDataSetChanged();
        }
    }


    private void setupMarkers(){
        if(wpPostList != null){
            final LatLngBounds.Builder builder = new LatLngBounds.Builder();
            VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
            markerLayoutList.clear();
            for(final PostResponse post : wpPostList){
                LatLng currentLocation = new LatLng(lat, lng);
                final LatLng location = new LatLng(post.getLatitude(), post.getLongitude());
                if(!visibleRegion.latLngBounds.contains(location))
                    calculateIntersection(currentLocation, location);
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
                        Log.e("list", wpPostList.toString());
                        setupMarkers();
                        //setupAdapters();
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
