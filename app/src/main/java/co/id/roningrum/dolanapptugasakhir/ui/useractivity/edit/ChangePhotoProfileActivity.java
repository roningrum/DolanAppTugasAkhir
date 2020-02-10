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

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.model.Users;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChangePhotoProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference dbProfileRef;
    private StorageReference storagePhoto;
    private FirebaseUser changePhotoUser;
    private ProgressBar pbLoading;
    private Toolbar toolbar;

    private CircleImageView photo_profile;


    private Uri photo_location;
    private Integer photo_max = 1;
    private String TAG = "UPLOAD_PROCESS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_photo_profile);
        ImageButton btnUploadPhoto = findViewById(R.id.btn_upload_image_from_device);
        pbLoading = findViewById(R.id.pb_loading_upload);
        photo_profile = findViewById(R.id.photo_akun_beranda);
        toolbar = findViewById(R.id.toolbar_edit);

        btnUploadPhoto.setOnClickListener(this);

        FirebaseAuth changePhotoAuth = FirebaseAuth.getInstance();
        changePhotoUser = changePhotoAuth.getCurrentUser();
        dbProfileRef = FirebaseDatabase.getInstance().getReference("Users");
        storagePhoto = FirebaseStorage.getInstance().getReference("Photo Users");

        DatabaseReference profileReference = FirebaseDatabase.getInstance().getReference().child("Users").child(changePhotoUser.getUid());
        profileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                if (users != null) {
                    if (!users.getPhoto_user().equals("")) {
                        Glide.with(getApplicationContext()).load(users.getPhoto_user()).into(photo_profile);
                    } else {
                        photo_profile.setImageResource(R.drawable.icon_nopic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, " " + databaseError.getMessage());
            }
        });

        setToolbar();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_upload_image_from_device) {
            uploadPhotoConfirm();
        }
    }

    private void uploadPhotoConfirm() {
        AlertDialog.Builder uploadAlert = new AlertDialog.Builder(ChangePhotoProfileActivity.this);
        uploadAlert.setTitle("Unggah Foto");
        uploadAlert.setMessage("Apakah kamu mau mengunggah foto?");

        uploadAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pbLoading.setVisibility(View.VISIBLE);
                uploadPhotoFromFile();
            }
        });
        uploadAlert.setNegativeButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePhoto();
            }
        });
        uploadAlert.show();
    }

    private void deletePhoto() {
        final DatabaseReference profileDb = dbProfileRef.getRef().child(changePhotoUser.getUid());
        profileDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                if (users != null) {

                    if (!users.getPhoto_user().equals("")) {
                        storagePhoto.getStorage().getReferenceFromUrl(users.getPhoto_user()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                profileDb.child("photo_user").setValue("");
                                pbLoading.setVisibility(View.GONE);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Gagal Menghapus", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Tidak perlu. Upload dulu", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pbLoading.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "" + databaseError.getMessage());
            }
        });

    }

    String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadPhotoFromFile() {
        Intent photoIntent = new Intent();
        photoIntent.setType("image/*");
        photoIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(photoIntent, photo_max);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == photo_max && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photo_location = data.getData();
            pbLoading.setVisibility(View.VISIBLE);
            uploadPhotoProcessConfirm();
        }
    }


    private void uploadPhotoProcessConfirm() {
        AlertDialog.Builder uploadAlert = new AlertDialog.Builder(ChangePhotoProfileActivity.this);
        uploadAlert.setTitle("Proses Unggah Foto");
        uploadAlert.setMessage("Apakah kamu yakin akan memproses unggahan?");

        uploadAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pbLoading.setVisibility(View.VISIBLE);
                uploadPhotoProcess();
            }
        });
        uploadAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Toast.makeText(getApplicationContext(), "Pressed Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        uploadAlert.show();
    }

    private void uploadPhotoProcess() {

        if (photo_location != null) {
            StorageReference storageReference = storagePhoto.child(System.currentTimeMillis() + "." + getFileExtension(photo_location));
            storageReference.putFile(photo_location).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl();
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
                                                        Users users = dataSnapshot.getValue(Users.class);
                                                        profileDb.child("photo_user").setValue(uri_photo);
                                                        pbLoading.setVisibility(View.GONE);
                                                        Glide.with(ChangePhotoProfileActivity.this).load(photo_location).into(photo_profile);
                                                        Toast.makeText(getApplicationContext(), "Sukses Terubah", Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, "Profile Data sukses ke Daftar");
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        pbLoading.setVisibility(View.VISIBLE);
                                                        Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
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


}
