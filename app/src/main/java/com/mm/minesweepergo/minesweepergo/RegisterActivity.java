package com.mm.minesweepergo.minesweepergo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import static android.R.attr.name;
import static android.R.attr.path;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Uri imagePath = null;
    static final int REQUEST_TAKE_PHOTO = 1;

    private String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                try {
                    if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");


                        this.imagePath = getImageUri(getApplicationContext(), imageBitmap);
                    }
                } catch (Exception e) {
                    Log.e("EditMyPlaceActivity", "Failed fetching location.");
                }
                break;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rgPictureBtn:
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);

            case R.id.rgRegisterBtn:
                EditText usernameET = (EditText)findViewById(R.id.rgUsername);
                EditText passwordET = (EditText)findViewById(R.id.rgPassword);
                EditText cpasswordET = (EditText)findViewById(R.id.rgConfirmPassword);
                EditText emailET = (EditText)findViewById(R.id.rgEmail);
                EditText phoneET = (EditText)findViewById(R.id.rgPhone);

                String username = usernameET.getText().toString();
                String password = passwordET.getText().toString();
                String cpassword = cpasswordET.getText().toString();
                String email = emailET.getText().toString();
                String phone =  phoneET.getText().toString();

                if(username == "" || password == "" || cpassword == "" || email == "" || phone == "" || this.imagePath == null)
                {
                    Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password != cpassword)
                {
                    Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
                    return;
                }
        }
    }
}
