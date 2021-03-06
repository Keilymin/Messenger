package com.example.messenger;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UsernameDialog extends DialogFragment  {
    private FirebaseAuth mAuth;
    private EditText userName;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(inflater.inflate(R.layout.dialog_username, null))
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        updateUsername();

                        UsernameDialog.this.getDialog().cancel();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(mAuth.getCurrentUser().getDisplayName() == null){
                            Intent intent = new Intent(getActivity(),MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            UsernameDialog.this.getDialog().cancel();
                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        userName =   getDialog().findViewById(R.id.new_password);
        mAuth = FirebaseAuth.getInstance();
    }

    void updateUsername(){
       FirebaseUser user = mAuth.getCurrentUser();
        // Updates the user attributes:
        if(!userName.getText().toString().equals("")) {
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(userName.getText().toString())
                    .build();
            user.updateProfile(profileUpdate);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users").child(mAuth.getUid());
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("name",userName.getText().toString());
            myRef.updateChildren(hashMap);
            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
            View hView =  navigationView.getHeaderView(0);
            TextView textEmail = hView.findViewById(R.id.text_email);
            TextView textLogin = hView.findViewById(R.id.text_login);
            textEmail.setText(user.getEmail());
            textLogin.setText(userName.getText().toString());
        }
        else {
            Toast.makeText(getActivity(), "Login Empty",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
