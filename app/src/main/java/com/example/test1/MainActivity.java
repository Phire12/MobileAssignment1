package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PackageManagerCompat;
import androidx.appcompat.app.AppCompatActivity;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent; //added intent
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity{
    private final int REQUEST_IMAGE_CAPTURE = 123;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 7;

    private static final int pic_id = 123;
    Button camBtn;
    ImageView imageId;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        camBtn = (Button) findViewById(R.id.camButton);
        imageId = (ImageView) findViewById(R.id.click_image);
        MaterialButton storBtn = findViewById(R.id.storageButton);

        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = new Intent();
                //intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(intent, 123);
                takePhotoIntent();
                addToGallery();
            }
        });

        storBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    //permission success
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    String path = Environment.getExternalStorageDirectory().getPath();
                    intent.putExtra("path",path);
                    startActivity(intent);
                }else { //permission failed
                    requestPermission();
                }
            }
        });
    }

    private boolean checkPermission(){  //permission checker
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else
            return false;
    }

    private void requestPermission(){ //act of requesting permission from user
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this, "Permission required, please allow from settings.", Toast.LENGTH_SHORT).show();
        }else
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            //Bundle extras = data.getExtras();
            //Bitmap photo = (Bitmap) extras.get("data");
            //imageId.setImageBitmap(photo);
            //Bitmap photo = (Bitmap) data.getExtras().get("data");
            //imageId.setImageBitmap(photo);

            //Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            //intent.putExtra("BitmapImage", photo);
        }
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        //save file
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void takePhotoIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

       // if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try {

                photoFile = createImageFile();

            } catch (IOException ex) {

            }

            if (photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                        "com.example.test1",
                                                photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
       // }
    }

    private void addToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
