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
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.ui.useractivity.login.SignInOptionActivity;

public class ChangeEmailProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UPDATE_EMAIL";
    private EditText edtChangeEmail;

    private DatabaseReference dbProfileRef;
    private FirebaseUser changeEmailUser;
    private FirebaseAuth changeEmailAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email_profile);
        edtChangeEmail = findViewById(R.id.edt_change_email);
        Button btnSaveChangeEmail = findViewById(R.id.btn_save_change_email);

        changeEmailAuth = FirebaseAuth.getInstance();
        changeEmailUser = changeEmailAuth.getCurrentUser();
        dbProfileRef = FirebaseDatabase.getInstance().getReference("Users");

        btnSaveChangeEmail.setOnClickListener(this);
        showEmailBeforeChange();

    }

    private void showEmailBeforeChange() {
        if (changeEmailUser != null) {
            final String uid = changeEmailUser.getUid();
            dbProfileRef.getRef().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
            final String uid = changeEmailUser.getUid();
            final String email = edtChangeEmail.getText().toString().trim();
            changeEmailUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        dbProfileRef.child(uid).child("email").setValue(email);
                        changeEmailAuth.signOut();
                        startActivity(new Intent(ChangeEmailProfileActivity.this, SignInOptionActivity.class));
                        finish();
                    } else {
                        Log.e(TAG, "" + task.getException());
                    }
                }
            });
        }
    }
}
