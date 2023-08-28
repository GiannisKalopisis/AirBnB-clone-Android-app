package com.example.fakebnb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.ChatRecyclerViewInterface;
import com.example.fakebnb.R;
import com.example.fakebnb.model.OverviewChatModel;

import java.util.ArrayList;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder>{

    private ChatRecyclerViewInterface chatRecyclerViewInterface;
    private ArrayList<OverviewChatModel> overviewChatModel;

    public ChatRecyclerAdapter(ChatRecyclerViewInterface chatRecyclerViewInterface, ArrayList<OverviewChatModel> overviewChatModel) {
        this.chatRecyclerViewInterface = chatRecyclerViewInterface;
        this.overviewChatModel = overviewChatModel;
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
    }

    @Override
    public int getItemCount() {
        return overviewChatModel.size();
    }

    public void setChatListModel(ArrayList<OverviewChatModel> overviewChatModel) {
        this.overviewChatModel = overviewChatModel;
        /* In case data come from a server and they change
           you have to refresh them.
         */
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView, lastMessageTextView;

        ViewHolder(@NonNull View itemView, ChatRecyclerViewInterface chatRecyclerViewInterface) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            lastMessageTextView = itemView.findViewById(R.id.lastMessageTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chatRecyclerViewInterface != null) {
                        int clickedPosition = getAdapterPosition();
                        if (clickedPosition != RecyclerView.NO_POSITION) {
                            long chatId = overviewChatModel.get(clickedPosition).getChatId();
                            chatRecyclerViewInterface.onItemClick(clickedPosition);
                        }
                    }
                }
            });
        }
    }
}
