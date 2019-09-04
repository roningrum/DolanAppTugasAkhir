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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import co.id.roningrum.dolanapptugasakhir.R;

public class RegisterEmail2 extends AppCompatActivity implements View.OnClickListener {
    Uri photo_location;
    Integer photo_max = 1;
    private ImageView userPhotoRegister;
    private ImageButton btnUserPhotoUpload;
    private Button btnRegisterFinal;
    private EditText edtNameRegister;
    private DatabaseReference registerUser1;
    private StorageReference registerPhotoRef;
    private FirebaseAuth firebaseRegisterfinal;
    private FirebaseUser firebaseUserfinal;

    //    String EMAIL_KEY = "emailKey";
//    String email_key = "";
//    String email_key_new ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account_name_photo);
        btnUserPhotoUpload = findViewById(R.id.btn_uplooad_image);
        btnRegisterFinal = findViewById(R.id.btn_registerfinal_page);
        edtNameRegister = findViewById(R.id.edt_name_register);
        userPhotoRegister = findViewById(R.id.user_image_register);

        btnRegisterFinal.setOnClickListener(this);
        btnUserPhotoUpload.setOnClickListener(this);

        firebaseRegisterfinal = FirebaseAuth.getInstance();
        firebaseUserfinal = firebaseRegisterfinal.getCurrentUser();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_uplooad_image:
                findPhotoUser();
                break;
            case R.id.btn_registerfinal_page:
                userfinalRegister();
                break;
        }
    }

    String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == photo_max && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photo_location = data.getData();
            Glide.with(this).load(photo_location).centerCrop().into(userPhotoRegister);
        }
    }

    private void userfinalRegister() {
        registerPhotoRef = FirebaseStorage.getInstance().getReference().child("PhotoUsers").child(firebaseUserfinal.getUid());
        registerUser1 = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUserfinal.getUid());

        if (photo_location != null) {
            StorageReference storageReference1 = registerPhotoRef.child(System.currentTimeMillis() + "." + getFileExtension(photo_location));
            storageReference1.putFile(photo_location).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String uri_photo = uri.toString();
                            registerUser1.getRef().child("url_photo_profile").setValue(uri_photo);
                        }
                    });
                    registerUser1.getRef().child("nama_lengkap").setValue(edtNameRegister.getText().toString());

                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    //Berpindah Activaty
                    Intent gotoSuccess = new Intent(RegisterEmail2.this, RegisteredemailConfirmActivity.class);
                    startActivity(gotoSuccess);
                    finish();
                }
            });
        }
    }

    private void findPhotoUser() {
        Intent pic = new Intent();
        pic.setType("image/*");
        pic.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pic, photo_max);
    }
}
