package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.service.autofill.Dataset;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import model.Message;
import model.User;
import parimal.examples.beconnectd.R;
import parimal.examples.beconnectd.UserAdapter;

//to add a chat fragment to the app
//displays the other users with whom we chatted
public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    //to store id of users we had chat
    private List<String> userList1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        //get recycler view and linear layout maanger
        recyclerView = view.findViewById(R.id.recycler_view_chathistory);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //get current user and database reference to root "chats"
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("chats");

        userList1 = new ArrayList<>();

        //put userids in userlist1 with whom we had chat from root ""chats
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList1.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Message message = dataSnapshot1.getValue(Message.class);
                    if(firebaseUser.getUid().equals(message.getSenderId()))
                    {
                        userList1.add(message.getReceiverId());
                    }
                    if(firebaseUser.getUid().equals(message.getReceiverId()))
                    {
                        userList1.add(message.getSenderId());
                    }
                }

                //call chatUsers
                chatUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    //get list of users who had chats with current user and add users other than current user to userList
    private void chatUsers()
    {
        userList=new ArrayList<>();
        databaseReference=FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    User user=dataSnapshot1.getValue(User.class);
                    for(String id:userList1)
                    {
                        if(user.getUserId().equals(id))
                        {
                            if(userList.size()!=0)
                            {
                                int z=0;
                                for(int i=0;i<userList.size();i++)
                                {
                                    User user1=userList.get(i);
                                    if(user.getUserId().equals(user1.getUserId()))
                                    {
                                        z=1;
                                    }
                                }
                                if(z==0)
                                {
                                    userList.add(user);
                                }
                            }
                            else
                            {
                                userList.add(user);
                            }
                        }
                    }
                }

                //create useradapter for recyclerView
                userAdapter=new UserAdapter(getContext(),userList,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
