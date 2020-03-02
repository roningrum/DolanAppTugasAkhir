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
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;

import static co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseQuery.UserRef;

public class ChangeEmailProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UPDATE_EMAIL";
    private EditText edtChangeEmail;

    private FirebaseUser changeEmailUser;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email_profile);
        edtChangeEmail = findViewById(R.id.edt_change_email);
        toolbar = findViewById(R.id.toolbar_edit);
        Button btnSaveChangeEmail = findViewById(R.id.btn_save_change_email);

        FirebaseAuth changeEmailAuth = FirebaseAuth.getInstance();
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
                    edtChangeEmail.setText(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString().trim());
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
        if (changeEmailUser != null) {
            String uid = changeEmailUser.getUid();
            String email = edtChangeEmail.getText().toString().trim();
            changeEmailConfirm(uid, email);

        }
    }

    private void changeEmailConfirm(final String uid, final String email) {
        AlertDialog.Builder uploadAlert = new AlertDialog.Builder(ChangeEmailProfileActivity.this);
        uploadAlert.setTitle("Konfirmasi perubahan email");
        uploadAlert.setMessage("Apakah kamu yakin mengubah email?");

        uploadAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeEmailUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            UserRef.child(uid).child("email").setValue(email);
                            Toast.makeText(getApplicationContext(), "Email telah berubah", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "" + task.getException());
                        }
                    }
                });
            }
        });
        uploadAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        uploadAlert.show();
    }


}
