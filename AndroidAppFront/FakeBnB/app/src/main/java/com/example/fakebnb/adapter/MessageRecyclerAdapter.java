package com.example.fakebnb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.R;
import com.example.fakebnb.model.MessageModel;

import java.util.ArrayList;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.ViewHolder> {

    private String senderUsername;
    private String receiverUsername;
    private ArrayList<MessageModel> messageModel;

    public MessageRecyclerAdapter(String senderUsername,
                                  String receiverUsername) {
        this.messageModel = new ArrayList<>();
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_recycler_view, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageRecyclerAdapter.ViewHolder holder, int position) {
        if (messageModel.get(position).getUsername().equals(senderUsername)) {
            holder.leftMessageLayout.setVisibility(View.GONE);
            holder.rightMessageLayout.setVisibility(View.VISIBLE);
            holder.rightMessageTextView.setText(messageModel.get(position).getContent());
        } else {
            holder.leftMessageLayout.setVisibility(View.VISIBLE);
            holder.rightMessageLayout.setVisibility(View.GONE);
            holder.leftMessageTextView.setText(messageModel.get(position).getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messageModel.size();
    }
    
    public void setMessageListModel(ArrayList<MessageModel> messageModel) {
        this.messageModel.addAll(0, messageModel);
        /* In case data come from a server and they change
           you have to refresh them.
         */
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftMessageLayout, rightMessageLayout;
        TextView leftMessageTextView, rightMessageTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            leftMessageLayout = itemView.findViewById(R.id.left_chat_layout);
            rightMessageLayout = itemView.findViewById(R.id.right_chat_layout);
            leftMessageTextView = itemView.findViewById(R.id.left_chat_textview);
            rightMessageTextView = itemView.findViewById(R.id.right_chat_textview);
        }
    }
}
