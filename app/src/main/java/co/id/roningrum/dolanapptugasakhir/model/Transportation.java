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

public class Transportation {
    private String name_transportation;
    private String location_transportation;
    private String url_photo_transport;
    private double lat_transportation;
    private double lng_transportation;

    public Transportation() {
    }

    public Transportation(String name_transportation, String location_transportation, String url_photo_transport, double lat_transportation, double lng_transportation) {
        this.name_transportation = name_transportation;
        this.location_transportation = location_transportation;
        this.url_photo_transport = url_photo_transport;
        this.lat_transportation = lat_transportation;
        this.lng_transportation = lng_transportation;
    }

    public String getName_transportation() {
        return name_transportation;
    }

    public void setName_transportation(String name_transportation) {
        this.name_transportation = name_transportation;
    }

    public String getLocation_transportation() {
        return location_transportation;
    }

    public void setLocation_transportation(String location_transportation) {
        this.location_transportation = location_transportation;
    }

    public String getUrl_photo_transport() {
        return url_photo_transport;
    }

    public void setUrl_photo_transport(String url_photo_transport) {
        this.url_photo_transport = url_photo_transport;
    }

    public double getLat_transportation() {
        return lat_transportation;
    }

    public void setLat_transportation(double lat_transportation) {
        this.lat_transportation = lat_transportation;
    }

    public double getLng_transportation() {
        return lng_transportation;
    }

    public void setLng_transportation(double lng_transportation) {
        this.lng_transportation = lng_transportation;
    }
}
