package com.example.messenger.chat;

import android.annotation.SuppressLint;
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
import com.example.messenger.notif.APIService;
import com.example.messenger.notif.Client;
import com.example.messenger.notif.Data;
import com.example.messenger.notif.MyResponse;
import com.example.messenger.notif.Sender;
import com.example.messenger.notif.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    ImageButton send;
    EditText message;
    CircleImageView avatar;
    TextView nickname;
    FirebaseUser mUser;
    DatabaseReference ref;
    FirebaseAuth mAuth;
    MessageAdapter messageAdapter;
    List<Message> mMessage;
    RecyclerView list;
    Intent intent;
    ValueEventListener seenListener;
    String id;
    APIService apiService;
    String stat;
    boolean notify = false;
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
                ref.removeEventListener(seenListener);
                finish();
            }
        });
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
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
        id = intent.getStringExtra("userId");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String mess = message.getText().toString();
                if(!mess.equals("")){
                    sendMessage(mUser.getUid(),id,mess);
                } else {
                    Toast.makeText(ChatActivity.this,"Пустое сообщение",Toast.LENGTH_SHORT).show();
                }
                message.setText("");
            }
        });
        DatabaseReference rf = FirebaseDatabase.getInstance().getReference("Users").child(id).child("status");
        rf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 stat = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                    Glide.with(getApplicationContext()).load(user.getImage()).into(avatar);
                }

                readMessage(mUser.getUid(),id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void seenMessage(final String userid){
        ref = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    if(message.getReciever().equals(mUser.getUid()) && message.getSender().equals(userid)){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String reciever, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("reciever",reciever);
        hashMap.put("message",message);
        hashMap.put("isseen",false);
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        hashMap.put("date", timeStamp);

        reference.child("Chats").push().setValue(hashMap);

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (notify && stat.equals("offline")) {
                    sendNotifiaction(reciever, user.getName(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendNotifiaction(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(mUser.getUid(), R.mipmap.ic_launcher, username+": "+message, "Новое сообщение",
                            id);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void  readMessage(final String myId, final String userId){
        mMessage = new ArrayList<>();
        DatabaseReference re = FirebaseDatabase.getInstance().getReference("Chats");
        ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessage.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);

                    if(message.getReciever().equals(myId) && message.getSender().equals(userId)
                            || message.getReciever().equals(userId) && message.getSender().equals(myId)){
                        message.setId(snapshot.getKey());
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

    @Override
    protected void onResume() {
        super.onResume();

            seenMessage(id);

        setStatus("online");


    }

    @Override
    protected void onPause() {
        if (seenListener != null && ref!=null) {
            ref.removeEventListener(seenListener);
        }
        super.onPause();
        setStatus("offline");
    }
}
