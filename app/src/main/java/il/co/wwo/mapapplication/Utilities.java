package il.co.wwo.mapapplication;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.SphericalUtil;

public class Utilities {
    class MarkerAnchor{
        float X, Y;
        MarkerAnchor(float x, float y){
            this.X = x;
            this.Y = y;
        }
    }
    public final MarkerAnchor ANCHOR_TOP = new MarkerAnchor(0.5f, 0f);
    public final MarkerAnchor ANCHOR_TOP_LEFT = new MarkerAnchor(0f, 0f);
    public final MarkerAnchor ANCHOR_TOP_RIGHT = new MarkerAnchor(1f, 0f);
    public final MarkerAnchor ANCHOR_RIGHT = new MarkerAnchor(1f, 0.5f);
    public final MarkerAnchor ANCHOR_BOTTOM = new MarkerAnchor(0.5f, 0.5f);
    public final MarkerAnchor ANCHOR_BOTTOM_LEFT = new MarkerAnchor(0f, 1f);
    public final MarkerAnchor ANCHOR_BOTTOM_RIGHT = new MarkerAnchor(1f, 1f);
    public final MarkerAnchor ANCHOR_LEFT = new MarkerAnchor(0f, 0.5f);

    public void repositionMarkers(LatLng currentLocation , LatLng location, GoogleMap mMap){
        LatLng temp = null;
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        double nLat, nLng;

        MarkerAnchor markerAnchor = new MarkerAnchor(0f,0f);
        double dir = SphericalUtil.computeHeading(currentLocation,location);// vehicleBearing(currentLocation,location);

        LatLng topLeft, topRight, bottomLeft, bottomRight;
        topLeft = visibleRegion.farLeft;
        topRight = visibleRegion.farRight;
        bottomLeft = visibleRegion.nearLeft;
        bottomRight = visibleRegion.nearRight;
        String anchor = "bottom";

        if( ( (dir > 0 && dir <=45) ||  ( dir < 0 && dir  >= -45) )) {

            markerAnchor = ANCHOR_TOP;
            anchor = "top";
            nLat = topLeft.latitude;
            nLng = location.longitude;
            if(nLng < topLeft.longitude) {
                nLng = topLeft.longitude;
                markerAnchor = ANCHOR_TOP_LEFT;
            }
            if( nLng > topRight.longitude ) {
                nLng = topRight.longitude;
                markerAnchor = ANCHOR_TOP_RIGHT;
            }
            temp = new LatLng(nLat, nLng);


        }
        if(dir > 45 && dir <= 135) {

            markerAnchor = ANCHOR_RIGHT;
            anchor = "right";
            nLat = location.latitude;
            if(nLat < bottomRight.latitude) {
                nLat = bottomRight.latitude;
                markerAnchor = ANCHOR_BOTTOM_RIGHT;
            }
            if(nLat > topRight.latitude) {
                nLat = topRight.latitude;
                markerAnchor = ANCHOR_TOP_RIGHT;
            }
            nLng = topRight.longitude;
            temp = new LatLng(nLat, nLng);

        }
        if( ( (dir > 135 && dir <= 180) ||  dir < -135 ) ){

            // Log.e("side ", "bottom");
            anchor = "bottom";
            markerAnchor = ANCHOR_BOTTOM;
            nLat = bottomLeft.latitude;
            nLng = location.longitude;
            if(nLng > bottomRight.longitude) {
                nLng = bottomRight.longitude;
                markerAnchor = ANCHOR_BOTTOM_RIGHT;
            }
            if(nLng < bottomLeft.longitude) {
                nLng = bottomLeft.longitude;
                markerAnchor = ANCHOR_BOTTOM_LEFT;
            }
            temp = new LatLng(nLat, nLng);

        }
        if(dir < -45 && dir > -135) {

            //Log.e("side ", "left");
            anchor = "left";
            markerAnchor = ANCHOR_LEFT;
            nLat = location.latitude;
            if( nLat < bottomLeft.latitude ) {
                nLat = bottomLeft.latitude;
                markerAnchor = ANCHOR_BOTTOM_LEFT;
            }
            if(nLat > topLeft.latitude) {
                nLat = topLeft.latitude;
                markerAnchor = ANCHOR_TOP_LEFT;
            }
            nLng = bottomLeft.longitude;
            temp = new LatLng(nLat, nLng);
        }

        final LatLng _location = temp;
        final String _anchor = anchor;
        final MarkerAnchor _markerAnchor = markerAnchor;


    }
}
