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

package co.id.roningrum.dolanapptugasakhir.item;

public class HospitalItem {
    private String name_hospital;
    private String location_hospital;
    private double lat_hospital;
    private double lng_hospital;
    private String url_photo_hospital;

    public HospitalItem() {
    }

    public HospitalItem(String name_hospital, String location_hospital, double lat_hospital, double lng_hospital, String url_photo_hospital) {
        this.name_hospital = name_hospital;
        this.location_hospital = location_hospital;
        this.lat_hospital = lat_hospital;
        this.lng_hospital = lng_hospital;
        this.url_photo_hospital = url_photo_hospital;
    }

    public String getUrl_photo_hospital() {
        return url_photo_hospital;
    }

    public void setUrl_photo_hospital(String url_photo_hospital) {
        this.url_photo_hospital = url_photo_hospital;
    }

    public String getName_hospital() {
        return name_hospital;
    }

    public void setName_hospital(String name_hospital) {
        this.name_hospital = name_hospital;
    }

    public String getLocation_hospital() {
        return location_hospital;
    }

    public void setLocation_hospital(String location_hospital) {
        this.location_hospital = location_hospital;
    }

    public double getLat_hospital() {
        return lat_hospital;
    }

    public void setLat_hospital(double lat_hospital) {
        this.lat_hospital = lat_hospital;
    }

    public double getLng_hospital() {
        return lng_hospital;
    }

    public void setLng_hospital(double lng_hospital) {
        this.lng_hospital = lng_hospital;
    }
}
