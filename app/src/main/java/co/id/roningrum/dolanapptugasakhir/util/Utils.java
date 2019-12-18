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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import co.id.roningrum.dolanapptugasakhir.R;

public class Utils {
    //    Hitung Jarak
    public static double calculateDistance(double startLat, double startlng, double endlat, double endLng) {

        double earthRadius = 6371;
        double latDiff = Math.toRadians((endlat - startLat));
        double lngDiff = Math.toRadians((endLng - startlng));

        startLat = Math.toRadians(startLat);
        endlat = Math.toRadians(endlat);

        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2);
        double c = Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2) * Math.cos(startLat) * Math.cos(endlat);
        return earthRadius * 2 * Math.asin(Math.sqrt(a + c));
    }

    // ubah ikon pada peta
    public static BitmapDescriptor getBitmapDescriptor(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) context.getDrawable(R.drawable.ic_marker);

            assert vectorDrawable != null;
            int h = vectorDrawable.getIntrinsicHeight();
            int w = vectorDrawable.getIntrinsicWidth();

            vectorDrawable.setBounds(0, 0, w, h);

            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bm);

        } else {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_marker);
        }
    }

    // untuk cek koneksi internet

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected;
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = (activeNetwork != null) && (activeNetwork.isConnected());
            return isConnected;
        } else {
            return true;
        }
    }

}
