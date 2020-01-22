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

public class Police {
    private String id;
    private String name_police;
    private String telepon;
    private String location_police;
    private double lat_location_police;
    private double lng_location_police;
    private String url_photo_police;


    public Police() {
    }

    public Police(String id, String name_police, String telepon, String location_police, double lat_location_police, double lng_location_police, String url_photo_police) {
        this.id = id;
        this.name_police = name_police;
        this.telepon = telepon;
        this.location_police = location_police;
        this.lat_location_police = lat_location_police;
        this.lng_location_police = lng_location_police;
        this.url_photo_police = url_photo_police;
    }

    public String getId() {
        return id;
    }

    public String getName_police() {
        return name_police;
    }

    public String getLocation_police() {
        return location_police;
    }

    public double getLat_location_police() {
        return lat_location_police;
    }

    public double getLng_location_police() {
        return lng_location_police;
    }

    public String getUrl_photo_police() {
        return url_photo_police;
    }

    public String getTelepon() {
        return telepon;
    }
}
