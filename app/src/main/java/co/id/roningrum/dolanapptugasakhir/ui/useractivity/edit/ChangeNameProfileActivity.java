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

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;

import static co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant.UserRef;

public class ChangeNameProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UBAH_NAMA";
    private EditText edtChangeName;
    private Toolbar toolbarEdit;

    private FirebaseUser changeNameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name_profile);

        edtChangeName = findViewById(R.id.edt_change_name);
        Button btnSaveChangeName = findViewById(R.id.btn_save_change_name);
        toolbarEdit = findViewById(R.id.toolbar_edit);

        FirebaseAuth changeNameAuth = FirebaseAuth.getInstance();
        changeNameUser = changeNameAuth.getCurrentUser();
        btnSaveChangeName.setOnClickListener(this);
        showNameBeforeChanges();

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


    private void showNameBeforeChanges() {
        if (changeNameUser != null) {
            final String uid = changeNameUser.getUid();
            Log.e("Check Status UID", " The UID : " + uid);
            UserRef.getRef().child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    edtChangeName.setText(Objects.requireNonNull(dataSnapshot.child("nama_user").getValue()).toString().trim());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "" + databaseError.getMessage());
                }
            });
        }
    }

    private void saveNameChange() {
        if (changeNameUser != null) {
            final String uid = changeNameUser.getUid();
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(edtChangeName.getText().toString())
                    .build();
            changeNameUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        UserRef.child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                UserRef.child(uid).getRef().child("nama_user").setValue(edtChangeName.getText().toString().trim());
                                Toast.makeText(getApplicationContext(), "Nama Berhasi di ubah", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "Gagal diubah", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save_change_name) {
            saveNameChange();
        }

    }
}
