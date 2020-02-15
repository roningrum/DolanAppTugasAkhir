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
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.model.Users;
import co.id.roningrum.dolanapptugasakhir.ui.useractivity.login.SignInEmailActivity;

import static co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseQuery.UserRef;

public class ChangeEmailProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UPDATE_EMAIL";
    private EditText edtChangeEmail;

    private FirebaseUser changeEmailUser;
    private FirebaseAuth changeEmailAuth;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email_profile);
        edtChangeEmail = findViewById(R.id.edt_change_email);
        toolbar = findViewById(R.id.toolbar_edit);
        Button btnSaveChangeEmail = findViewById(R.id.btn_save_change_email);

        changeEmailAuth = FirebaseAuth.getInstance();
        changeEmailUser = changeEmailAuth.getCurrentUser();

        btnSaveChangeEmail.setOnClickListener(this);
        showEmailBeforeChange();
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

    private void showEmailBeforeChange() {
        if (changeEmailUser != null) {
            final String uid = changeEmailUser.getUid();
            UserRef.getRef().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users users = dataSnapshot.getValue(Users.class);
                    assert users != null;
                    if (users.getEmail().equals(changeEmailUser.getEmail())) {
                        edtChangeEmail.setText(users.getEmail());
                    } else {
                        edtChangeEmail.setText(changeEmailUser.getEmail());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "" + databaseError.getMessage());
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save_change_email) {
            saveEmailChange();
        }

    }

    private void saveEmailChange() {
        final String email = edtChangeEmail.getText().toString().trim();
        if (email.isEmpty()) {
            edtChangeEmail.setError("Email tidak boleh kosong");
        } else if (!isValidEmail(email)) {
            edtChangeEmail.setError("Email tidak valid");
        } else {
            changeEmailConfirm(email);
        }
    }

    private void changeEmailConfirm(final String email) {
        AlertDialog.Builder uploadAlert = new AlertDialog.Builder(ChangeEmailProfileActivity.this);
        uploadAlert.setTitle("Konfirmasi perubahan email");
        uploadAlert.setMessage("Apakah kamu yakin mengubah email?");

        uploadAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                emailResetProcess(email);

            }
        });
        uploadAlert.setNegativeButton("Batalkan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        uploadAlert.show();
    }

    private void emailResetProcess(final String email) {
        final String uid = changeEmailUser.getUid();
        UserRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                if (users != null) {
                    String password = users.getPassword();
                    String emailUser = users.getEmail();
                    AuthCredential credential = EmailAuthProvider.getCredential(emailUser, password);
                    changeEmailUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    changeEmailUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                UserRef.child(uid).getRef().child("email").setValue(email);
                                                changeEmailAuth.signOut();
                                                startActivity(new Intent(ChangeEmailProfileActivity.this, SignInEmailActivity.class));
                                                Toast.makeText(getApplicationContext(), "Silakan ke halaman Login untuk proses masuk", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                                                finish();
                                                Log.e(TAG, "" + task.getException());
                                            }
                                        }
                                    });
                                }
                            });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private boolean isValidEmail(String emailLogin) {
        return Patterns.EMAIL_ADDRESS.matcher(emailLogin).matches();
    }


}
