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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.ui.useractivity.login.SignInEmailActivity;

import static co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseQuery.UserRef;

public class ChangePasswordProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UpdatePassword";
    private TextInputLayout edtChangePassword;

    private FirebaseAuth changePasswordAuth;
    private FirebaseUser changePasswordUser;

    private Toolbar toolbarEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_profile);
        Button btnSaveChangePassword = findViewById(R.id.btn_save_change_password);
        edtChangePassword = findViewById(R.id.edt_change_pass_layout);
        toolbarEdit = findViewById(R.id.toolbar_edit);

        changePasswordAuth = FirebaseAuth.getInstance();
        changePasswordUser = changePasswordAuth.getCurrentUser();
        btnSaveChangePassword.setOnClickListener(this);

        setToolbar();
    }

    private void setToolbar() {
        setSupportActionBar(toolbarEdit);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save_change_password) {
            saveChangePassword();
        }
    }

    private void saveChangePassword() {
        String password = edtChangePassword.getEditText().getText().toString().trim();
        if (password.isEmpty()) {
            edtChangePassword.setError("Password Harus Di isi");
        } else if (password.length() < 6) {
            edtChangePassword.setError("Password Minimal 6 Karakter");
        } else {
            changePasswordConfirm(password);
        }

    }

    private void changePasswordConfirm(final String password) {
        AlertDialog.Builder uploadAlert = new AlertDialog.Builder(ChangePasswordProfileActivity.this);
        uploadAlert.setTitle("Konfirmasi perubahan password");
        uploadAlert.setMessage("Apakah kamu yakin mengubah password?");

        uploadAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String uid = changePasswordUser.getUid();
                if (changePasswordUser != null) {
                    changePasswordUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                UserRef.getRef().child(uid).child("password").setValue(password);
                                Toast.makeText(getApplicationContext(), "Sukses update \n", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Gagal Update \n" + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        uploadAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        uploadAlert.show();
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), SignInEmailActivity.class));
        finish();
    }
}
