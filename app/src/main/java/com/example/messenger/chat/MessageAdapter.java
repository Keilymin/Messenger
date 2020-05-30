package com.example.messenger.chat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
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

import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
   public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;

    private Context mContext;
    private List<Message> mMessage;
    Message myMessage;
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
        final Message message = mMessage.get(position);
        myMessage = message;
        holder.chatMessage.setText(message.getMessage());
        if(getItemViewType(position) == 1)
        if(message.isIsseen()){

            holder.seen.setImageResource(R.drawable.seen);
        }
        holder.time.setText(message.getDate());

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuItem myActionItem = menu.add(1, v.getId(), 0, "Удалить");
                myActionItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(message.getId());
                            reference.removeValue();

                        return true;
                    }
                });
            }
        });
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
