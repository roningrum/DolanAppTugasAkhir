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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.id.roningrum.dolanapptugasakhir.R;

public class ChangeNameProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UBAH_NAMA";
    private EditText edtChangeName;

    private DatabaseReference dbProfileRef;
    private FirebaseUser changeNameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name_profile);

        edtChangeName = findViewById(R.id.edt_change_name);
        Button btnSaveChangeName = findViewById(R.id.btn_save_change_name);

        FirebaseAuth changeNameAuth = FirebaseAuth.getInstance();
        changeNameUser = changeNameAuth.getCurrentUser();
        dbProfileRef = FirebaseDatabase.getInstance().getReference("Users");

        btnSaveChangeName.setOnClickListener(this);
        showNameBeforeChanges();


    }


    private void showNameBeforeChanges() {
        if (changeNameUser != null) {
            String uid = changeNameUser.getUid();
            dbProfileRef.getRef().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    edtChangeName.setText(dataSnapshot.child("nama_user").getValue().toString().trim());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void showNameAfterChange() {
        if (changeNameUser != null) {
            String uid = changeNameUser.getUid();
            dbProfileRef.child(uid).child("nama_user").setValue(edtChangeName.getText().toString().trim());
            startActivity(new Intent(ChangeNameProfileActivity.this, EditProfileActivity.class));
            finish();

        }

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save_change_name) {
            showNameAfterChange();
        }

    }
}
