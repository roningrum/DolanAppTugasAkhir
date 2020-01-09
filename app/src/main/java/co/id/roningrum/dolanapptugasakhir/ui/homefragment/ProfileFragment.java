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

package co.id.roningrum.dolanapptugasakhir.ui.homefragment;


import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.ui.useractivity.edit.EditProfileActivity;
import co.id.roningrum.dolanapptugasakhir.ui.useractivity.login.SignInOptionActivity;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 123;
    private CircleImageView photo_profile;
    private TextView tvNameProfile, tvEmailProfile;
    private FirebaseAuth firebaseAuthMain;
    private DatabaseReference profileReference;
    private FirebaseUser user;

    private static final String TAG = "Pesan";

    private GoogleApiClient mGoogleApiClient;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }



    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvEmailProfile = view.findViewById(R.id.tv_emaill_profile);
        tvNameProfile = view.findViewById(R.id.tv_name_profile);
        photo_profile = view.findViewById(R.id.photo_akun_beranda);
        LinearLayout signOut = view.findViewById(R.id.ln_sign_out);
        LinearLayout editProfile = view.findViewById(R.id.ln_edit_profile);
        LinearLayout aboutApp = view.findViewById(R.id.ln_about_app);


        editProfile.setOnClickListener(this);
        aboutApp.setOnClickListener(this);

        firebaseAuthMain = FirebaseAuth.getInstance();
        user = firebaseAuthMain.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(requireContext())
                .enableAutoManage(requireActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        signOut.setOnClickListener(this);

        profileReference = FirebaseConstant.UserRef;


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ln_sign_out:
                firebaseAuthMain.signOut();
                updateUI(user);
                break;
            case R.id.ln_edit_profile:
                goToEditProfile();
                break;
            case R.id.ln_about_app:
                goToAboutPage();
                break;
        }
    }

    private void goToAboutPage() {
    }

    private void goToEditProfile() {
        Intent goToProfileIntent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(goToProfileIntent);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            firebaseAuthMain.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    Intent intent = new Intent(getActivity(), SignInOptionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            });
            Toast.makeText(getContext(), "Thanks for visiting", Toast.LENGTH_LONG).show();
        } else {
            Log.e("Status", "Gagal Keluar");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "message" + connectionResult.getErrorMessage());
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), getActivity(), 0).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (getActivity() != null) {
                mGoogleApiClient.stopAutoManage(getActivity());
                mGoogleApiClient.disconnect();
            }

        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        profileReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    tvNameProfile.setText(Objects.requireNonNull(dataSnapshot.child("nama_user").getValue()).toString().trim());
                    tvEmailProfile.setText(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString().trim());
                    String photo_url = Objects.requireNonNull(dataSnapshot.child("photo_user").getValue()).toString();
                    assert getActivity() != null;
                    Glide.with(getActivity()).load(photo_url).into(photo_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Gagal Load" + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.w(TAG, "Destroy");
    }
}
