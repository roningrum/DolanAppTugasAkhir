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

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import co.id.roningrum.dolanapptugasakhir.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChangePhotoProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnUploadPhoto;
    private Button btnRemovePhoto;
    private Button btnSaveChange;
    private Button btnCancelChange;

    private DatabaseReference dbProfileRef;
    private StorageReference storagePhoto;
    private FirebaseAuth changePhotoAuth;
    private FirebaseUser changePhotoUser;

    private CircleImageView photo_profile;
    boolean isGoogleSignIn;


    private Uri photo_location;
    private Integer photo_max = 1;
    private String TAG = "UPLOAD_PROCESS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chang_photo_profile);
        btnUploadPhoto = findViewById(R.id.btn_upload_image_from_device);
        btnRemovePhoto = findViewById(R.id.btn_remove_image_upload);
        btnSaveChange = findViewById(R.id.btn_save_upload_image_from_device);
        btnCancelChange = findViewById(R.id.btn_cancel_upload);
        photo_profile = findViewById(R.id.photo_akun_beranda);

        btnRemovePhoto.setOnClickListener(this);
        btnUploadPhoto.setOnClickListener(this);
        btnSaveChange.setOnClickListener(this);
        btnCancelChange.setOnClickListener(this);

        changePhotoAuth = FirebaseAuth.getInstance();
        changePhotoUser = changePhotoAuth.getCurrentUser();
        dbProfileRef = FirebaseDatabase.getInstance().getReference("Users");
        storagePhoto = FirebaseStorage.getInstance().getReference("Photo Users");

        DatabaseReference profileReference = FirebaseDatabase.getInstance().getReference().child("Users").child(changePhotoUser.getUid());
        profileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isGoogleSignIn = getIntent().getBooleanExtra("isGoogle", true);
                if (changePhotoUser != null) {
                    Glide.with(getApplicationContext()).load(dataSnapshot.child("photo_user").getValue().toString()).into(photo_profile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload_image_from_device:
                uploadPhotoFromFile();
                break;
            case R.id.btn_remove_image_upload:
                removePhotoProcess();
                break;
            case R.id.btn_save_upload_image_from_device:
                uploadPhotoProcess();
                break;
            case R.id.btn_cancel_upload:
                break;
        }
    }

    private void removePhotoProcess() {
    }

    private void uploadPhotoProcess() {
        if (photo_location != null) {
            btnUploadPhoto.setEnabled(false);
            btnSaveChange.setEnabled(true);
            StorageReference storageReference = storagePhoto.child(System.currentTimeMillis() + "." + getFileExtension(photo_location));
            storageReference.putFile(photo_location).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String uri_photo = uri.toString();
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(uri_photo))
                                    .build();
                            changePhotoUser.updateProfile(profileChangeRequest)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                final DatabaseReference profileDb = dbProfileRef.getRef().child(changePhotoUser.getUid());
                                                profileDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        profileDb.child("photo_user").setValue(uri_photo);
                                                        Log.d(TAG, "Profile Data sukses ke Daftar");
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        Log.e(TAG, "" + databaseError.getMessage());
                                                    }
                                                });

                                            } else {
                                                Log.w(TAG, "Gagal Update");
                                            }
                                        }
                                    });
                        }
                    });
                }
            });
        }
    }

    private void uploadPhotoFromFile() {
        Intent photoIntent = new Intent();
        photoIntent.setType("image/*");
        photoIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(photoIntent, photo_max);

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == photo_max && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photo_location = data.getData();
        }
    }
}
