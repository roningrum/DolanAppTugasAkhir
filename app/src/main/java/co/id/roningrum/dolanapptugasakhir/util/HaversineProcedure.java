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

/**
 * Created by roningrum on 26/06/2019 2019.
 */
public class HaversineProcedure {
    private static final int EARTH_RADIUS = 6371;

    public static double distance(double startLat, double startLng, double endLat, double endLng){

        double earthRadius = 6371;
        int meterConversion = 1609;
        double latDiff = Math.toRadians(startLat-startLng);
        double lngDiff = Math.toRadians(endLat-endLng);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(startLat)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c * meterConversion;

        return Float.valueOf((float) Math.floor(distance)/1000).floatValue();

    }
//    public static double haversin(double var){
//        return ;
//    }
}
