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
    private String id;
    private String name_transport;
    private String location_transport;
    private String url_photo_transport;
    private double lat_location_transport;
    private double lng_location_transport;

    public Transportation() {
    }

    public Transportation(String id, String name_transport, String location_transport, String url_photo_transport, double lat_location_transport, double lng_location_transport) {
        this.id = id;
        this.name_transport = name_transport;
        this.location_transport = location_transport;
        this.url_photo_transport = url_photo_transport;
        this.lat_location_transport = lat_location_transport;
        this.lng_location_transport = lng_location_transport;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName_transport() {
        return name_transport;
    }

    public void setName_transport(String name_transport) {
        this.name_transport = name_transport;
    }

    public String getLocation_transport() {
        return location_transport;
    }

    public void setLocation_transport(String location_transport) {
        this.location_transport = location_transport;
    }

    public String getUrl_photo_transport() {
        return url_photo_transport;
    }

    public void setUrl_photo_transport(String url_photo_transport) {
        this.url_photo_transport = url_photo_transport;
    }

    public double getLat_location_transport() {
        return lat_location_transport;
    }

    public void setLat_location_transport(double lat_location_transport) {
        this.lat_location_transport = lat_location_transport;
    }

    public double getLng_location_transport() {
        return lng_location_transport;
    }

    public void setLng_location_transport(double lng_location_transport) {
        this.lng_location_transport = lng_location_transport;
    }
}
