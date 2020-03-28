package parimal.examples.beconnectd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class ChatActivity extends AppCompatActivity {

    CircleImageView pro_image;
    TextView userEmail;

    FirebaseUser firebaseUser;
    DatabaseReference dbReference;

    ImageButton buttonSend;
    EditText msg_send;
    ChatAdapter chatAdapter;
    List<Message> messageList;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView=(RecyclerView)findViewById(R.id.msg_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        pro_image=(CircleImageView)findViewById(R.id.prof_image_chat);
        userEmail=(TextView)findViewById(R.id.userEmail_chat);
        buttonSend=(ImageButton)findViewById(R.id.button_msg_send);
        msg_send=(EditText)findViewById(R.id.msg_to_send_edt);

        final String userId=getIntent().getStringExtra("userid");

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=msg_send.getText().toString();
                if(!msg.equals(""))
                {
                    sendMessage(firebaseUser.getUid(),userId,msg);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"You cannot send an empty message",Toast.LENGTH_LONG).show();
                }
                msg_send.setText("");
            }
        });

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
                    Glide.with(ChatActivity.this).load(user.getImageUrl()).into(pro_image);
                }
                read_message(firebaseUser.getUid(),userId,user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String senderId,String receiverId,String message)
    {
        dbReference=FirebaseDatabase.getInstance().getReference();

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("senderId",senderId);
        hashMap.put("receiverId",receiverId);
        hashMap.put("message",message);

        dbReference.child("chats").push().setValue(hashMap);
    }

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
                        Log.d("message", "onDataChange: message,"+message.getMessage());
                    }
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
}
