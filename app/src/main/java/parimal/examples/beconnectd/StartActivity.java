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

//to display splash screen
public class StartActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //hide status bar and actionbar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

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
