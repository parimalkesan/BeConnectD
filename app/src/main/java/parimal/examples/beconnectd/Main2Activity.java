package parimal.examples.beconnectd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Main2Activity extends AppCompatActivity {
FirebaseAuth auth;
DatabaseReference boj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        auth=FirebaseAuth.getInstance();
        boj= FirebaseDatabase.getInstance().getReference("User");


     boj = FirebaseDatabase.getInstance().getReference("Users");
     boj.child("Nilesh").setValue(new User(auth.getCurrentUser().getDisplayName(),auth.getCurrentUser().getEmail())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.w("LoginActivity", "Authentication successful");
                    Toast.makeText(Main2Activity.this, "Authentication Successful", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),Main2Activity.class));
                }
            }
        });
    }
}
