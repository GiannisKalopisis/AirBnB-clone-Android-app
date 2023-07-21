package com.example.fakebnb;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.model.ChatMessageModel;
import com.example.fakebnb.model.UserModel;
import com.example.fakebnb.utils.AndroidUtil;

import java.security.Timestamp;
import java.time.LocalTime;

public class IndividualChatPage extends AppCompatActivity {

    private static final String TAG = "IndividualChatPage";

    private UserModel otherUser;
    private EditText chat_message_input;
    private ImageButton message_send_btn, back_btn;
    private TextView other_username;
    private RecyclerView chat_recycler_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat);

        // get UserModel from intent
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());

        initView();
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        other_username.setText(otherUser.getUsername());

        message_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = chat_message_input.getText().toString().trim();
                if (message.isEmpty()) {
                    return;
                }
                sendMessageToUser(message);
            }
        });

        getOrCreateChatroomModel();
    }

    private void sendMessageToUser(String message) {
        ChatMessageModel chatMessageModel = new ChatMessageModel(message, otherUser.getUsername(), LocalTime.now());
        // send data to database. In case of complete set text to ""
//        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
//                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                        if(task.isSuccessful()){
//                            messageInput.setText("");
//                            sendNotification(message);
//                        }
//                    }
//                });
    }

    private void getOrCreateChatroomModel() {

    }

    private void initView() {
        chat_message_input = findViewById(R.id.chat_message_input);
        message_send_btn = findViewById(R.id.message_send_btn);
        back_btn = findViewById(R.id.back_btn);
        other_username = findViewById(R.id.other_username);
        chat_recycler_view = findViewById(R.id.chat_recycler_view);
    }
}
