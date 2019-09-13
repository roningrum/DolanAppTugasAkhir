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

package co.id.roningrum.dolanapptugasakhir.ui.homefragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.ui.useractivity.EditProfileActivity;
import co.id.roningrum.dolanapptugasakhir.ui.useractivity.SignInOptionActivity;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    private CircleImageView photo_profile;
    private TextView tvNameProfile, tvEmailProfile;
    private FirebaseAuth firebaseAuthMain;
    private FirebaseUser user;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvEmailProfile = view.findViewById(R.id.tv_emaill_profile);
        tvNameProfile = view.findViewById(R.id.tv_name_profile);
        photo_profile = view.findViewById(R.id.photo_akun_beranda);
        LinearLayout signOut = view.findViewById(R.id.ln_sign_out);
        LinearLayout editProfile = view.findViewById(R.id.ln_edit_profile);
        LinearLayout aboutApp = view.findViewById(R.id.ln_about_app);
        
        signOut.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        aboutApp.setOnClickListener(this);

        firebaseAuthMain = FirebaseAuth.getInstance();
        user = firebaseAuthMain.getCurrentUser();

        DatabaseReference profileReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        profileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvNameProfile.setText(dataSnapshot.child("nama_user").getValue().toString().trim());
                tvEmailProfile.setText(dataSnapshot.child("email").getValue().toString().trim());
                Glide.with(view).load(dataSnapshot.child("photo_user").getValue().toString()).into(photo_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ln_sign_out:
                firebaseAuthMain.signOut();
                updateUI(user);
                break;
            case R.id.ln_edit_profile:
                goToEditProfile();
                break;
            case R.id.ln_about_app:
                goToAboutPage();
                break;
        }
    }

    private void goToAboutPage() {
    }

    private void goToEditProfile() {
        Intent goToProfileIntent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(goToProfileIntent);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(getContext(), SignInOptionActivity.class));
            Toast.makeText(getContext(), "Thanks for visiting", Toast.LENGTH_LONG).show();
        }
    }
}
