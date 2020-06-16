package com.mad.bookreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;

public class onlinereadActivity extends AppCompatActivity {

    PDFView pdfview;
    final static String TAG = "onlinereadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onlineread);
        pdfview = findViewById(R.id.pdfViewOnline);
        Intent geturl=getIntent();
        String url=geturl.getStringExtra("urllink");
        Log.v(TAG,"URL IS "+ url);
        FileLoader.with(this).load(url)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        File pdfFile= response.getBody();

                        pdfview.fromFile(pdfFile).defaultPage(0).enableSwipe(true).load();
                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                    }
                });
    }
}