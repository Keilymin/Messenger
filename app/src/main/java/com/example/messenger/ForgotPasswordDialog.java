package com.example.messenger;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ForgotPasswordDialog extends DialogFragment {
    private FirebaseAuth mAuth;
    private EditText email;
    private TextView state;
    FirebaseUser user;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(inflater.inflate(R.layout.dialog_forgot_pass, null))
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    String e = email.getText().toString();
                    Log.e("E",e+"FFF");
                    if(e.equals("")){
                        state.setText("Пустое поле");
                    } else {

                        mAuth.sendPasswordResetEmail(e).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    state.setText("Пожалуйста проверьте свою почту");
                                    state.setTextColor(Color.GREEN);

                                }else {
                                    String err = task.getException().getMessage();
                                    state.setText(err);
                                }
                            }
                        });
                    }



                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        ForgotPasswordDialog.this.getDialog().cancel();

                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        state = getActivity().findViewById(R.id.state);
        state.setTextColor(Color.RED);
        state.setText("");
        email =   getDialog().findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

    }


}