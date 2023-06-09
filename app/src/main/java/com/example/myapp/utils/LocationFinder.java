package com.example.myapp.utils;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class LocationFinder implements LocationListener {
    LocationManager locationManager;
    Context context;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean valid = false;
    Location location = null;
//    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
//    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 second

    public LocationFinder(Context context) {
        this.context = context;
        updateLocation();
    }

    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("Location", "Updated " + location);
        this.location = location;
    }

    public void updateLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Log.e("Location", "No provider available");
            } else {
                checkPermission();
                this.valid = true;
                if (isNetworkEnabled) {
                    Log.d("Location", "Network Available");
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
//                    locationManager.requestLocationUpdates(
//                            LocationManager.NETWORK_PROVIDER,
//                            MIN_TIME_BW_UPDATES,
//                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }
                if (isGPSEnabled) {
                    Log.d("Location", "GPS Available");
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
//                    locationManager.requestLocationUpdates(
//                            LocationManager.GPS_PROVIDER,
//                            MIN_TIME_BW_UPDATES,
//                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Location getLocation() {
        checkPermission();
        return location;
    }

    public String getLocationMsg() {
        Location loc = getLocation();
        if (loc != null) {
            return String.format("(%f, %f, %f)",
                    loc.getLatitude(), loc.getLongitude(), loc.getAltitude());
        }
        return null;
    }

    public boolean isValid() {
        return this.valid && this.location != null;
    }
}
