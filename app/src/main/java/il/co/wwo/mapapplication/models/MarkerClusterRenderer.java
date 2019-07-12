package il.co.wwo.mapapplication.models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import de.hdodenhof.circleimageview.CircleImageView;
import il.co.wwo.mapapplication.MapsActivity;
import il.co.wwo.mapapplication.R;

public class MarkerClusterRenderer extends DefaultClusterRenderer<MyItem> {   // 1

    private static final int MARKER_DIMENSION = 120;  // 2

    private final IconGenerator iconGenerator;
    private final ImageView markerImageView;
    private Context mContext;


    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        iconGenerator = new IconGenerator(context);  // 3
        markerImageView = new ImageView(context);
        markerImageView.setLayoutParams(new ViewGroup.LayoutParams(MARKER_DIMENSION, MARKER_DIMENSION));
        iconGenerator.setContentView(markerImageView);  // 4
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) { // 5
//        markerImageView.setImageResource(R.drawable.right_marker);  // 6
//        Bitmap icon = iconGenerator.makeIcon();  // 7
        double roundOff = Math.round(item.getKmaway() * 100.0) / 100.0;
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(mContext, R.drawable.g1, item.getTitle(), roundOff + " KM")));  // 8
        markerOptions.title(item.getTitle());

//        mMap.addMarker(markerOptions.position(customMarkerLocation).
//                icon(BitmapDescriptorFactory.fromBitmap(
//                        createCustomMarker(MapsActivity.this, R.drawable.g1, post.getTitle(), roundOff + " KM"))))
//                .setTitle(post.getTitle());
//        Marker m = mMap.addMarker(markerOptions);
//        m.showInfoWindow();
//
//        builder.include(customMarkerLocation);


    }

    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name, String km) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        CircleImageView markerImage = marker.findViewById(R.id.user_dp);
        markerImage.setImageResource(resource);
        TextView txt_name = marker.findViewById(R.id.name);
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
}
