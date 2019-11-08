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

package co.id.roningrum.dolanapptugasakhir;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class OptionalOrderHotelFragment extends DialogFragment {

    public OptionalOrderHotelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_optional_order_hotel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout btnOrderHotel = view.findViewById(R.id.btn_order_link_hotel);
        LinearLayout btnOrderHotel1 = view.findViewById(R.id.btn_order_link_hotel1);
    }


//    private void getOrderKamarHotel1() {
//        final Query orderHotelLinkQuery = hotelDetailRef.orderByChild("order_link_hotel1");
//        orderHotelLinkQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Hotel hotel = dataSnapshot.getValue(Hotel.class);
//                assert hotel != null;
//                String orderLink = hotel.getOrder_link_hotel1();
//
//                if (orderLink.equals("tidak tersedia")) {
//                    Toast.makeText(HotelDetail.this, "Maaf Link Tidak tersedia", Toast.LENGTH_SHORT).show();
//                } else {
//                    Intent linkOrderNow = new Intent(Intent.ACTION_VIEW, Uri.parse(orderLink));
//                    startActivity(linkOrderNow);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e(TAG, "" + databaseError.getMessage());
//            }
//        });
//
//    }
//
//    private void getOrderKamarHotel() {
//        final Query orderHotelLinkQuery = hotelDetailRef.orderByChild("order_link_hotel");
//        orderHotelLinkQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Hotel hotel = dataSnapshot.getValue(Hotel.class);
//                assert hotel != null;
//                String orderLink = hotel.getOrder_link_hotel();
//
//                if (orderLink.equals("tidak tersedia")) {
//                    Toast.makeText(HotelDetail.this, "Maaf Link Tidak tersedia", Toast.LENGTH_SHORT).show();
//                } else {
//                    Intent linkOrderNow = new Intent(Intent.ACTION_VIEW, Uri.parse(orderLink));
//                    startActivity(linkOrderNow);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e(TAG, "" + databaseError.getMessage());
//            }
//        });
//    }
}
