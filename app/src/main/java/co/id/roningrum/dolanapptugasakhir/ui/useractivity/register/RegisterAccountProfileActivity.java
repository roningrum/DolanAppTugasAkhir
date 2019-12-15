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

package co.id.roningrum.dolanapptugasakhir.ui.useractivity.register;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterAccountProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Status Register";
    private TextInputLayout edtNamaRegister;
    private ImageButton btnUploadImageProfile;
    private CircleImageView profilePhotoImage;

    private DatabaseReference dbRegisterRef;
    private StorageReference photoProfileStore;
    private FirebaseAuth registerProfileAuth;

    private Uri photo_location;
    private Integer photo_max = 1;
    private String nameRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account_name_photo);
        edtNamaRegister = findViewById(R.id.edt_name_register_layout);
        Button btnRegisterProfile = findViewById(R.id.btn_registerfinal_page);
        btnUploadImageProfile = findViewById(R.id.btn_upload_image);
        profilePhotoImage = findViewById(R.id.image_profile_register);

        registerProfileAuth = FirebaseAuth.getInstance();
        dbRegisterRef = FirebaseDatabase.getInstance().getReference();
        photoProfileStore = FirebaseStorage.getInstance().getReference("Photo Users");

        btnUploadImageProfile.setOnClickListener(this);
        btnRegisterProfile.setOnClickListener(this);

        checkAccessFilePermission();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_upload_image:
                uploadPhotoFromFile();
                break;
            case R.id.btn_registerfinal_page:
                registerProfileProcess();
                break;
        }
    }

    private void uploadPhotoFromFile() {
        Intent photoIntent = new Intent();
        photoIntent.setType("image/*");
        photoIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(photoIntent, photo_max);

    }

    private void checkAccessFilePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                btnUploadImageProfile.setEnabled(false);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                btnUploadImageProfile.setEnabled(true);
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == photo_max && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photo_location = data.getData();
            Glide.with(this).load(photo_location).centerCrop().into(profilePhotoImage);
        }
    }

    private void registerProfileProcess() {
        if (photo_location != null) {
            nameRegister = edtNamaRegister.getEditText().getText().toString();
            StorageReference storageReference = photoProfileStore.child(System.currentTimeMillis() + "." + getFileExtension(photo_location));
            storageReference.putFile(photo_location).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = Objects.requireNonNull(taskSnapshot.getMetadata().getReference()).getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String uri_photo = uri.toString();
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nameRegister)
                                    .setPhotoUri(Uri.parse(uri_photo))
                                    .build();
                            final FirebaseUser profileUser = registerProfileAuth.getCurrentUser();
                            assert profileUser != null;
                            profileUser.updateProfile(profileChangeRequest)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                final DatabaseReference profileDb = dbRegisterRef.child("Users").child(profileUser.getUid());
                                                profileDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        profileDb.child("nama_user").setValue(nameRegister);
                                                        profileDb.child("photo_user").setValue(uri_photo);

                                                        Log.d(TAG, "Profile Data sukses ke Daftar");
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        Log.e(TAG, "" + databaseError.getMessage());
                                                    }
                                                });
                                                gotoFinishRegisterPage();

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

    private void gotoFinishRegisterPage() {
        Toast.makeText(RegisterAccountProfileActivity.this, "Pendaftaran Selesai", Toast.LENGTH_SHORT).show();
        Toast.makeText(RegisterAccountProfileActivity.this, "Silakan cek pesan verfikasi email ", Toast.LENGTH_SHORT).show();

        Intent finalRegisterIntent = new Intent(RegisterAccountProfileActivity.this, RegisterSuccessActivity.class);
        startActivity(finalRegisterIntent);
        finish();
    }
}
