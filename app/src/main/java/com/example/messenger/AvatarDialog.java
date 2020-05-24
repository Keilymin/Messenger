package com.example.messenger;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class AvatarDialog extends DialogFragment implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private ImageView avatar;
    private Button setAvatar;
    private String avatarImage;
    static final int GALLERY_REQUEST = 1;
    private StorageReference storageReference;
    private Uri imageUri;
    private StorageTask task;
    private int state = 0;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        storageReference = FirebaseStorage.getInstance().getReference("images");
        builder.setView(inflater.inflate(R.layout.avatar_dialog, null))
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if(task != null && task.isInProgress()){
                            Toast.makeText(getContext(),"Загрузка в процессе",Toast.LENGTH_SHORT).show();
                        } else {
                            uploadImage();
                            updateAvatar();
                        }


                        AvatarDialog.this.getDialog().cancel();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mAuth.getCurrentUser().getDisplayName() == null) {
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            AvatarDialog.this.getDialog().cancel();
                        }
                    }
                });
        return builder.create();
    }
    Context tgetContext(){
        return getContext();
    }
    @Override
    public void onStart() {
        super.onStart();
        avatar = getDialog().findViewById(R.id.avatar);
        setAvatar = getDialog().findViewById(R.id.setAvatar);
        setAvatar.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        if(state == 0) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users").child(mAuth.getUid()).child("image");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    avatarImage = dataSnapshot.getValue(String.class);
                    if(state == 0){
                    if (avatarImage.equals("def")) {
                        avatar.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(tgetContext()).load(avatarImage).into(avatar);
                    }}

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    avatar.setImageResource(R.mipmap.ic_launcher);

                }
            });
        }

    }

    void updateAvatar() {
        FirebaseUser user = mAuth.getCurrentUser();
        // Updates the user attributes:



            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
            View hView = navigationView.getHeaderView(0);
            TextView textEmail = hView.findViewById(R.id.text_email);
            TextView textLogin = hView.findViewById(R.id.text_login);
            ImageView imageView = hView.findViewById(R.id.imageView);
            textEmail.setText(user.getEmail());
            textLogin.setText(user.getDisplayName());
            Glide.with(this).load(avatarImage).into(imageView);

    }

    @Override
    public void onClick(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        imageUri = imageReturnedIntent.getData();
        avatarImage = imageUri.toString();
        state = 1;
        avatar.setImageURI(imageUri);

    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Загрузка");
        pd.show();

        if(imageUri != null){
            final  StorageReference fileReferense = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));
            task = fileReferense.putFile(imageUri);
            task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return  fileReferense.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Users").child(mAuth.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("image", mUri);
                        myRef.updateChildren(hashMap);

                        pd.dismiss();
                    }else {
                        Toast.makeText(getContext(),"Ошибка",Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(),"Ошибка",Toast.LENGTH_SHORT).show();

        }
    }

}
