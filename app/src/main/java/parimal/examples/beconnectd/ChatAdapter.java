package parimal.examples.beconnectd;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import model.Message;

//adapter for recyclerview in ChatActivity class
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context context;
    //list to store messages between current user and an other user
    private List<Message> messagesList;
    private String imageUrl;

    public static final int MESSAGE_LEFT = 0;
    public static final int MESSAGE_RIGHT = 1;

    FirebaseUser firebaseUser;

    public ChatAdapter(Context context, List<Message> messagesList, String imageUrl) {
        this.context = context;
        this.messagesList = messagesList;
        this.imageUrl = imageUrl;
    }

    //check whether message is send by current user or by other user and set layout for both differently
    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //if message is from current user,set layout on right
        if(viewType==MESSAGE_RIGHT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.messages_right, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
        else
        {
            //set layout on left if message is from other user
            View view = LayoutInflater.from(context).inflate(R.layout.messages_left, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
    }

    //set prof_image and user_message for each view in recyclerView
    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        final Message message = messagesList.get(position);
        holder.user_message.setText(message.getMessage());
        if ("default".equals(imageUrl)) {
            holder.prof_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(imageUrl).into(holder.prof_image);
        }
        //set whether the message has been seen or not for last message
        if(position==messagesList.size()-1)
        {
            //if message is seen,set txtseen as "seen"
            if("true".equals(message.getIsSeen()))
            {
                holder.txtSeen.setText("Seen");
            }
            else
            {
                //else set txtseen as "Delivered"
                holder.txtSeen.setText("Delivered");
            }
        }
        else
        {
            //set txtseen as GONE for all other messages
            holder.txtSeen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    //viewholder class to provide a holder for views in recyclerview
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView user_message;
        public ImageView prof_image;
        public TextView txtSeen;

        public ViewHolder(View view) {
            super(view);
            user_message = view.findViewById(R.id.user_message);
            prof_image = view.findViewById(R.id.prof_image_message);
            txtSeen=view.findViewById(R.id.seen_status_tv);
        }
    }

    //get whether the message is sent by current user or other user
    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (messagesList.get(position).getSenderId().equals(firebaseUser.getUid())) {
            return MESSAGE_RIGHT;
        }
        return MESSAGE_LEFT;
    }
}
