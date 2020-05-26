package com.example.messenger.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;

    String lastMSG;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isChat){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);

        holder.username.setText(user.getName());
        if(user.getImage().equals("def")){
            holder.image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.getImage()).into(holder.image);
        }

        if(isChat){
            getLastMess(user.getId(),holder.last_mess);
            if(user.getStatus().equals("online")){
                holder.online.setVisibility(View.VISIBLE);
                holder.offline.setVisibility(View.GONE);
            }else {
                holder.online.setVisibility(View.GONE);
                holder.offline.setVisibility(View.VISIBLE);
            }
        } else {
            holder.last_mess.setVisibility(View.GONE);
            holder.online.setVisibility(View.GONE);
            holder.offline.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("userId",user.getId());
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public CircleImageView image;
        private ImageView online;
        private ImageView offline;
        private TextView last_mess;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            image = itemView.findViewById(R.id.image);
            online = itemView.findViewById(R.id.online);
            offline = itemView.findViewById(R.id.offline);
            last_mess = itemView.findViewById(R.id.last_mess);
        }
    }

    private void getLastMess(final String userId, final TextView last_mess){
        lastMSG = "Пусто";

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    if (message.getReciever().equals(fuser.getUid()) && message.getSender().equals(userId)){
                        lastMSG = "Вам: "+message.getMessage();
                    }else if (message.getReciever().equals(userId) && message.getSender().equals(fuser.getUid())){
                        lastMSG = "Вы: "+message.getMessage();
                    }
                }
                last_mess.setText(lastMSG);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
