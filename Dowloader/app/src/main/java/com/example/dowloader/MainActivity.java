package com.example.dowloader;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private static final int Request_code = 100;

    Button download;
    EditText url,Fn;
    String URL;
    String FileName;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        download = findViewById(R.id.download);
        url = findViewById(R.id.url);
        Fn = findViewById(R.id.FileName);

        Intent intent = getIntent();
       String action =  intent.getAction();
       String type = intent.getType();
       Log.d("Type",""+type);
        Log.d("Type",""+intent.getStringExtra(Intent.EXTRA_TEXT));

      /* if(Intent.ACTION_SEND.equals(action) && type!=null) {
       url.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
       }*/
        if(Intent.ACTION_SEND.equals(action)&&type!=null) {
            ImageView img = findViewById(R.id.imageView);
            img.setImageURI(intent.getParcelableExtra(Intent.EXTRA_STREAM));
            Log.d("Type",""+type);
            img.setVisibility(View.VISIBLE);
        }


       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
           if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
               requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},Request_code);
           }
       }

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileName = String.valueOf(Fn.getText()).trim();
                URL = String.valueOf(url.getText()).trim();
                download(URL,FileName);

            }
        });
    }
    public void download(String url, String outFileName){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(FileName);
        request.setDescription("Downloading "+FileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.allowScanningByMediaScanner();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,outFileName);
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}