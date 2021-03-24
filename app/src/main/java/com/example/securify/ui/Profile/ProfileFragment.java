package com.example.securify.ui.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.securify.model.User;
import com.example.securify.ui.LoginActivity;
import com.example.securify.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private Button signOutButton;
    private GoogleSignInClient mGoogleSignInClient;

    private static final String TAG = "ProfileFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        signOutButton = root.findViewById(R.id.sign_out_button);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestId()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        TextView profileName = root.findViewById(R.id.profile_name);
        profileName.setText(User.getInstance().getName());

        TextView profileEmail = root.findViewById(R.id.profile_email);
        profileEmail.setText(User.getInstance().getEmail());

        CircleImageView profilePicture = root.findViewById(R.id.profile_picture);
        String profileUri = User.getInstance().getProfilePicture();

        if(profileUri.equals("")) {
            profilePicture.setImageResource(R.drawable.ic_profile);
        } else {
            Glide.with(getContext()).load(User.getInstance().getProfilePicture()).into(profilePicture);
        }


        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignOut();
                revokeAccess();

                Toast.makeText(getActivity(), "GoodBye.", Toast.LENGTH_LONG).show();

                getActivity().finishAffinity();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        return root;
    }

    private void googleSignOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "User is signed out of google.");
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "User is revoked");
                    }
                });
    }

}
