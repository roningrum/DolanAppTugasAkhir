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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import co.id.roningrum.dolanapptugasakhir.R;

/**
 * Created by roningrum on 18/06/2019 2019.
 * source code modified from https://gist.github.com/passiondroid/cd2ad32e7ba26f4e47ae
 */

public class LocationPermissionHandler {

    @SuppressLint("StaticFieldLeak")
    private static LocationPermissionHandler locationPermissionHandler;
    private static final int REQUEST_CODE = 1;
    private Activity activity;
    private static final String PERMISSION_ACCESS_FINE_LOCTAION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String PERMISSION_ACCESS_COARSE_LOCTAION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private ArrayList<String> requiredPermissions;
    private ArrayList<String> ungrantedPermissions = new ArrayList<>();

    private LocationPermissionHandler(Activity activity) {
        this.activity = activity;
    }

    public static synchronized LocationPermissionHandler getInstance(Activity activity) {
        if (locationPermissionHandler == null) {
            locationPermissionHandler = new LocationPermissionHandler(activity);
        }
        return locationPermissionHandler;
    }

    private void initPermissions() {
        requiredPermissions = new ArrayList<>();
        requiredPermissions.add(PERMISSION_ACCESS_FINE_LOCTAION);
        requiredPermissions.add(PERMISSION_ACCESS_COARSE_LOCTAION);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void deniedPermission(){
        ungrantedPermissions = getUnGratedPermissionList();
        if(canShowPermissionDialog()){
            showMessageOKCancel(activity.getResources().getString(R.string.permission_message),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            askPermissions();
                        }
                    });
            return;
        }
        askPermissions();
    }

    public void deniedPermission(final String permission){
        ungrantedPermissions = getUnGratedPermissionList();
        if(canShowPermissionDialog(permission)){
            showMessageOKCancel(activity.getResources().getString(R.string.permission_message),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            askPermissions();
                        }
                    });
            return;
        }
        askPermission(permission);
    }

    private void askPermissions() {
        if(ungrantedPermissions.size()>0) {
            ActivityCompat.requestPermissions(activity, ungrantedPermissions.toArray(new String[0]), REQUEST_CODE);
        }
    }
    private void askPermission(String permission) {
        ActivityCompat.requestPermissions(activity, new String[] {permission}, REQUEST_CODE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(activity, R.string.permission_message, Toast.LENGTH_SHORT).show();
                        activity.finish();
                    }
                })
                .create()
                .show();
    }

    private boolean canShowPermissionDialog() {
        boolean shouldshowRationale = false;
        for (String permission : ungrantedPermissions){
            boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
            if(shouldShow){
                shouldshowRationale =true;
            }
        }
        return shouldshowRationale;
    }

    private boolean canShowPermissionDialog(String permission) {
        boolean shouldShowRationale = false;
        boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        if(shouldShow) {
            shouldShowRationale = true;
        }
        return shouldShowRationale;
    }
    private ArrayList<String> getUnGratedPermissionList() {
        ArrayList<String> permissionList = new ArrayList<>();
        for(String permission : requiredPermissions){
            int result = ActivityCompat.checkSelfPermission(activity, permission);
            if(result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(permission);
            }
        }
        return permissionList;
    }

    public boolean isAllPermissionAvailable() {
        boolean isAllPermissionAvailable = true;
        initPermissions();
        for(String permission : requiredPermissions){
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED){
                isAllPermissionAvailable = false;
                break;
            }
        }
        return isAllPermissionAvailable;
    }

}
