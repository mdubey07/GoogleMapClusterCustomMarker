package il.co.wwo.mapapplication.models;

public class WPPost {

    private String id;
    private String title;
    private double latitude;
    private double longitude;
    private String imgPath;
    private String url;
    private double kmaway;

    public double getKmaway() {
        return kmaway;
    }

    public void setKmaway(double kmaway) {
        this.kmaway = kmaway;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
