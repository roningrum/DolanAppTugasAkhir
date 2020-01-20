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

package co.id.roningrum.dolanapptugasakhir.model;

public class GasStation {
    private String id;
    private String name_gasstation;
    private String location_gasstation;
    private double lat_location_gasstation;
    private double lng_location_gasstation;
    private String url_photo_gasstation;

    public GasStation() {
    }

    public String getId() {
        return id;
    }

    public String getName_gasstation() {
        return name_gasstation;
    }

    public String getLocation_gasstation() {
        return location_gasstation;
    }

    public double getLat_location_gasstation() {
        return lat_location_gasstation;
    }

    public double getLng_location_gasstation() {
        return lng_location_gasstation;
    }

    public String getUrl_photo_gasstation() {
        return url_photo_gasstation;
    }
}
