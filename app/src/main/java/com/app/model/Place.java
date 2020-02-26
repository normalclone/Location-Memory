package com.app.model;

public class Place {
    private String place_id;
    private String name;
    private double longitude;
    private double latitude;
    private String icon;
    private String address;
    private double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Place(String place_id, String name, double longitude, double latitude, String icon, String address, double distance) {
        this.place_id = place_id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.icon = icon;
        this.address = address;
        this.distance = distance;
    }

    public Place() {
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Place{" +
                "place_id='" + place_id + '\'' +
                ", name='" + name + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", icon='" + icon + '\'' +
                ", address='" + address + '\'' +
                ", distance=" + distance +
                '}';
    }
}
