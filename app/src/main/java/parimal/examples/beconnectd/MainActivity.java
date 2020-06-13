package parimal.examples.beconnectd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;

import Fragments.ChatFragment;
import Fragments.ProfileFragment;
import Fragments.UserFragment;
import model.Message;

//MainActivity using tabLayout
public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set title in toolbar as "Be ConnectD"
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Be ConnectD");

        //get tabLayout and ViewPager
        final TabLayout tabLayout=(TabLayout)findViewById(R.id.tab_layout);
        final ViewPager viewPager=(ViewPager)findViewById(R.id.view_pager);

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        databaseReference=FirebaseDatabase.getInstance().getReference("chats");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
                int unreadMsg=0;
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    Message message=dataSnapshot1.getValue(Message.class);
                    if(message.getReceiverId().equals(firebaseUser.getUid())&&!message.getIsSeen().equals("true"))
                    {
                        unreadMsg++;
                    }
                }
                //add fragemnts to viewpager
                if(unreadMsg==0)
                {
                    viewPagerAdapter.addFragment(new ChatFragment(),"Chats");
                }
                else
                {
                    viewPagerAdapter.addFragment(new ChatFragment(),"("+unreadMsg+") Chats");
                }
                viewPagerAdapter.addFragment(new UserFragment(),"Users");
                viewPagerAdapter.addFragment(new ProfileFragment(),"Profile");

                //set adapter to viewPager
                viewPager.setAdapter(viewPagerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
    }

    //create menu for Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    //check menu item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            //if logout is selected
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }
        return false;
    }

    //ViewpagerAdapter to set items in viewpager
    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fmanager)
        {
            super(fmanager);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        public int getCount()
        {
            return fragments.size();
        }

        //add a new fragment to view pager
        public void addFragment(Fragment fragment,String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    //add status of a user to database
    private void status(String Status)
    {
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object>hashMap=new HashMap<>();

        hashMap.put("Status",Status);
        databaseReference.updateChildren(hashMap);
    }

    //set status
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    //set status
    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
