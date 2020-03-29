package parimal.examples.beconnectd;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
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

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import model.Message;
import model.User;

import static androidx.constraintlayout.widget.Constraints.TAG;

//adapter class to recyclerview in UserFragment and ChatFragment
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
     private Context context;
     private List<User> userList;
     public boolean ischat;
     private String lastMessage;
     public static String tstamp;

     public UserAdapter(Context context,List<User> users,boolean ischat)
     {
         this.context=context;
         this.userList=users;
         this.ischat=ischat;
     }

    @NonNull
    @Override
    //to provide layout for each view in recyclerview
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view= LayoutInflater.from(context).inflate(R.layout.user_data_list,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    //to set userEmail_tv and user_prof_image for each view in recyclerview
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         final User user=userList.get(position);
         holder.userEmail_tv.setText(user.getEmail());
         if("default".equals(user.getImageUrl()))
         {
             holder.user_prof_image.setImageResource(R.mipmap.ic_launcher);
         }
         else
         {
             Glide.with(context).load(user.getImageUrl()).into(holder.user_prof_image);
         }

         //display last message sent or received for each user
         if(ischat)
         {
             last_Message(user.getUserId(),holder.lastMsgTv,holder.user_timestamp);
         }
         else
         {
             holder.lastMsgTv.setVisibility(View.GONE);
         }

         //check whether the user is online or offline(if the user had a chat
        //with current user
         if(ischat)
         {
             //if user is online
             if("online".equals(user.getStatus()))
             {
                 holder.userStatus_on.setVisibility(View.VISIBLE);
                 holder.userStatus_off.setVisibility(View.INVISIBLE);
             }
             //if user is offline
             else
             {
                 holder.userStatus_off.setVisibility(View.VISIBLE);
                 holder.userStatus_on.setVisibility(View.INVISIBLE);
             }
         }
         //if there is no chat between both
         else
         {
             holder.userStatus_off.setVisibility(View.GONE);
             holder.userStatus_on.setVisibility(View.GONE);
         }

         //set onClickListener on each view in recyclerview to go to ChatActivity class
         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent=new Intent(context,ChatActivity.class);
                 intent.putExtra("userid",user.getUserId());
                 context.startActivity(intent);
             }
         });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    //Viewholder to provide a holder for each view in recyclerview
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView userEmail_tv;
        public ImageView user_prof_image;
        private ImageView userStatus_on;
        private ImageView userStatus_off;
        private TextView lastMsgTv;
        private  TextView user_timestamp;
        public ViewHolder(View view)
        {
            super(view);
            userEmail_tv=view.findViewById(R.id.userEmail_tv);
            user_prof_image=view.findViewById(R.id.user_prof_image);
            userStatus_on=view.findViewById(R.id.img_status_on);
            userStatus_off=view.findViewById(R.id.img_status_off);
            lastMsgTv=view.findViewById(R.id.last_msg_tv);
            user_timestamp=view.findViewById(R.id.user_timestamp);
        }
    }

    //check for last message sent or received for a user and its timestamp
    private void last_Message(final String userId, final TextView lastMsg,final TextView user_timestamp)
    {
        lastMessage="default";
        tstamp="";
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("chats");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    //check for msgs between current user and other user
                    Message message=dataSnapshot1.getValue(Message.class);
                    if(firebaseUser.getUid().equals(message.getSenderId()) && userId.equals(message.getReceiverId())
                            || userId.equals(message.getSenderId()) && firebaseUser.getUid().equals(message.getReceiverId()))
                    {
                        //set last message
                        lastMessage=message.getMessage();

                        //get timestamp of last message
                        String timestamp=message.getTimestamp();
                        Calendar cal= Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(Long.parseLong(timestamp));
                        tstamp= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
                    }
                }
                //set lst message for each user
                switch (lastMessage)
                {
                    case "default":lastMsg.setText("No Message");
                                    break;
                    default:lastMsg.setText(lastMessage);
                                     break;
                }
                //set the timestamp for each user
                switch (tstamp)
                {
                    case "":user_timestamp.setText("");
                        break;
                    default:user_timestamp.setText(tstamp);
                        break;
                }
                lastMessage="default";
                tstamp="";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
