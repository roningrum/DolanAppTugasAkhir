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

package co.id.roningrum.dolanapptugasakhir.controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.jetbrains.annotations.NotNull;


public class FirebaseConstant {
    public static DatabaseReference TourismRef = FirebaseDatabase.getInstance().getReference().child("Tourism");
    public static DatabaseReference HotelRef = FirebaseDatabase.getInstance().getReference().child("Hotel");
    public static DatabaseReference GasRef = FirebaseDatabase.getInstance().getReference().child("GasStation");
    public static DatabaseReference HospitalRef = FirebaseDatabase.getInstance().getReference().child("Hospital");
    public static DatabaseReference PoliceRef = FirebaseDatabase.getInstance().getReference().child("Police");
    public static DatabaseReference TransportRef = FirebaseDatabase.getInstance().getReference().child("Transportation");


    private static Query query = null;


    //mengambil reference kunci
    @NotNull
    public static DatabaseReference getTransportByKey(String id) {
        return TransportRef.child(id);
    }

    @NotNull
    public static DatabaseReference getGasByKey(String id) {
        return GasRef.child(id);
    }

    @NotNull
    public static DatabaseReference getHospitalByKey(String id) {
        return HospitalRef.child(id);
    }

    @NotNull
    public static DatabaseReference getHotelKey(String id) {
        return HotelRef.child(id);
    }

    @NotNull
    public static DatabaseReference getPoliceKey(String id) {
        return PoliceRef.child(id);
    }

    @NotNull
    public static DatabaseReference getTourismRef(String id) {
        return TourismRef.child(id);
    }

    //Query untuk memanggil data wisata berdasarkan kategori
    public static Query getTourismEducation() {
        query = TourismRef.orderByChild("category_tourism").equalTo("edukasi");
        return query;
    }

    public static Query getTourismSejarah() {
        query = TourismRef.orderByChild("category_tourism").equalTo("sejarah");
        return query;
    }

    public static Query getTourismAlam() {
        query = TourismRef.orderByChild("category_tourism").equalTo("alam");
        return query;
    }

    public static Query getTourismKuliner() {
        query = TourismRef.orderByChild("catgeory_tourism").equalTo("kuliner");
        return query;
    }

    public static Query getTourismRekreasi() {
        query = TourismRef.orderByChild("category_tourism").equalTo("rekreasi");
        return query;
    }

    public static Query getTourismReligi() {
        query = TourismRef.orderByChild("category_tourism").equalTo("religi");
        return query;
    }

    public static Query getTourismBelanja() {
        query = TourismRef.orderByChild("category_tourism").equalTo("belanja");
        return query;
    }

    public static Query getTourismDesa() {
        query = TourismRef.orderByChild("category_tourism").equalTo("desa");
        return query;
    }

    public static Query getTourismAir() {
        query = TourismRef.orderByChild("category_tourism").equalTo("air");
        return query;
    }

    // untuk memanggil data bertipe Query data Hotel, Hospital, Gas dan Police;
    public static Query getHotel() {
        query = HotelRef;
        return query;
    }

    public static Query getGas() {
        query = GasRef;
        return query;
    }

    public static Query getHospital() {
        query = HospitalRef;
        return query;
    }

    public static Query getPolice() {
        query = PoliceRef;
        return query;
    }
    //kumpulan Query utuk memanggil data transportasi berdasarkan kategori transportasi

    public static Query getTransportBus() {
        query = TransportRef.orderByChild("category_transportation").equalTo("bus");
        return query;
    }

    public static Query getTransportPesawat() {
        query = TransportRef.orderByChild("category_transportation").equalTo("airport");
        return query;
    }

    public static Query getTransportKereta() {
        query = TransportRef.orderByChild("category_transportation").equalTo("train");
        return query;
    }

    public static Query getTransportShip() {
        query = TransportRef.orderByChild("category_transportation").equalTo("harbor");
        return query;
    }
}
