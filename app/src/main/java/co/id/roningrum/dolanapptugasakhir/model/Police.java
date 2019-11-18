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
    private String location_police;
    private double lat_police;
    private double lng_police;
    private String url_photo_police;


    public Police() {
    }

    public Police(String id, String name_police, String location_police, double lat_police, double lng_police, String url_photo_police) {
        this.id = id;
        this.name_police = name_police;
        this.location_police = location_police;
        this.lat_police = lat_police;
        this.lng_police = lng_police;
        this.url_photo_police = url_photo_police;
    }

    public String getName_police() {
        return name_police;
    }

    public void setName_police(String name_police) {
        this.name_police = name_police;
    }

    public String getLocation_police() {
        return location_police;
    }

    public void setLocation_police(String location_police) {
        this.location_police = location_police;
    }

    public double getLat_police() {
        return lat_police;
    }

    public void setLat_police(double lat_police) {
        this.lat_police = lat_police;
    }

    public double getLng_police() {
        return lng_police;
    }

    public void setLng_police(double lng_police) {
        this.lng_police = lng_police;
    }

    public String getUrl_photo_police() {
        return url_photo_police;
    }

    public void setUrl_photo_police(String url_photo_police) {
        this.url_photo_police = url_photo_police;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
