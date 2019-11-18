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
    private double lat_gasstation;
    private double lng_gasstation;
    private String url_photo_gasstation;

    public GasStation() {
    }

    public GasStation(String id, String name_gasstation, String location_gasstation, double lat_gasstation, double lng_gasstation, String url_photo_gasstation) {
        this.id = id;
        this.name_gasstation = name_gasstation;
        this.location_gasstation = location_gasstation;
        this.lat_gasstation = lat_gasstation;
        this.lng_gasstation = lng_gasstation;
        this.url_photo_gasstation = url_photo_gasstation;
    }

    public String getUrl_photo_gasstation() {
        return url_photo_gasstation;
    }

    public void setUrl_photo_gasstation(String url_photo_gasstation) {
        this.url_photo_gasstation = url_photo_gasstation;
    }

    public String getName_gasstation() {
        return name_gasstation;
    }

    public void setName_gasstation(String name_gasstation) {
        this.name_gasstation = name_gasstation;
    }

    public String getLocation_gasstation() {
        return location_gasstation;
    }

    public void setLocation_gasstation(String location_gasstation) {
        this.location_gasstation = location_gasstation;
    }

    public double getLat_gasstation() {
        return lat_gasstation;
    }

    public void setLat_gasstation(double lat_gasstation) {
        this.lat_gasstation = lat_gasstation;
    }

    public double getLng_gasstation() {
        return lng_gasstation;
    }

    public void setLng_gasstation(double lng_gasstation) {
        this.lng_gasstation = lng_gasstation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
