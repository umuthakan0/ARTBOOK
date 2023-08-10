package com.demirgroup.artbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.demirgroup.artbook.databinding.ActivityFrmdetailsBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class frmdetails extends AppCompatActivity {
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedimage;
    private ActivityFrmdetailsBinding binding;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFrmdetailsBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        registerLaunhcer();
        Intent intent=getIntent();
        String info = intent.getStringExtra("info");
        if(info.equals("new")){
            //new add
            binding.btnsave.setVisibility(View.VISIBLE);

        }else {
            //old show
            binding.btnsave.setVisibility(View.INVISIBLE);
            try {
               int artId = intent.getIntExtra("artId",0);
                database = this.openOrCreateDatabase("Artbook", MODE_PRIVATE, null);
                Cursor cursor=database.rawQuery("SELECT * FROM artbook WHERE id=?",new String[]{String.valueOf(artId)});
                while (cursor.moveToNext()){
                    binding.editTextDateyear.setText(cursor.getString(3));
                    binding.editTextTextart.setText(cursor.getString(1));
                    binding.editTextTextartist.setText(cursor.getString(2));
                    byte[]bytes=cursor.getBlob(4);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView.setImageBitmap(bitmap);
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
    public Bitmap makeSmallImage(Bitmap image, int MaxSize){
        int width=image.getWidth();
        int height=image.getHeight();
        float imageraio=(float) height / (float) width;
        if(imageraio > 1){
            //portraitImage
            height=MaxSize;
            width=(int)(height/imageraio);
        }else {
            //landscape Image
            width=MaxSize;
            height=(int)(width*imageraio);
        }
        return image.createScaledBitmap(image,width,height,true);
    }
    public void openg(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"need to permission!!",Snackbar.LENGTH_INDEFINITE).setAction("Set Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //request permıssion
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }else{
                    //request permıssion
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                }
            }else {
                //open gallery
                Intent intenttogallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intenttogallery);
            }
        }else{
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"need to permission!!",Snackbar.LENGTH_INDEFINITE).setAction("Set Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //request permıssion
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                }else{
                    //request permıssion
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }else {
                //open gallery
                Intent intenttogallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intenttogallery);
            }
        }
    }
    private void registerLaunhcer(){
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK){
                    Intent intentfromresult = result.getData();
                    if (intentfromresult != null){
                        Uri imagessdata =intentfromresult.getData();
                        if (Build.VERSION.SDK_INT >= 28){
                            try {
                                ImageDecoder.Source source=ImageDecoder.createSource(getContentResolver(),imagessdata);
                                selectedimage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedimage);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            try {
                                selectedimage=MediaStore.Images.Media.getBitmap(getContentResolver(),imagessdata);
                                binding.imageView.setImageBitmap(selectedimage);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        permissionLauncher= registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    Intent intenttogallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intenttogallery);
                }else {
                    Toast.makeText(frmdetails.this,"need to permission!!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void save(View view){
        String artName=binding.editTextTextart.getText().toString();
        String artistName=binding.editTextTextartist.getText().toString();
        String artYear=binding.editTextDateyear.getText().toString();
        Bitmap smalllImage=makeSmallImage(selectedimage,200);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        smalllImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] arrayStream=outputStream.toByteArray();
        try {
            database = this.openOrCreateDatabase("Artbook", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS artbook (id INTEGER PRIMARY KEY, artname TEXT, artist TEXT, artyear TEXT, image BLOB)");

            SQLiteStatement sqLiteStatement = database.compileStatement("INSERT INTO artbook (artname, artist, artyear, image) VALUES (?, ?, ?, ?)");
            sqLiteStatement.bindString(1, artName);
            sqLiteStatement.bindString(2, artistName);
            sqLiteStatement.bindString(3, artYear);
            sqLiteStatement.bindBlob(4, arrayStream);
            sqLiteStatement.execute();
            database.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        Intent intent=new Intent(frmdetails.this,MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}