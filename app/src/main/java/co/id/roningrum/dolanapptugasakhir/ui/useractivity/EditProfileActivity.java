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

package co.id.roningrum.dolanapptugasakhir.ui.useractivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.UploadDialogFragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView nameProfile, emailProfile;
    private CircleImageView imageEditprofile;

    private DatabaseReference dbProfileReferece;
    private FirebaseAuth editProfileAuth;
    private String TAG = "PROFILE_STATUS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ImageButton btnStartUploadImage = findViewById(R.id.btn_edit_profile_image);
        LinearLayout changeNameMenu = findViewById(R.id.ln_change_name_menu);
        LinearLayout changeEmailMenu = findViewById(R.id.ln_change_email_menu);
        LinearLayout changePasswordmenu = findViewById(R.id.ln_change_password_menu);
        nameProfile = findViewById(R.id.tv_name_edit_profile);
        emailProfile = findViewById(R.id.tv_email_edit_profile);
        imageEditprofile = findViewById(R.id.image_profile_edit);

        btnStartUploadImage.setOnClickListener(this);
        nameProfile.setOnClickListener(this);
        emailProfile.setOnClickListener(this);
        changeNameMenu.setOnClickListener(this);
//        changeEmailMenu.setOnClickListener(this);
//        changePasswordmenu.setOnClickListener(this);

        dbProfileReferece = FirebaseDatabase.getInstance().getReference();
        editProfileAuth = FirebaseAuth.getInstance();
        showProfileData();
    }

    private void showProfileData() {
        FirebaseUser editUser = editProfileAuth.getCurrentUser();
        dbProfileReferece.child("Users").child(editUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameProfile.setText(dataSnapshot.child("nama_user").getValue().toString().trim());
                emailProfile.setText(dataSnapshot.child("email").getValue().toString().trim());
                Glide.with(EditProfileActivity.this).load(dataSnapshot.child("photo_user").getValue().toString()).into(imageEditprofile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "" + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit_profile_image:
                UploadDialogFragment uploadDialogFragment = new UploadDialogFragment();
                uploadDialogFragment.show(getSupportFragmentManager(), uploadDialogFragment.getTag());
                break;
            case R.id.ln_change_name_menu:
                Intent changeNameIntent = new Intent(EditProfileActivity.this, ChangeNameProfileActivity.class);
                startActivity(changeNameIntent);
                break;
        }
    }
}
