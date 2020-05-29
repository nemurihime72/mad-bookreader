package com.mad.bookreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;

public class bookreadActivity extends AppCompatActivity {

    //Pdf view from the layout
    PDFView pdfview;
    //pdf name of the pdf file
    String pdfName;
    File pdfFile;
    String pdfUri;
    CountDownTimer tapCountdown;
    private final static String TAG="bookreadActivity.java";

    //DB for this part
    public static int pageLastRead=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookreadlayout);

        //Get the pdf name from previous page
        Intent pdfPage=getIntent();

        //Setting variable pdfName as the name of the pdf file
        pdfName=pdfPage.getStringExtra("PdfName");
        pdfUri = pdfPage.getStringExtra("PdfUri");
        Uri uri = Uri.parse(pdfUri);

        //Setting custom toolbar
        Toolbar toolbar=findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        Log.v(TAG,"Top toolbar set");

        //Find the PDFView and load the pdf from previous page to the view
        pdfview=findViewById(R.id.pdfView);
        pdfview.fromUri(uri).defaultPage(pageLastRead).onPageChange(new OnPageChangeListener() {
            @Override
            public void onPageChanged(int page, int pageCount) {
                pageLastRead=pdfview.getCurrentPage();
                Log.v(TAG,"Page changed to: "+pageLastRead);
            }
        }).load();
        //pdfview.fromUri(pdfUri);

        //Set instead of vertical scrolling, becomes horizontal scrolling, there will be a setting for this later on
        setSwipeHorizon(pdfview);


        /*pdfview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v(TAG, "pdf tapped");
                setTapCountdown();
                return false;
            }
        });*/


    }

    public void setSwipeHorizon(PDFView pdfview){
        pdfview.setSwipeVertical(false);
        Log.v(TAG,"Setting scrolling to horizontal");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setTapCountdown() {
        tapCountdown = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getSupportActionBar().show();
                Log.v(TAG, "Countdown for app bar: " + millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                getSupportActionBar().hide();
            }
        }.start();
    }
}
