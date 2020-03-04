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


public class Tourism {
    private String id;
    private String name_tourism;
    private String location_tourism;
    private String info_tourism;
    private String telepon;
    private String url_photo_tourism;
    private double lat_location_tourism;
    private double lng_location_tourism;

    public Tourism() {
        //constructor untuk panggilan ke DataSnapshot.getValue
    }

    public Tourism(String id, String name_tourism, String location_tourism, String info_tourism, String telepon, String url_photo_tourism, double lat_location_tourism, double lng_location_tourism) {
        this.id = id;
        this.name_tourism = name_tourism;
        this.location_tourism = location_tourism;
        this.info_tourism = info_tourism;
        this.telepon = telepon;
        this.url_photo_tourism = url_photo_tourism;
        this.lat_location_tourism = lat_location_tourism;
        this.lng_location_tourism = lng_location_tourism;
    }

    public String getId() {
        return id;
    }

    public String getName_tourism() {
        return name_tourism;
    }

    public String getLocation_tourism() {
        return location_tourism;
    }

    public String getInfo_tourism() {
        return info_tourism;
    }

    public String getTelepon() {
        return telepon;
    }

    public String getUrl_photo_tourism() {
        return url_photo_tourism;
    }

    public double getLat_location_tourism() {
        return lat_location_tourism;
    }

    public double getLng_location_tourism() {
        return lng_location_tourism;
    }
}
