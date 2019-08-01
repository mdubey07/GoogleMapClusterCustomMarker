package il.co.wwo.mapapplication.models;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class PostResponse{

	@SerializedName("_longitude")
	private String itmLongitude;

	@SerializedName("id")
	private String id;

	@SerializedName("distance")
	private double distance;

	@SerializedName("latitude")
	private double latitude;

	@SerializedName("link")
	private String link;

	@SerializedName("_latitude")
	private String itmLatitude;

	@SerializedName("title")
	private String title;

	@SerializedName("thumbnail")
	private String thumbnail;

	@SerializedName("longitude")
	private double longitude;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getItmLongitude() {
		return itmLongitude;
	}

	public void setItmLongitude(String itmLongitude) {
		this.itmLongitude = itmLongitude;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getItmLatitude() {
		return itmLatitude;
	}

	public void setItmLatitude(String itmLatitude) {
		this.itmLatitude = itmLatitude;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}