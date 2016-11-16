package com.iiitd.team10.nearyou;

public class PlaceItem implements java.io.Serializable{

    private String type;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private int distance;
    private String distance_Text;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getDistance_Text() {
        return distance_Text;
    }

    public void setDistance_Text(String distance_Text) {
        this.distance_Text = distance_Text;
    }

    public PlaceItem(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
