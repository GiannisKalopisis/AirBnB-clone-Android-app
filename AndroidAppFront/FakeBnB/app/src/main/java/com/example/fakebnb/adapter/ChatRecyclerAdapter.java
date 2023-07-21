//package com.example.fakebnb.adapter;
//
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.fakebnb.R;
//import com.example.fakebnb.model.ChatMessageModel;
//
//public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder>{
//
//    Context context;
//
//    public ChatRecyclerAdapter(Context context) {
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public ChatMessageModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ChatMessageModel holder, int position, @NonNull ChatMessageModel model) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }
//
//
//    class ChatModelViewHolder extends RecyclerView.ViewHolder{
//
//        LinearLayout leftChatLayout,rightChatLayout;
//        TextView leftChatTextview,rightChatTextview;
//
//        public ChatModelViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
//            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
//            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
//            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
//        }
//    }
//}
