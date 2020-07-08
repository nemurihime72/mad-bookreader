package com.mad.bookreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class onlinereadActivity extends AppCompatActivity {

    PDFView pdfview;
    String url;
    TextView pageNo;
    final static String TAG = "onlinereadActivity";

    public static int pageSwipeDirection;
    public static int noOfPages;
    public static int currentPage;



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

        pageNo=findViewById(R.id.pageNumbers);
        pdfview = findViewById(R.id.pdfViewOnline);
        Intent geturl=getIntent();
        url=geturl.getStringExtra("urllink");
        Log.v(TAG,"URL IS "+ url);
        FileLoader.with(this).load(url)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        File pdfFile= response.getBody();
                        pageSwipeDirection=1;
                        pdfview.fromFile(pdfFile).defaultPage(0).enableSwipe(true).onPageChange(new OnPageChangeListener() {
                            @Override
                            public void onPageChanged(int page, int pageCount) {
                                noOfPages = pdfview.getPageCount();
                                currentPage=pdfview.getCurrentPage()+1;
                                pageNo.setText("Page: "+currentPage+"/"+noOfPages);
                            }
                        }).load();
                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                    }
                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.bookreadmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scrolldirection:
                if (pageSwipeDirection==0){
                    pageSwipeDirection=1;
                    FileLoader.with(getApplicationContext()).load(url)
                            .asFile(new FileRequestListener<File>() {
                                @Override
                                public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                    File pdfFile = response.getBody();
                                    pdfview.fromFile(pdfFile).defaultPage(currentPage).onPageChange(new OnPageChangeListener() {
                                        @Override
                                        public void onPageChanged(int page, int pageCount) {
                                            noOfPages = pdfview.getPageCount();
                                            currentPage = pdfview.getCurrentPage()+1;
                                            pageNo.setText("Page: "+currentPage+"/"+noOfPages);
                                        }
                                    }).load();
                                }

                                @Override
                                public void onError(FileLoadRequest request, Throwable t) {
                                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                                }
                            });
                }
                else{
                    pageSwipeDirection=0;
                    FileLoader.with(getApplicationContext()).load(url)
                            .asFile(new FileRequestListener<File>() {
                                @Override
                                public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                    File pdfFile = response.getBody();
                                    pdfview.fromFile(pdfFile).swipeVertical(true).defaultPage(currentPage).onPageChange(new OnPageChangeListener() {
                                        @Override
                                        public void onPageChanged(int page, int pageCount) {
                                            noOfPages = pdfview.getPageCount();
                                            currentPage = pdfview.getCurrentPage()+1;
                                            pageNo.setText("Page: "+currentPage+"/"+noOfPages);
                                        }
                                    }).load();
                                }

                                @Override
                                public void onError(FileLoadRequest request, Throwable t) {
                                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                                }
                            });
                }
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
                                    Toast.makeText(getApplicationContext(),"Please enter a page number", Toast.LENGTH_SHORT).show();
                                } else {
                                    final int pageNumber=Integer.parseInt(pageNo1);
                                    FileLoader.with(getApplicationContext()).load(url)
                                            .asFile(new FileRequestListener<File>() {
                                                @Override
                                                public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                                    File pdfFile = response.getBody();
                                                    pdfview.fromFile(pdfFile).defaultPage(pageNumber).onPageChange(new OnPageChangeListener() {
                                                        @Override
                                                        public void onPageChanged(int page, int pageCount) {
                                                            noOfPages = pdfview.getPageCount();
                                                            currentPage=pdfview.getCurrentPage()+1;
                                                            pageNo.setText("Page: "+currentPage+"/"+noOfPages);
                                                        }
                                                    }).load();
                                                }

                                                @Override
                                                public void onError(FileLoadRequest request, Throwable t) {
                                                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                                                }
                                            });
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
}