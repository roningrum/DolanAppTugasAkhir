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

public class Hotel {
    private String id;
    private String name_hotel;
    private String info_hotel;
    private String location_hotel;
    private String telepon;
    private String url_photo_hotel;
    private String order_link_hotel;
    private String order_link_hotel1;
    private double lat_location_hotel;
    private double lng_location_hotel;

    public Hotel() {
    }

    public Hotel(String id, String name_hotel, String info_hotel, String location_hotel, String telepon, String url_photo_hotel, String order_link_hotel, String order_link_hotel1, double lat_location_hotel, double lng_location_hotel) {
        this.id = id;
        this.name_hotel = name_hotel;
        this.info_hotel = info_hotel;
        this.location_hotel = location_hotel;
        this.telepon = telepon;
        this.url_photo_hotel = url_photo_hotel;
        this.order_link_hotel = order_link_hotel;
        this.order_link_hotel1 = order_link_hotel1;
        this.lat_location_hotel = lat_location_hotel;
        this.lng_location_hotel = lng_location_hotel;
    }

    public String getId() {
        return id;
    }

    public String getName_hotel() {
        return name_hotel;
    }

    public String getLocation_hotel() {
        return location_hotel;
    }

    public String getTelepon() {
        return telepon;
    }

    public String getInfo_hotel() {
        return info_hotel;
    }

    public String getUrl_photo_hotel() {
        return url_photo_hotel;
    }

    public String getOrder_link_hotel() {
        return order_link_hotel;
    }

    public String getOrder_link_hotel1() {
        return order_link_hotel1;
    }

    public double getLat_location_hotel() {
        return lat_location_hotel;
    }

    public double getLng_location_hotel() {
        return lng_location_hotel;
    }
}
