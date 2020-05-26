package com.example.messenger.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.messenger.MainActivity;
import com.example.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LogOutFragment extends Fragment {
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        setStatus("offline");
        mAuth.getInstance().signOut();

        Intent intent = new Intent(root.getContext(), MainActivity.class);
        startActivity(intent);
        return root;
    }
    private void setStatus(String status){
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users").child(mAuth.getUid());
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("status",status);
            myRef.updateChildren(hashMap);
        }
    }

}