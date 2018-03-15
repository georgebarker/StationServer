package model;

/**
 * I am a POJO that represents a train station.
 */
public class Station {
	private String stationName;
	private LatLng latLng;
	
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public LatLng getLatLng() {
		return latLng;
	}
	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}
	
	
}
