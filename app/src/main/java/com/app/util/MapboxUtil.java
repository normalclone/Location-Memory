package com.app.util;

import android.location.Location;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

public class MapboxUtil {
    public static void flyToLocation(double Latitude, double Longtitude, MapboxMap mapboxMap){
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(Latitude,Longtitude)).zoom(13) // Sets the zoom
                .bearing(0) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build();

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 3000);
    }

    public static double calculateBetween2Location(Location l1, Location l2){
        return l1.distanceTo(l2);
    }

}
