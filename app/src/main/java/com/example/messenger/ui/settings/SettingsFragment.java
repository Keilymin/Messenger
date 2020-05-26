package com.example.messenger.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.messenger.AvatarDialog;
import com.example.messenger.MainActivity;
import com.example.messenger.PasswordDialog;
import com.example.messenger.R;
import com.example.messenger.UsernameDialog;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_settings, container, false);
        ImageButton changeName = root.findViewById(R.id.buttonChangeName);
        changeName.setOnClickListener(this);
        ImageButton changePass = root.findViewById(R.id.buttonChangePass);
        changePass.setOnClickListener(this);
        ImageButton changeAvatar = root.findViewById(R.id.avatar);
        changeAvatar.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonChangeName:
                DialogFragment dialog = new UsernameDialog();
                dialog.show(getFragmentManager(), "dia");
                break;
            case R.id.buttonChangePass:
                DialogFragment dialog1 = new PasswordDialog();
                dialog1.show(getFragmentManager(), "dia");
                break;
            case R.id.avatar:
                DialogFragment dialog2 = new AvatarDialog();
                dialog2.show(getFragmentManager(), "dia");
                break;
        }
    }
}