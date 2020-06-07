package com.mad.bookreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class bookreadActivity extends AppCompatActivity {

    //Pdf view from the layout
    PDFView pdfview;
    //pdf name of the pdf file
    String pdfName;
    File pdfFile;
    String pdfUri;
    CountDownTimer tapCountdown;
    Uri uri;
    private final static String TAG= "bookreadActivity.java";

    //DB for this part
    public static int pageLastRead=0;
    public static int pageSwipeDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookreadlayout);
        LinearLayout pdfLayout = findViewById(R.id.pdfLayout);


        //Get listBooks from MainActivity


       /* Intent listBooks = getIntent();
        if(listBooks.hasExtra("BUNDLE ")){
            Bundle importedBookList = listBooks.getBundleExtra("BUNDLE");
            List<importedBooks> bookList = (List<importedBooks>) importedBookList.getSerializable("ARRAYLIST");
            for(int i = 0; i < bookList.size(); i++){
                importedBooks book = bookList.get(i);
                loadPDF(book.getTitle(), book.getPdfUri());
            }
        }*/
        //List<List<String>> storedbooks =  new ArrayList<>();
       //storedbooks = dbHandler.startBooks(storedbooks);
       //for (int i, )


        //Get the pdf name from previous page
        Intent pdfPage=getIntent();
        if (pdfPage.hasExtra("PdfName") | pdfPage.hasExtra("PdfUri")) {

            //Setting variable pdfName as the name of the pdf file
            Log.v(TAG, "Getting information from intent");
            pdfName = pdfPage.getStringExtra("PdfName");
            Log.v(TAG, "PDF Name is: " + pdfName);
            pdfUri = pdfPage.getStringExtra("PdfUri");

            //Setting custom toolbar
            Toolbar toolbar = findViewById(R.id.bookreadbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(pdfName);
            Log.v(TAG, "Top toolbar set");

            //Find the PDFView and load the pdf from previous page to the view
            loadPDF(pdfName, pdfUri);
            Log.v(TAG, "PDF loaded");
        }

        //Create database handler
        BookDBHandler dbHandler = new BookDBHandler(this, null, null, 1);




        //pdfview.fromUri(pdfUri);
        /*pdfview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v(TAG, "pdf tapped");
                setTapCountdown();
                return false;
            }
        });*/
    }

    public void loadPDF(final String pdfName, final String pdfURI){
        final BookDBHandler dbHandler = new BookDBHandler(this, null, null, 1);
        pageLastRead = dbHandler.lastPage(pdfName) + 1;
        Log.v(TAG, "file name: " + pdfName);
        //since Uri is still string, convert back to Uri to load
        uri = Uri.parse(pdfURI);
        pdfview = findViewById(R.id.pdfView);
        pdfview.fromUri(uri).defaultPage(pageLastRead).onPageChange(new OnPageChangeListener() {
            @Override
            public void onPageChanged(int page, int pageCount) {
                pageLastRead = pdfview.getCurrentPage();
                dbHandler.updateLastPage(pdfName, pdfview.getCurrentPage());
                Log.v(TAG,"Page changed to: "+pageLastRead);
                if (pageLastRead+1==pdfview.getPageCount()){
                    pageLastRead = 0;//dbHandler.lastPage();
                    Log.v(TAG,"Finished reading, page returned to the start");
                }
            }
        }).load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.bookreadmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    /*sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(pageSwipeDirection, )*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //To change the scroll direction between vertical and horizontal
            case R.id.scrolldirection:
                //Change from vertical to horizontal
                if (pageSwipeDirection==1){
                    pageSwipeDirection=0;
                    pdfview.fromUri(uri).defaultPage(pageLastRead+1).onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            pageLastRead=pdfview.getCurrentPage();
                            Log.v(TAG,"Page changed to: "+pageLastRead);
                            if (pageLastRead+1==pdfview.getPageCount()){
                                pageLastRead=0;
                                Log.v(TAG,"Finished reading, page last read returned to the start");
                            }
                        }
                    }).load();
                    Log.v(TAG,"Setting scrolling to horizontal");
                    return true;
                }
                //Change from horizontal to vertical scrolling
                else{
                    pageSwipeDirection=1;
                    pdfview.fromUri(uri).swipeVertical(true).defaultPage(pageLastRead+1).onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            pageLastRead=pdfview.getCurrentPage();
                            Log.v(TAG,"Page changed to: "+pageLastRead);
                            if (pageLastRead+1==pdfview.getPageCount()){
                                pageLastRead=0;
                                Log.v(TAG,"Finished reading, page last read returned to the start");
                            }
                        }
                    }).load();
                    Log.v(TAG,"Setting scrolling changed to vertical");
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setTapCountdown() {
        getSupportActionBar().show();
        tapCountdown = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.v(TAG, "Countdown for app bar: " + millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                getSupportActionBar().hide();
            }
        }.start();
    }
}
