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

package co.id.roningrum.dolanapptugasakhir.ui.useractivity.edit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant.UserRef;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView nameProfile, emailProfile;
    private CircleImageView imageEditprofile;
    private Toolbar toolbarEdit;

    private FirebaseUser editUser;
    private String TAG = "PROFILE_STATUS";

    private LinearLayout changeEmailMenu, changePasswordmenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ImageButton btnStartUploadImage = findViewById(R.id.btn_edit_profile_image);
        LinearLayout changeNameMenu = findViewById(R.id.ln_change_name_menu);
        changeEmailMenu = findViewById(R.id.ln_change_email_menu);
        changePasswordmenu = findViewById(R.id.ln_change_password_menu);
        nameProfile = findViewById(R.id.tv_name_edit_profile);
        emailProfile = findViewById(R.id.tv_email_edit_profile);
        imageEditprofile = findViewById(R.id.image_profile_edit);
        toolbarEdit = findViewById(R.id.toolbar_edit);


        btnStartUploadImage.setOnClickListener(this);
        nameProfile.setOnClickListener(this);
        emailProfile.setOnClickListener(this);
        changeNameMenu.setOnClickListener(this);
        changeEmailMenu.setOnClickListener(this);
        changePasswordmenu.setOnClickListener(this);

        FirebaseAuth editProfileAuth = FirebaseAuth.getInstance();
        editUser = editProfileAuth.getCurrentUser();
        showProfileData();

        setToolbar();
    }

    private void setToolbar() {
        setSupportActionBar(toolbarEdit);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbarEdit.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void showProfileData() {

        assert editUser != null;
        UserRef.child(editUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameProfile.setText(Objects.requireNonNull(dataSnapshot.child("nama_user").getValue()).toString().trim());
                emailProfile.setText(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString().trim());
                Log.w(TAG, "Photo_URL : " + dataSnapshot.child("photo_user").getValue());
                Glide.with(getApplicationContext()).load(Objects.requireNonNull(dataSnapshot.child("photo_user").getValue()).toString()).into(imageEditprofile);
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
                goToEditPhoto();
                break;
            case R.id.ln_change_name_menu:
                goToEditName();
                break;
            case R.id.ln_change_email_menu:
                goToEditEmail();
                break;
            case R.id.ln_change_password_menu:
                goToEditPassword();
                break;

        }
    }

    private void goToEditPhoto() {
        startActivity(new Intent(this, ChangePhotoProfileActivity.class));
    }

    private void goToEditPassword() {
        UserRef.child(editUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("login").getValue().equals("Google")) {
                    changePasswordmenu.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Tidak dapat melakukan perubahan password", Toast.LENGTH_SHORT).show();
                }
                if (dataSnapshot.child("login").getValue().equals("email")) {
                    Intent changePasswordIntent = new Intent(EditProfileActivity.this, ChangePasswordProfileActivity.class);
                    startActivity(changePasswordIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.getMessage();
            }
        });
    }

    private void goToEditEmail() {
        UserRef.child(editUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("login").getValue().equals("Google")) {
                    changeEmailMenu.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Tidak dapat melakukan perubahan email", Toast.LENGTH_SHORT).show();
                }
                if (dataSnapshot.child("login").getValue().equals("email")) {
                    Intent changeEmailIntent = new Intent(EditProfileActivity.this, ChangeEmailProfileActivity.class);
                    startActivity(changeEmailIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.getMessage();
            }
        });

    }

    private void goToEditName() {
        Intent changeNameIntent = new Intent(EditProfileActivity.this, ChangeNameProfileActivity.class);
        startActivity(changeNameIntent);
    }
}
