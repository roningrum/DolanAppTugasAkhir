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

package co.id.roningrum.dolanapptugasakhir.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by roningrum on 27/06/2019 2019.
 */
public class PermissionUtils {
    Context context;
    Activity myActivity;

    PermissionResultCallback permissionResultCallback;

    ArrayList<String> permission_list = new ArrayList<>();
    ArrayList<String> listPermissionNeeded = new ArrayList<>();

    String dialog_content = "";
    int requestCode;

    public PermissionUtils(Context context) {

        this.context = context;
        this.myActivity = (Activity) context;

        permissionResultCallback = (PermissionResultCallback) context;
    }

    public PermissionUtils(Context context, PermissionResultCallback callback) {

        this.context = context;
        this.myActivity = (Activity) context;

        permissionResultCallback = callback;
    }

    //check API Permission khusus API23

    public void check_permission(ArrayList<String> permissions, String dialog_content, int request_code) {

        this.permission_list = permissions;
        this.dialog_content = dialog_content;
        this.requestCode = request_code;

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkAndRequestPermissions(permissions, request_code)) {

                permissionResultCallback.PermissionGranted(request_code);
                Log.i("permissions", "Acc");
                Log.i("permissions", "sudah diproses");
            }
        } else {
            permissionResultCallback.PermissionGranted(request_code);
            Log.i("permissions", "Acc");
            Log.i("permissions", "sudah diproses");
        }
    }

    //cek dan meminta permissions

    private boolean checkAndRequestPermissions(ArrayList<String> permissions, int request_code) {
        if (permissions.size() > 0) {
            listPermissionNeeded = new ArrayList<>();

            for (int i = 0; i < permissions.size(); i++) {
                int hasPermission = ContextCompat.checkSelfPermission(myActivity, permissions.get(i));

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionNeeded.add(permissions.get(i));
                }
            }
            if (!listPermissionNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(myActivity, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]), request_code);
                return false;
            }
        }
        return true;
    }

    //memasukkan request code agar permission dapat digunakan

    public void onRequestPermissionResults(final int requestCode, String[] permissions, int[] grantResults){

        switch (requestCode){
            case 1:
                if(grantResults.length>0){

                    Map<String, Integer> perms = new HashMap<>();

                    for (int i = 0; i < permissions.length; i++){
                        perms.put(permissions[i], grantResults[i]);
                    }
                    final ArrayList<String> pendingPermissions = new ArrayList<>();

                    for (int i = 0; i < listPermissionNeeded.size(); i++)
                    {
                        if (perms.get(listPermissionNeeded.get(i)) != PackageManager.PERMISSION_GRANTED)
                        {
                            if(ActivityCompat.shouldShowRequestPermissionRationale(myActivity,listPermissionNeeded.get(i)))
                                pendingPermissions.add(listPermissionNeeded.get(i));
                            else
                            {
                                Log.i("Go to settings","and enable permissions");
                                permissionResultCallback.NeverAskAgain(requestCode);
                                Toast.makeText(myActivity, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                    }

                    if(pendingPermissions.size()>0)
                    {
                        showMessageOKCancel(dialog_content,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                check_permission(permission_list,dialog_content,requestCode);
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                Log.i("permisson","not fully given");
                                                if(permission_list.size()==pendingPermissions.size())
                                                    permissionResultCallback.PermissionDenied(requestCode);
                                                else
                                                    permissionResultCallback.PartialPermissionGranted(requestCode,pendingPermissions);
                                                break;
                                        }


                                    }
                                });

                    }
                    else
                    {
                        Log.i("all","permissions granted");
                        Log.i("proceed","to next step");
                        permissionResultCallback.PermissionGranted(requestCode);

                    }



                }
                break;
        }
    }


    /**
     * Explain why the app needs permissions
     *
     * @param message
     * @param okListener
     */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(myActivity)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

}
