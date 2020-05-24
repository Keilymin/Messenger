package com.example.messenger.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.messenger.HomeActivity;
import com.example.messenger.MainActivity;
import com.example.messenger.R;
import com.example.messenger.chat.UserSearchActivity;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;
    private Button newChatButton;
    private View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);
        newChatButton = root.findViewById(R.id.newChatButton);
        newChatButton.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newChatButton:
                Intent intent = new Intent(root.getContext(), UserSearchActivity.class);
                startActivity(intent);
                break;
        }
    }
}