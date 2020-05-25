package com.example.messenger.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messenger.R;
import com.example.messenger.entity.Message;
import com.example.messenger.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    ImageButton send;
    EditText message;
    ImageView avatar;
    TextView nickname;
    FirebaseUser mUser;
    DatabaseReference ref;

    MessageAdapter messageAdapter;
    List<Message> mMessage;
    RecyclerView list;
    Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list = findViewById(R.id.list);
        list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        list.setLayoutManager(linearLayoutManager);

        avatar = findViewById(R.id.avatar);
        nickname = findViewById(R.id.nickname);
        send = findViewById(R.id.send);
        message = findViewById(R.id.message);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        intent = getIntent();
        final String id = intent.getStringExtra("userId");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = message.getText().toString();
                if(!mess.equals("")){
                    sendMessage(mUser.getUid(),id,mess);
                } else {
                    Toast.makeText(ChatActivity.this,"Пустое сообщение",Toast.LENGTH_SHORT).show();
                }
                message.setText("");
            }
        });

        ref = FirebaseDatabase.getInstance().getReference("Users").child(id);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nickname.setText(user.getName());
                if(user.getImage().equals("def")){
                    avatar.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(ChatActivity.this).load(user.getImage()).into(avatar);
                }

                readMessage(mUser.getUid(),id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, String reciever,String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("reciever",reciever);
        hashMap.put("message",message);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void  readMessage(final String myId, final String userId){
        mMessage = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessage.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);

                    if(message.getReciever().equals(myId) && message.getSender().equals(userId)
                            || message.getReciever().equals(userId) && message.getSender().equals(myId)){
                        mMessage.add(message);
                    }
                    messageAdapter = new MessageAdapter(ChatActivity.this,mMessage);

                    list.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
