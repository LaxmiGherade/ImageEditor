package com.example.imageeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    Button BSelectImage,FlipVerticle,FlipHorizontal,Crop,Save;
    ImageView IVPreviewImage;
    private Bitmap originalBitmap;
    private  float y=-1.0f;
    private  float x=1.0f;
    private Bitmap flippedBitmap;
    private boolean isFlippedVertical = false; // Flag to track vertical flip state
    private boolean isFlippedHorizontal = false; // Flag to track horizontal flip state




    int SELECT_PICTURE = 200;
    private Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BSelectImage = findViewById(R.id.image_edit_btn);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);
        FlipVerticle=findViewById(R.id.flip_vertical);
        FlipHorizontal=findViewById(R.id.flip_horizontal);
        Crop=findViewById(R.id.crop);
        Save=findViewById(R.id.save);

        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        if (selectedImageUri != null) {
            originalBitmap = loadImageFromUri(selectedImageUri);
            IVPreviewImage.setImageBitmap(originalBitmap);
        }

        FlipVerticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipVertical(y);
                if(y==-1.0f){
                    y=1.0f;
                }else{
                    y=-1.0f;
                }
            }
        });
        FlipHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipHorizontal(x);
                if(x==-1.0f){
                    x=1.0f;
                }else{
                    x=-1.0f;
                }
            }
        });
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToGallery();
            }
        });


        Crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                CropImage.activity(selectedImageUri)
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(EditImageActivity.this);


            }
        });

    }



    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//            if (resultCode == RESULT_OK) {
//                // Get the cropped image and set it to your ImageView
//                Uri croppedImageUri = result.getUri();
//                Bitmap croppedBitmap = loadImageFromUri(croppedImageUri);
//                IVPreviewImage.setImageBitmap(croppedBitmap);
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                // Handle cropping error
//                Exception error = result.getError();
//                Toast.makeText(this, "Error cropping image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                 selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    originalBitmap = loadImageFromUri(selectedImageUri);
                    IVPreviewImage.setImageBitmap(originalBitmap);
                }

            }
        }
    }

    private void saveToGallery() {
        BitmapDrawable drawable = (BitmapDrawable) IVPreviewImage.getDrawable();
        Bitmap editedBitmap = drawable.getBitmap();

        String relativeLocation = Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_folder_name);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "Edited" + System.currentTimeMillis() + ".jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, relativeLocation);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri imageUri = getContentResolver().insert(externalContentUri, values);

        try {
            OutputStream outputStream = getContentResolver().openOutputStream(imageUri);
            editedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            if (outputStream != null) {
                outputStream.close();
            }
            Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }







    private Bitmap loadImageFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void flipVertical(float a) {
        if (originalBitmap != null) {
            Matrix matrix = new Matrix();
            matrix.preScale(1.0f, a); // Flip vertically

            flippedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
            IVPreviewImage.setImageBitmap(flippedBitmap);

            isFlippedVertical = !isFlippedVertical;
        }
    }

    public void flipHorizontal(float b) {
        if (originalBitmap != null) {
            Matrix matrix = new Matrix();
            matrix.preScale(b, 1.0f); // Flip horizontally

            flippedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
            IVPreviewImage.setImageBitmap(flippedBitmap);

            isFlippedHorizontal = !isFlippedHorizontal;
        }
    }




}
