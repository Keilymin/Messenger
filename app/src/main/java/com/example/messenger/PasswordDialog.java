package com.example.messenger;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class PasswordDialog extends DialogFragment  {
    private FirebaseAuth mAuth;
    private EditText password;
    private EditText newPassword;
    private Boolean b;
    private TextView state;
    FirebaseUser user;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(inflater.inflate(R.layout.dialog_password, null))
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        updatePassword();


                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                            PasswordDialog.this.getDialog().cancel();

                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        password =   getDialog().findViewById(R.id.password);
        newPassword =   getDialog().findViewById(R.id.new_password);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        state = getActivity().findViewById(R.id.state);

        state.setTextColor(Color.RED);
        state.setText("");
    }

    Boolean updatePassword(){

        // Updates the user attributes:

        if(!password.getText().toString().equals("") || !newPassword.getText().toString().equals("")) {
            if(!password.getText().toString().equals(newPassword.getText().toString())) {
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),password.getText().toString());
                user.reauthenticateAndRetrieveData(credential) .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword.getText().toString());
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Users").child(mAuth.getUid());
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id",mAuth.getUid());
                            hashMap.put("email",user.getEmail());
                            hashMap.put("name",user.getDisplayName());
                            hashMap.put("image","def");
                            myRef.setValue(hashMap);
                            state.setText("Пароль сменен");
                            state.setTextColor(Color.GREEN);
                        } else {
                            Log.e(TAG, "Error reauthenticating", task.getException());
                            state.setText("Ошибка пароля");
                        }
                    }
                });


            }
            else {
                state.setText("Passwords Equals");
            }
        }
        else {
            state.setText("Password Empty");
        }
        return b;
    }

}
