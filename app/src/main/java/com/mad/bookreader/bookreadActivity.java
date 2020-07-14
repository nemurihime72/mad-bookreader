package com.mad.bookreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class bookreadActivity extends AppCompatActivity {

    //Pdf view from the layout
    PDFView pdfview;
    //pdf name of the pdf file
    File pdfFile;
    String pdfUri;
    CountDownTimer tapCountdown;
    Uri uri;
    TextView pageNo;
    private final static String TAG= "bookreadActivity.java";

    //DB for this part
    public static int pageLastRead=0;
    public static int pageSwipeDirection;
    public static String pdfName;
    public static int noOfPages;
    public static int columnID;
    public static boolean isChecked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookreadlayout);
        pageNo=findViewById(R.id.pageNumber);
        LinearLayout pdfLayout = findViewById(R.id.pdfLayout);



        //Create database handler
        BookDBHandler dbHandler = new BookDBHandler(this, null, null, 1);


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
            columnID=Integer.parseInt(pdfPage.getStringExtra("id"));
            Log.v(TAG, "Getting information from intent");
            pdfName = pdfPage.getStringExtra("PdfName");
            Log.v(TAG, "PDF Name is: " + pdfName);
            pdfUri = pdfPage.getStringExtra("PdfUri");

            //Setting custom toolbar
            Toolbar toolbar = findViewById(R.id.bookreadbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(pdfName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),MainActivity.class);
                    v.getContext().startActivity(intent);
                }
            });
            Log.v(TAG, "Top toolbar set");

            pageSwipeDirection=dbHandler.pageSwipe(columnID);
            Log.v(TAG,"Page swipe direction: "+pageSwipeDirection);
            if (pageSwipeDirection==0){
                isChecked = false;
            }
            else if (pageSwipeDirection==1){
                isChecked = true;
            }

            //Find the PDFView and load the pdf from previous page to the view
            loadPDF(pdfName, pdfUri);
            Log.v(TAG, "PDF loaded");

        }

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

    public void loadPDF(final String pdfName, final String pdfURI) {
        final BookDBHandler dbHandler = new BookDBHandler(this, null, null, 1);
        pageLastRead = dbHandler.lastPage(columnID);
        pageSwipeDirection= dbHandler.pageSwipe(columnID);
        Log.v(TAG,"(Load)PAGE SWIPE DIRECTION="+pageSwipeDirection);
        Log.v(TAG, "file name: " + pdfName);
        //since Uri is still string, convert back to Uri to load
        uri = Uri.parse(pdfURI);
        pdfview = findViewById(R.id.pdfView);
        if (pageSwipeDirection == 0) {
            pdfview.fromUri(uri).defaultPage(pageLastRead + 1).onPageChange(new OnPageChangeListener() {
                @Override
                public void onPageChanged(int page, int pageCount) {
                    noOfPages=pdfview.getPageCount();
                    Log.v(TAG,"Pages total: "+noOfPages);
                    pageLastRead = pdfview.getCurrentPage();
                    Log.v(TAG, "Page changed to: " + pageLastRead);
                    pageNo.setText("Page: "+(pageLastRead+1)+"/"+noOfPages);
                    if (pageLastRead + 1 == pdfview.getPageCount()) {
                        pageLastRead = 0;
                        Log.v(TAG, "Finished reading, page last read returned to the start");
                    }
                    dbHandler.updateLastPage(columnID,pageLastRead);
                }
            }).load();
        }
        //Change from horizontal to vertical scrolling
        else if (pageSwipeDirection == 1){
            pdfview.fromUri(uri).swipeVertical(true).defaultPage(pageLastRead + 1).onPageChange(new OnPageChangeListener() {
                @Override
                public void onPageChanged(int page, int pageCount) {
                    noOfPages=pdfview.getPageCount();
                    Log.v(TAG,"Pages total: "+noOfPages);
                    pageLastRead = pdfview.getCurrentPage();
                    Log.v(TAG, "Page changed to: " + pageLastRead);
                    pageNo.setText("Page: "+(pageLastRead+1)+"/"+noOfPages);
                    if (pageLastRead + 1 == pdfview.getPageCount()) {
                        pageLastRead = 0;
                        Log.v(TAG, "Finished reading, page last read returned to the start");
                    }
                    dbHandler.updateLastPage(columnID,pageLastRead);
                }
            }).load();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.bookreadmenu,menu);
        MenuItem checkable = menu.findItem(R.id.vertical);
        checkable.setChecked(isChecked);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final BookDBHandler dbHandler = new BookDBHandler(this, null, null, 1);
        pageLastRead = dbHandler.lastPage(columnID);
        pageSwipeDirection=dbHandler.pageSwipe(columnID);
        switch (item.getItemId()) {
            case R.id.vertical:
                isChecked = !item.isChecked();
                if (isChecked==true){
                    pageSwipeDirection=1;
                    dbHandler.updatePageSwipe(columnID, pageSwipeDirection);
                    pdfview.fromUri(uri).swipeVertical(true).defaultPage(pageLastRead+1).onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            pageLastRead=pdfview.getCurrentPage();
                            Log.v(TAG,"Page changed to: "+pageLastRead);
                            pageNo.setText("Page: "+(pageLastRead+1)+"/"+noOfPages);
                            if (pageLastRead+1==pdfview.getPageCount()){
                                pageLastRead=0;
                                Log.v(TAG,"Finished reading, page last read returned to the start");
                            }
                            dbHandler.updateLastPage(columnID,pageLastRead);
                        }
                    }).load();
                }
                else if(isChecked==false){
                    pageSwipeDirection=0;
                    dbHandler.updatePageSwipe(columnID, pageSwipeDirection);
                    pdfview.fromUri(uri).defaultPage(pageLastRead+1).onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            pageLastRead=pdfview.getCurrentPage();
                            Log.v(TAG,"Page changed to: "+pageLastRead);
                            pageNo.setText("Page: "+(pageLastRead+1)+"/"+noOfPages);
                            if (pageLastRead+1==pdfview.getPageCount()){
                                pageLastRead=0;
                                Log.v(TAG,"Finished reading, page last read returned to the start");
                            }
                            dbHandler.updateLastPage(columnID,pageLastRead);
                        }
                    }).load();
                }
                item.setChecked(isChecked);
                return true;

            case android.R.id.home:
                finish();
                return true;
            case R.id.goToPage:
                android.app.AlertDialog.Builder pagebuilder=new AlertDialog.Builder(this);
                View view= LayoutInflater.from(this).inflate(R.layout.dialogue,null);
                final EditText goToPage = (EditText) view.findViewById(R.id.fileTitle);
                goToPage.setHint("Page no");
                pagebuilder.setView(view).setTitle("Go To Page")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String pageNo1=goToPage.getText().toString();

                                if (pageNo1.equals("") || pageNo1.equals(null)) {
                                    Toast.makeText(getApplicationContext(),"Please enter a title", Toast.LENGTH_SHORT).show();
                                } else {
                                    int pageNumber=Integer.parseInt(pageNo1)-1;
                                    pageLastRead=pageNumber;
                                    dbHandler.updateLastPage(columnID,pageLastRead);
                                    loadPDF(pdfName, pdfUri);
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG,"Cancel \"Go to Page \" feature");
                    }
                });
                AlertDialog pagealert=pagebuilder.create();
                Log.v(TAG,"Alert dialog for \"Go to page\" successfully created");
                pagealert.show();
                return true;

            //To change the scroll direction between vertical and horizontal
            /*case R.id.scrolldirection:
                Log.v(TAG,"(CASE)PAGE SWIPE DIRECTION="+pageSwipeDirection);
                //Change from vertical to horizontal
                if (pageSwipeDirection==1){
                    pageSwipeDirection=0;
                    dbHandler.updatePageSwipe(columnID, pageSwipeDirection);
                    pdfview.fromUri(uri).defaultPage(pageLastRead+1).onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            pageLastRead=pdfview.getCurrentPage();
                            Log.v(TAG,"Page changed to: "+pageLastRead);
                            pageNo.setText("Page: "+(pageLastRead+1)+"/"+noOfPages);
                            if (pageLastRead+1==pdfview.getPageCount()){
                                pageLastRead=0;
                                Log.v(TAG,"Finished reading, page last read returned to the start");
                            }
                            dbHandler.updateLastPage(columnID,pageLastRead);
                        }
                    }).load();
                    Log.v(TAG,"Setting scrolling to horizontal");
                    return true;
                }
                //Change from horizontal to vertical scrolling
                else if (pageSwipeDirection==0){
                    pageSwipeDirection=1;
                    Log.v(TAG,"pageswipe: "+dbHandler.pageSwipe(columnID));
                    dbHandler.updatePageSwipe(columnID,pageSwipeDirection);
                    pdfview.fromUri(uri).swipeVertical(true).defaultPage(pageLastRead+1).onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            pageLastRead=pdfview.getCurrentPage();
                            Log.v(TAG,"Page changed to: "+pageLastRead);
                            pageNo.setText("Page: "+(pageLastRead+1)+"/"+noOfPages);
                            if (pageLastRead+1==pdfview.getPageCount()){
                                pageLastRead=0;
                                Log.v(TAG,"Finished reading, page last read returned to the start");
                            }
                            dbHandler.updateLastPage(columnID,pageLastRead);
                        }
                    }).load();
                    Log.v(TAG,"Setting scrolling changed to vertical");
                    return true;
                }*/

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

    protected void onStart(){
        super.onStart();
        Log.v(TAG, "Starting GUI!");
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.v(TAG, "Resuming...");
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.v(TAG, "Pausing...");
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.v(TAG, "Stopping!");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.v(TAG, "Destroying!");
    }

}
