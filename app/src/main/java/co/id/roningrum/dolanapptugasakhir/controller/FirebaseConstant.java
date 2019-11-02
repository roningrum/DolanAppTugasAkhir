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


public class FirebaseConstant {
    public static DatabaseReference TourismRef = FirebaseDatabase.getInstance().getReference().child("Tourism");
    private static DatabaseReference HotelRef = FirebaseDatabase.getInstance().getReference().child("Hotel");
    private static Query query = null;

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
        query = TourismRef.orderByChild("catgeory_tourism").equalTo("desa");
        return query;
    }


}
