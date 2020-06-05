package com.mad.bookreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.PathUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "MainActivity.java";

    List<importedBooks> listBooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*To add my own toolbar instead of the default*/
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        Log.v(TAG,"Top toolbar set");


        //Create list and populate( populate To be removed)
        /*for (int i=0;i<50;i++){
        importedBooks b1 = new importedBooks("Manga", R.drawable.isla, "testpdf.pdf");
        listBooks.add(b1);
        }
        */
        
        /*importedBooks b1=new importedBooks("manga",R.drawable.isla,"kendo.pdf");
        listBooks.add(b1);*/



        //Call the recyclerView function
        Log.v(TAG,"Displaying recyclerview of book items");
        recyclerFunction(listBooks);


        //displayBooks(listsBooks);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getScreenWidth() {
        Log.v(TAG,"Screen width : "+(Resources.getSystem().getDisplayMetrics().widthPixels)/130);
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private void recyclerFunction(List<importedBooks> bList) {
        RecyclerView rview = (RecyclerView) findViewById(R.id.bookCycler);
        recyclerAdaptor rAdaptor = new recyclerAdaptor(bList);
        int spanCount=pxToDp(getScreenWidth())/130;
        GridLayoutManager gLayoutManager = new GridLayoutManager(this,spanCount);
        rview.setLayoutManager(gLayoutManager);
        rview.setAdapter(rAdaptor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.v(TAG,"Settings selected");
                Intent settingsIntent=new Intent(MainActivity.this,settingsMain.class);
                startActivity(settingsIntent);
                return true;


            case R.id.action_import:
                Log.v(TAG,"Import files selected");
                //intent to import files
                Intent intent = new Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*public void displayBooks(List<importedBooks> storedBooks){
        BookDBHandler db = new BookDBHandler(this, null, null, 1);
        db.startBooks(storedBooks);

    }*/



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        //checks if requestCode and resultCode matches from above intent
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            Uri selectedFile = data.getData();
            final String selectedFileString = selectedFile.toString();
            Log.v(TAG,selectedFileString);
            final String fileName =selectedFileString.substring(selectedFileString.lastIndexOf("%2F")+3);
            Log.v(TAG,fileName);


            Log.v(TAG,"Alert dialog to prompt for book title creating");
            android.app.AlertDialog.Builder builder=new AlertDialog.Builder(this);


            View view=LayoutInflater.from(this).inflate(R.layout.dialogue,null);
            final EditText editTextTitle = (EditText) view.findViewById(R.id.fileTitle);
            editTextTitle.setHint(fileName);
            editTextTitle.setText(fileName);
            builder.setView(view).setTitle("Set title for book")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String titleName=editTextTitle.getText().toString();
                            importedBooks book = new importedBooks(titleName, R.drawable.no_image, selectedFileString);
                            listBooks.add(book);
                            recyclerFunction(listBooks);
                        }
                    });
            AlertDialog alert=builder.create();
            Log.v(TAG,"Alert dialog successfully created");
            alert.show();


            //File file = new File(selectedFile.getPath());
            /*final String[] split = file.getPath().split(":");
            Log.v(TAG,file.getPath());
            String filePath = split[1];
            File pdfFile = new File(filePath);*/









           /* importedBooks book = new importedBooks(fileName, R.drawable.isla, pdfFile);
            listBooks.add(book);
            recyclerFunction(listBooks);*/

        }
    }
}
