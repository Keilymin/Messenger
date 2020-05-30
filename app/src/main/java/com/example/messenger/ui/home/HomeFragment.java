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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.HomeActivity;
import com.example.messenger.MainActivity;
import com.example.messenger.R;
import com.example.messenger.chat.UserAdapter;
import com.example.messenger.chat.UserSearchActivity;
import com.example.messenger.entity.Message;
import com.example.messenger.entity.User;
import com.example.messenger.notif.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;
    private Button newChatButton;
    private View root;
    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> mUsers;
    FirebaseUser user;
    DatabaseReference ref;

    private List<String> userList;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);
        newChatButton = root.findViewById(R.id.newChatButton);
        newChatButton.setOnClickListener(this);

        recyclerView = root.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        user = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference("Chats");
        if(user != null)
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);

                    if(message.getSender().equals(user.getUid())){
                        userList.add(message.getReciever());
                    }
                    if(message.getReciever().equals(user.getUid())){
                        userList.add(message.getSender());
                    }
                }
                Set<String> set = new HashSet<>(userList);
                userList.clear();
                userList.addAll(set);
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());
        return root;
    }
    private void readChats() {
        mUsers = new ArrayList<>();


        ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();


                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    User user=snapshot.getValue(User.class);
                    for(String id:userList){
                        assert user != null;
                        if (user.getId().equals(id)) {
                            mUsers.add(user);
                        }
                    }
                }

                Set<User> set = new HashSet<>(mUsers);
                mUsers.clear();
                mUsers.addAll(set);
                userAdapter = new UserAdapter(getContext(), mUsers,true);
                recyclerView.setAdapter(userAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        if(user != null)
        reference.child(user.getUid()).setValue(token1);
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