package com.mm.minesweepergo.minesweepergo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.mm.minesweepergo.minesweepergo.DomainModel.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mm.minesweepergo.minesweepergo.Utilities.getImageUri;

public class UserPanel extends AppCompatActivity implements View.OnClickListener{

    private User user;
    private Uri imagePath = null;
    static final int REQUEST_TAKE_PHOTO = 1;

    private String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new Date().toString();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panel);

        Intent i = getIntent();
        user = (User) i.getParcelableExtra("userInfo");
        setImage();

        Toast.makeText(this, user.username, Toast.LENGTH_SHORT).show();

        Button uploadBtn = (Button) findViewById(R.id.upUploadBtn);
        uploadBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upUploadBtn:
                dispatchTakePictureIntent();
                break;
        }
    }

    public void setImage(){
        ExecutorService transThread= Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream input = new java.net.URL(Constants.URL + user.imagePath).openStream();
                    ImageView iv = (ImageView) findViewById(R.id.upProfileImageView);
                    iv.setImageBitmap(BitmapFactory.decodeStream(input));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
