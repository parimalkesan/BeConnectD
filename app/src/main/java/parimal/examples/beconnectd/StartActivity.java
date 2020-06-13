package parimal.examples.beconnectd;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

//to display splash screen
public class StartActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //hide actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //set offline capability for data
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference("chats").keepSynced(true);

        //create new handler for StartActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //check for any logged in user and start the MainActivity class directly
                if(firebaseUser!=null) {
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                }
                //else start the LoginActivity class for user to login
                else
                {
                    startActivity(new Intent(StartActivity.this,LoginActivity.class));
                }
            }
        },3000);//open the new Activity after a delay of 3s
    }

    @Override
    protected void onStart() {
        super.onStart();
        //get current firebase user
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }
}
