package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import model.User;
import parimal.examples.beconnectd.R;
import parimal.examples.beconnectd.UserAdapter;

//user fragment to display all the users other than current user of the application
public class UserFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    //list to store users information
    private List<User> userList;
    //edittext to search for a particular user
    private EditText userSearch;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_user, container, false);

        //get recycler view
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList=new ArrayList<>();

        //get reference of usersearch and add textchangelistener to it.
        userSearch=view.findViewById(R.id.userSearch_edt);
        userSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //to get all the users of the app other than current user
        getUsers();

        return view;
    }

    //search for a particular user
    private void searchUser(String s) {
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        //get all the users with starting alphabet as string "s" and store in query
        Query query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("Email")
                .startAt(s)
                .endAt(s+"\uf8ff");

        //add valueeventlistener to query to add users to userlist
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    User user=dataSnapshot1.getValue(User.class);
                    assert user!=null;
                    assert firebaseUser!=null;
                    if(!user.getUserId().equals(firebaseUser.getUid()))
                    {
                        userList.add(user);
                    }
                }

                //add adapter to recycler view to only display searched users.
                userAdapter=new UserAdapter(getContext(),userList,false);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //to display all the users other than current user
    private void getUsers() {
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbReference= FirebaseDatabase.getInstance().getReference("Users");

        //add valueeventlistener and store all the users data in userlist
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //only if edittext is empty
                if (userSearch.getText().toString().equals("")) {
                    userList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        User user = dataSnapshot1.getValue(User.class);

                        assert user != null;
                        assert firebaseUser != null;

                        if (!firebaseUser.getUid().equals(user.getUserId())) {
                            userList.add(user);
                        }
                    }
                    //add adapter to recyclerview to display all users excluding current user
                    userAdapter = new UserAdapter(getContext(), userList, false);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
