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


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    CircleImageView photo_profile;
    TextView tvNameProfile, tvEmailProfile;
    FirebaseAuth firebaseAuthMain;
    FirebaseUser user;
    LinearLayout signOut;

    DatabaseReference profileReference;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_profile, container, false);
        tvEmailProfile = v.findViewById(R.id.tv_emaill_profile);
        tvNameProfile = v.findViewById(R.id.tv_name_profile);
        photo_profile = v.findViewById(R.id.photo_akun_beranda);
        signOut = v.findViewById(R.id.keluar);
        signOut.setOnClickListener(this);

        firebaseAuthMain = FirebaseAuth.getInstance();
        user = firebaseAuthMain.getCurrentUser();

        profileReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        profileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvNameProfile.setText(dataSnapshot.child("nama_lengkap").getValue().toString().trim());
                tvEmailProfile.setText(dataSnapshot.child("email").getValue().toString().trim());
                Glide.with(v.getContext()).load(dataSnapshot.child("url_photo_profile").toString()).into(photo_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.keluar) {
            if (user != null) {
                firebaseAuthMain.signOut();
                updateUI(user);
            }

        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(getContext(), SignInActivity.class));
            Toast.makeText(getContext(), "Thanks for visiting", Toast.LENGTH_LONG).show();
        }
    }
}
