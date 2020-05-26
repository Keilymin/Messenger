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

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
   public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;

    private Context mContext;
    private List<Message> mMessage;

    FirebaseUser user;


    public MessageAdapter(Context mContext, List<Message> mMessage){
        this.mMessage = mMessage;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
if(viewType == MSG_RIGHT) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
    return new MessageAdapter.ViewHolder(view);
    } else {
    View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
    return new MessageAdapter.ViewHolder(view);
    }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message message = mMessage.get(position);
        holder.chatMessage.setText(message.getMessage());
        if(getItemViewType(position) == 1)
        if(message.isIsseen()){

            holder.seen.setImageResource(R.drawable.seen);
        }
        holder.time.setText(message.getDate());
    }


    @Override
    public int getItemCount() {

        return mMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView chatMessage;
        public TextView time;
        public  ImageView seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            chatMessage = itemView.findViewById(R.id.message);
            seen = itemView.findViewById(R.id.noseen);
            time = itemView.findViewById(R.id.time);
        }
    }

    @Override
    public int getItemViewType(int position) {
       user = FirebaseAuth.getInstance().getCurrentUser();
       if(mMessage.get(position).getSender().equals(user.getUid())){
           return MSG_RIGHT;
       } else {
           return MSG_LEFT;
       }
    }
}
