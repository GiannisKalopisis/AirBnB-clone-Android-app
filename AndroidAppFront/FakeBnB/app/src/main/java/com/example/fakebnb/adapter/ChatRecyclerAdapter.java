package com.example.fakebnb.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.ChatRecyclerViewInterface;
import com.example.fakebnb.R;
import com.example.fakebnb.model.OverviewChatModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder>{

    private ChatRecyclerViewInterface chatRecyclerViewInterface;
    private ArrayList<OverviewChatModel> overviewChatModel;
    private Map<Long, Bitmap> usersImages;     // <chatId, userImage>

    public ChatRecyclerAdapter(ChatRecyclerViewInterface chatRecyclerViewInterface, ArrayList<OverviewChatModel> overviewChatModel) {
        this.chatRecyclerViewInterface = chatRecyclerViewInterface;
        this.overviewChatModel = overviewChatModel;
        this.usersImages = new HashMap<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user_recycler_view, parent, false);
        ChatRecyclerAdapter.ViewHolder holder = new ChatRecyclerAdapter.ViewHolder(view, chatRecyclerViewInterface);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerAdapter.ViewHolder holder, int position) {
        holder.usernameTextView.setText(overviewChatModel.get(position).getUsername());
        holder.lastMessageTextView.setText(overviewChatModel.get(position).getContentOfLastMessage());
        if (overviewChatModel.get(position).getSeen()) {
            holder.lastMessageTextView.setTextColor(holder.itemView.getResources().getColor(R.color.grey));
        } else {
            holder.lastMessageTextView.setTextColor(holder.itemView.getResources().getColor(R.color.black));
        }
        holder.userImageView.setImageBitmap(usersImages.get(overviewChatModel.get(position).getChatId()));
    }

    @Override
    public int getItemCount() {
        return overviewChatModel.size();
    }

    public void setChatListModel(ArrayList<OverviewChatModel> overviewChatModel) {
        this.overviewChatModel.addAll(overviewChatModel);
        /* In case data come from a server and they change
           you have to refresh them.
         */
        notifyDataSetChanged();
    }

    public void setUserImage(Long chatId, Bitmap userImage) {
        usersImages.put(chatId, userImage);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView, lastMessageTextView;
        ImageView userImageView;

        ViewHolder(@NonNull View itemView, ChatRecyclerViewInterface chatRecyclerViewInterface) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            lastMessageTextView = itemView.findViewById(R.id.lastMessageTextView);
            userImageView = itemView.findViewById(R.id.chat_user_profile_pic_image_view);

            itemView.setOnClickListener(v -> {
                if (chatRecyclerViewInterface != null) {
                    int clickedPosition = getAdapterPosition();
                    if (clickedPosition != RecyclerView.NO_POSITION) {
                        long chatId = overviewChatModel.get(clickedPosition).getChatId();
                        Bitmap userImage = usersImages.get(chatId);
                        chatRecyclerViewInterface.onItemClick(chatId);
                    }
                }
            });
        }
    }
}
