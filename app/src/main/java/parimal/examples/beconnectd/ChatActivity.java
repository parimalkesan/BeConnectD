package parimal.examples.beconnectd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import model.Message;
import model.User;

//to display messages between users
public class ChatActivity extends AppCompatActivity {

    CircleImageView pro_image;
    TextView userEmail;

    FirebaseUser firebaseUser;
    DatabaseReference dbReference;

    //button to send a message
    ImageButton buttonSend;
    //editText to type a message
    EditText msg_send;
    ChatAdapter chatAdapter;
    //list to store messages between users
    List<Message> messageList;

    RecyclerView recyclerView;

    //to check whether user has seen a message
    ValueEventListener seenLis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get toolbar to display other user's email and profile image
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            //go back to parent activity
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        //get recycler view to display messages and display messages from bottom to top fashion.
        recyclerView=(RecyclerView)findViewById(R.id.msg_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //get references to views
        pro_image=(CircleImageView)findViewById(R.id.prof_image_chat);
        userEmail=(TextView)findViewById(R.id.userEmail_chat);
        buttonSend=(ImageButton)findViewById(R.id.button_msg_send);
        msg_send=(EditText)findViewById(R.id.msg_to_send_edt);

        //get other user's id from calling intent
        final String userId=getIntent().getStringExtra("userid");

        //add listener to send button
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=msg_send.getText().toString();
                if(!msg.equals(""))
                {
                    //call sendMessage()
                    sendMessage(firebaseUser.getUid(),userId,msg);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"You cannot send an empty message",Toast.LENGTH_LONG).show();
                }
                msg_send.setText("");
            }
        });

        //get database reference to chiled node of other user and set its email and profile image
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        dbReference= FirebaseDatabase.getInstance().getReference("Users").child(userId);

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                userEmail.setText(user.getEmail());
                if("default".equals(user.getImageUrl()))
                {
                    pro_image.setImageResource(R.mipmap.ic_launcher);
                }
                else
                {
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(pro_image);
                }
                //call read_message()
                read_message(firebaseUser.getUid(),userId,user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //call seenMessage()
        seenMessage(userId);
    }

    //to check if a message has been seen by other user
    private void seenMessage(final String userId)
    {
        dbReference=FirebaseDatabase.getInstance().getReference("chats");
        seenLis=dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    //get messages from data snapshot
                    Message message=dataSnapshot1.getValue(Message.class);
                    //to check if a message has been seen by current user and change isSeen to "true"
                    if(message.getReceiverId().equals(firebaseUser.getUid())&&message.getSenderId().equals(userId))
                    {
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isSeen","true");
                        dataSnapshot1.getRef().updateChildren(hashMap);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //send a message from current user to other user
    private void sendMessage(String senderId,String receiverId,String message)
    {
        dbReference=FirebaseDatabase.getInstance().getReference();

        String timestamp=String.valueOf(System.currentTimeMillis());

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("senderId",senderId);
        hashMap.put("receiverId",receiverId);
        hashMap.put("message",message);
        hashMap.put("isSeen","false");
        hashMap.put("timestamp",timestamp);

        dbReference.child("chats").push().setValue(hashMap);
    }

    //read messages from database and store them in messageList.
    private void read_message(final String selfId, final String userId, final String imageUrl)
    {
        messageList=new ArrayList<>();

        dbReference=FirebaseDatabase.getInstance().getReference("chats");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    Message message = dataSnapshot1.getValue(Message.class);
                    if (selfId.equals(message.getSenderId()) && userId.equals(message.getReceiverId())
                            || userId.equals(message.getSenderId()) && selfId.equals(message.getReceiverId())) {
                        messageList.add(message);
                    }
                    //create an adapter from recycler view and set recycler view to display from last message when opened
                    chatAdapter=new ChatAdapter(ChatActivity.this,messageList,imageUrl);
                    recyclerView.setAdapter(chatAdapter);
                    recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    //remove seenLis valueeventlistener
    protected void onPause() {
        super.onPause();
        dbReference.removeEventListener(seenLis);
    }
}
