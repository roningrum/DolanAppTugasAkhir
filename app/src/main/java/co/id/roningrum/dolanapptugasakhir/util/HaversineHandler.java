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

public class HaversineHandler {

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

}
