package parimal.examples.beconnectd;

import android.content.Context;
import android.content.Intent;
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

import org.w3c.dom.Text;

import java.util.List;

import model.User;

import static androidx.constraintlayout.widget.Constraints.TAG;

//adapter class to recyclerview in UserFragment and ChatFragment
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
     private Context context;
     private List<User> userList;
     public boolean ischat;

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
        public ViewHolder(View view)
        {
            super(view);
            userEmail_tv=view.findViewById(R.id.userEmail_tv);
            user_prof_image=view.findViewById(R.id.user_prof_image);
            userStatus_on=view.findViewById(R.id.img_status_on);
            userStatus_off=view.findViewById(R.id.img_status_off);
        }
    }
}
