package com.mad.bookreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class onlinereadActivity extends AppCompatActivity {

    PDFView pdfview;
    String url;
    TextView pageNo;
    final static String TAG = "onlinereadActivity";

    public static int id;
    public static int pageSwipeDirection;
    public static int noOfPages;
    public static int currentPage;
    public static boolean isChecked;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onlineread);
        //Setting custom toolbar
        Toolbar toolbar = findViewById(R.id.bookreadbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),MainActivity.class);
                v.getContext().startActivity(intent);
            }
        });
        boolean internetavailable=haveNetworkConnection();
        Log.v(TAG,"CHECKING INTERNETAVAILBLE"+internetavailable);
        if (!internetavailable){
            Toast.makeText(this,"Not connected to Wifi/Data",Toast.LENGTH_LONG).show();
        }




        final BookDBHandler dbHandler = new BookDBHandler(this, null, null, 1);



        pageNo=findViewById(R.id.pageNumbers);
        pdfview = findViewById(R.id.pdfViewOnline);
        Intent geturl=getIntent();
        url=geturl.getStringExtra("urllink");
        id=Integer.parseInt(geturl.getStringExtra("id"));
        Log.v(TAG,"URL IS "+ url);
        currentPage=dbHandler.lastPage(id);
        Log.v(TAG,"LAST READ:"+currentPage);
        pageSwipeDirection=dbHandler.pageSwipe(id);
        if (pageSwipeDirection==0){
            isChecked = false;
        }
        else if (pageSwipeDirection==1){
            isChecked = true;
        }

        if (pageSwipeDirection==0){
            FileLoader.with(getApplicationContext()).load(url)
                    .asFile(new FileRequestListener<File>() {
                        @Override
                        public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                            File pdfFile = response.getBody();
                            pdfview.fromFile(pdfFile).defaultPage(currentPage+1).onPageChange(new OnPageChangeListener() {
                                @Override
                                public void onPageChanged(int page, int pageCount) {
                                    noOfPages = pdfview.getPageCount();
                                    currentPage = pdfview.getCurrentPage();
                                    pageNo.setText("Page: "+(currentPage+1)+"/"+noOfPages);
                                    if (currentPage==pdfview.getPageCount()-1){
                                        currentPage=0;
                                        Log.v(TAG,"Finished reading, page last read returned to the start");
                                    }
                                    Log.v(TAG,"LAST READ(PAGE CHANGE):"+currentPage);
                                    dbHandler.updateLastPage(id,currentPage);
                                    Log.v(TAG,"Updated db last page");
                                }
                            }).load();
                        }

                        @Override
                        public void onError(FileLoadRequest request, Throwable t) {
                            if (haveNetworkConnection()){
                                Log.v(TAG,"Url pdf does not exist");
                                Toast.makeText(getApplicationContext(), "Url of pdf does not exist", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else {
            FileLoader.with(getApplicationContext()).load(url)
                    .asFile(new FileRequestListener<File>() {
                        @Override
                        public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                            File pdfFile = response.getBody();
                            pdfview.fromFile(pdfFile).swipeVertical(true).defaultPage(currentPage + 1).onPageChange(new OnPageChangeListener() {
                                @Override
                                public void onPageChanged(int page, int pageCount) {
                                    noOfPages = pdfview.getPageCount();
                                    currentPage = pdfview.getCurrentPage();
                                    pageNo.setText("Page: " + (currentPage + 1) + "/" + noOfPages);
                                    if (currentPage == pdfview.getPageCount() - 1) {
                                        currentPage = 0;
                                        Log.v(TAG, "Finished reading, page last read returned to the start");
                                    }
                                    Log.v(TAG, "LAST READ(PAGE CHANGE):" + currentPage);
                                    dbHandler.updateLastPage(id, currentPage);
                                    Log.v(TAG, "Updated db last page");
                                }
                            }).load();
                        }

                        @Override
                        public void onError(FileLoadRequest request, Throwable t) {
                            if (haveNetworkConnection()) {
                                Log.v(TAG, "Url pdf does not exist");
                                Toast.makeText(getApplicationContext(), "Url of pdf does not exist", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
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
        currentPage = dbHandler.lastPage(id);
        pageSwipeDirection=dbHandler.pageSwipe(id);
        switch (item.getItemId()) {
            case R.id.vertical:
                isChecked = !item.isChecked(); //true means not vert
                if (isChecked==false){
                    pageSwipeDirection=0;
                    dbHandler.updatePageSwipe(id, pageSwipeDirection);
                    FileLoader.with(getApplicationContext()).load(url)
                            .asFile(new FileRequestListener<File>() {
                                @Override
                                public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                    File pdfFile = response.getBody();
                                    pdfview.fromFile(pdfFile).defaultPage(currentPage+1).onPageChange(new OnPageChangeListener() {
                                        @Override
                                        public void onPageChanged(int page, int pageCount) {
                                            noOfPages = pdfview.getPageCount();
                                            currentPage = pdfview.getCurrentPage();
                                            pageNo.setText("Page: " + (currentPage+1) + "/" + noOfPages);
                                            if (currentPage==pdfview.getPageCount()-1){
                                                currentPage=0;
                                                Log.v(TAG,"Finished reading, page last read returned to the start");
                                            }
                                            dbHandler.updateLastPage(id,currentPage);
                                        }
                                    }).load();
                                }

                                @Override
                                public void onError(FileLoadRequest request, Throwable t) {
                                    if (haveNetworkConnection()) {
                                        Log.v(TAG, "Url pdf does not exist");
                                        Toast.makeText(getApplicationContext(), "Url of pdf does not exist", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                else if(isChecked==true){
                    pageSwipeDirection=1;
                    dbHandler.updatePageSwipe(id, pageSwipeDirection);
                    FileLoader.with(getApplicationContext()).load(url)
                            .asFile(new FileRequestListener<File>() {
                                @Override
                                public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                    File pdfFile = response.getBody();
                                    pdfview.fromFile(pdfFile).swipeVertical(true).defaultPage(currentPage+1).onPageChange(new OnPageChangeListener() {
                                        @Override
                                        public void onPageChanged(int page, int pageCount) {
                                            noOfPages = pdfview.getPageCount();
                                            currentPage = pdfview.getCurrentPage();
                                            pageNo.setText("Page: " + (currentPage+1) + "/" + noOfPages);
                                            if (currentPage==pdfview.getPageCount()-1){
                                                currentPage=0;
                                                Log.v(TAG,"Finished reading, page last read returned to the start");
                                            }
                                            dbHandler.updateLastPage(id,currentPage);
                                        }
                                    }).load();
                                }

                                @Override
                                public void onError(FileLoadRequest request, Throwable t) {
                                    if (haveNetworkConnection()) {
                                        Log.v(TAG, "Url pdf does not exist");
                                        Toast.makeText(getApplicationContext(), "Url of pdf does not exist", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
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
                        .setPositiveButton("Go", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String pageNo1=goToPage.getText().toString();
                                if (pageNo1.equals("") || pageNo1.equals(null)) {
                                    Toast.makeText(getApplicationContext(),"Please enter a page number", Toast.LENGTH_SHORT).show();
                                } else {
                                    final int pageNumber=Integer.parseInt(pageNo1);
                                    if (pageSwipeDirection==1){
                                        FileLoader.with(getApplicationContext()).load(url)
                                                .asFile(new FileRequestListener<File>() {
                                                    @Override
                                                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                                        File pdfFile = response.getBody();
                                                        pdfview.fromFile(pdfFile).defaultPage(pageNumber).onPageChange(new OnPageChangeListener() {
                                                            @Override
                                                            public void onPageChanged(int page, int pageCount) {
                                                                noOfPages = pdfview.getPageCount();
                                                                currentPage = pdfview.getCurrentPage();
                                                                pageNo.setText("Page: " + (currentPage+1) + "/" + noOfPages);
                                                                if (currentPage==pdfview.getPageCount()-1){
                                                                    currentPage=0;
                                                                    Log.v(TAG,"Finished reading, page last read returned to the start");
                                                                }
                                                                Log.v(TAG,"LAST READ(PAGE CHANGE):"+currentPage);
                                                                dbHandler.updateLastPage(id,currentPage);
                                                            }
                                                        }).load();
                                                    }

                                                    @Override
                                                    public void onError(FileLoadRequest request, Throwable t) {
                                                        if (haveNetworkConnection()) {
                                                            Log.v(TAG, "Url pdf does not exist");
                                                            Toast.makeText(getApplicationContext(), "Url of pdf does not exist", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    }
                                    else {
                                        FileLoader.with(getApplicationContext()).load(url)
                                                .asFile(new FileRequestListener<File>() {
                                                    @Override
                                                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                                        File pdfFile = response.getBody();
                                                        pdfview.fromFile(pdfFile).swipeVertical(true).defaultPage(pageNumber).onPageChange(new OnPageChangeListener() {
                                                            @Override
                                                            public void onPageChanged(int page, int pageCount) {
                                                                noOfPages = pdfview.getPageCount();
                                                                currentPage = pdfview.getCurrentPage();
                                                                pageNo.setText("Page: " + (currentPage+1) + "/" + noOfPages);
                                                                if (currentPage == pdfview.getPageCount()-1) {
                                                                    currentPage = 0;
                                                                    Log.v(TAG, "Finished reading, page last read returned to the start");
                                                                }
                                                                Log.v(TAG,"LAST READ(PAGE CHANGE):"+currentPage);
                                                                dbHandler.updateLastPage(id, currentPage);
                                                            }
                                                        }).load();
                                                    }

                                                    @Override
                                                    public void onError(FileLoadRequest request, Throwable t) {
                                                        if (haveNetworkConnection()) {
                                                            Log.v(TAG, "Url pdf does not exist");
                                                            Toast.makeText(getApplicationContext(), "Url of pdf does not exist", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    }
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Check if wifi/data is on
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
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