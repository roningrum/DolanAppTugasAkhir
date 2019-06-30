/*
 * Copyright 2019 RONINGRUM. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.id.roningrum.dolanapptugasakhir.handler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by roningrum on 24/06/2019 2019.
 */
public class GPSHandler extends Service implements LocationListener {

    private Context context;
    private Activity activity;
    boolean isGPSEnabled = false;

    FusedLocationProviderClient fusedLocationProviderClient;
    SettingsClient settingsClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    LocationSettingsRequest locationSettingsRequest;

    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;

    protected LocationManager locationManager;
    private static final int PERMISSIONS_REQUEST_LOCATION = 99;
    private static final long MIN_DISTANCE_CHANGE_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;

    public GPSHandler() {
    }

    public GPSHandler(Context context) {
        this.context = context;
        getLocation();
    }

    protected void requpdateLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private Location getLocation() {

        if (checkLocationPermission()) {
            try {

                locationManager = (LocationManager) context
                        .getSystemService(LOCATION_SERVICE);
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    Log.d("Jaringan", "Tidak ada koneksi");
                    return null;
                } else {
                    requpdateLocation();
                    this.canGetLocation = true;
                    if (isNetworkEnabled && isGPSEnabled) {
                        locationManager
                                .requestLocationUpdates(
                                        LocationManager.NETWORK_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(
                                            LocationManager.NETWORK_PROVIDER);
//                            location = locationManager
//                                    .getLastKnownLocation(
//                                            LocationManager.GPS_PROVIDER);
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                    }
                                }
                            });
                        }


                    }
//                    if (isGPSEnabled) {
//                        locationManager
//                                .requestLocationUpdates(
//                                        LocationManager.NETWORK_PROVIDER,
//                                        MIN_TIME_BW_UPDATES,
//                                        MIN_DISTANCE_CHANGE_UPDATES, this);
//                        if (locationManager != null) {
//                            location = locationManager
//                                    .getLastKnownLocation(
//                                            LocationManager.GPS_PROVIDER);
//                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//                            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//                                @Override
//                                public void onSuccess(Location location) {
//                                    if (location != null) {
//                                        latitude = location.getLatitude();
//                                        longitude = location.getLongitude();
//                                    }
//                                }
//                            });
//                        }
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        return location;
    }

    private boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }
        return true;
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSHandler.this);
        }
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public boolean isCanGetLocation() {
        return this.canGetLocation;
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Working", "Service started");
        getLocation();
        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
