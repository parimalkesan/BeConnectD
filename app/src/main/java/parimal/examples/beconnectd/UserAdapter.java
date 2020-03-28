package parimal.examples.beconnectd;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

import model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
     private Context context;
     private List<User> userList;

     public UserAdapter(Context context,List<User> users)
     {
         this.context=context;
         this.userList=users;
     }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view= LayoutInflater.from(context).inflate(R.layout.user_data_list,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

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

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView userEmail_tv;
        public ImageView user_prof_image;
        public ViewHolder(View view)
        {
            super(view);
            userEmail_tv=view.findViewById(R.id.userEmail_tv);
            user_prof_image=view.findViewById(R.id.user_prof_image);
        }
    }
}
