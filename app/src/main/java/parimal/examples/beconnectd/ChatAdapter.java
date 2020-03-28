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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context context;
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

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MESSAGE_RIGHT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.messages_right, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.messages_left, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        final Message message = messagesList.get(position);
        holder.user_message.setText(message.getMessage());
        if ("default".equals(imageUrl)) {
            holder.prof_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(imageUrl).into(holder.prof_image);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView user_message;
        public ImageView prof_image;

        public ViewHolder(View view) {
            super(view);
            user_message = view.findViewById(R.id.user_message);
            prof_image = view.findViewById(R.id.prof_image_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (messagesList.get(position).getSenderId().equals(firebaseUser.getUid())) {
            return MESSAGE_RIGHT;
        }
        return MESSAGE_LEFT;
    }
}
