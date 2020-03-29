package Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import model.User;
import parimal.examples.beconnectd.R;

import static android.app.Activity.RESULT_OK;

//to display profile of current user
public class ProfileFragment extends Fragment {

    CircleImageView prof_image_view;
    TextView prof_email;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    StorageReference storageReference;
    public static final int IMAGE_REQ = 1;
    private Uri ImageUrl;
    private StorageTask taskUpload;

    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //get reference to prof_image_view and prof_email_tv
        prof_image_view = view.findViewById(R.id.prof_image_view);
        prof_email = view.findViewById(R.id.prof_email_tv);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        //get reference to FirebaseStorage with root as"Uploads"
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");

            //add valueeventlistener to set email and profile image of current user
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    prof_email.setText(user.getEmail());
                    if ("default".equals(user.getImageUrl())) {
                        prof_image_view.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        if (getActivity()!= null)
                            Glide.with(getContext()).load(user.getImageUrl()).into(prof_image_view);
                    }
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        //add onclicklistener to prof_image_view to set profle image
        prof_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_ImageView();
            }
        });

        return view;
    }

    //call intent to get profile image from external storage
    private void open_ImageView() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQ);
    }

    //get mime type of obtained uri
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //upload image to FirebaseStorage and FirebaseDatabase
    private void imageUpload() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (ImageUrl != null) {
            final StorageReference fileStorage = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(ImageUrl));
            taskUpload = fileStorage.putFile(ImageUrl);

            taskUpload.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileStorage.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        //get uri of uploaded image
                        Uri downloadUri=task.getResult();
                        String uri=downloadUri.toString();

                        //get databaseReference and store imageurl at right child node(current user)
                        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("ImageUrl",uri);
                        databaseReference.updateChildren(hashMap);

                        sharedPreferences=getContext().getSharedPreferences("sharedUrl", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("uri",uri);
                        editor.commit();

                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }
        else
        {
            Toast.makeText(getContext(),"No Image Selected",Toast.LENGTH_LONG).show();
        }
    }

    //get ImageUrl from called intent and call imageUpload()
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==IMAGE_REQ&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null)
        {
            ImageUrl=data.getData();
            //if an image is currently being uploaded
            if(taskUpload!=null&&taskUpload.isInProgress())
            {
                Toast.makeText(getContext(),"Upload in Progress",Toast.LENGTH_LONG).show();
            }
            else
            {
               imageUpload();
            }
        }
    }
}
