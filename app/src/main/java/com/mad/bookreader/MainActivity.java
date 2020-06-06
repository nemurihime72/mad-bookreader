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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

        //Call the recyclerView function
        Log.v(TAG,"Displaying recyclerview of book items");
        recyclerFunction(listBooks);


    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getScreenWidth() {
        Log.v(TAG,"Screen width : "+(Resources.getSystem().getDisplayMetrics().widthPixels)/130);
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private void recyclerFunction(List<importedBooks> bList) {
        //function to display recyclerview
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
                //intent to import only pdf
                Intent intent = new Intent().setType("application/pdf").setAction(Intent.ACTION_GET_CONTENT);
                //starts that intent
                startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        //checks if requestCode and resultCode matches from above intent
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            String fileName;
            //get Uri from data passed from intent
            final Uri selectedFile = data.getData();
            //convert Uri to string for use when adding as object to list
            final String selectedFileString = selectedFile.toString();
            Log.v(TAG,selectedFileString);
            /*try {
                File file = new File(selectedFile.getPath());
                final String[] split = file.getPath().split(":");
                Log.v(TAG,file.getPath());
                String filePath = split[1];
                File pdfFile = new File(filePath);
                fileName = pdfFile.getName();
                Log.v(TAG, "File name: " + fileName);
            } catch (Exception e) {
                fileName = selectedFileString.substring(selectedFileString.lastIndexOf("%2F")+3);
                Log.v(TAG, "File name: " + fileName);
            }*/
            fileName = getFileName(selectedFile);
            Log.v(TAG,fileName);


            Log.v(TAG,"Alert dialog to prompt for book title creating");
            //alert dialog to prompt to edit name if wanted
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
                            Bitmap thumbnail = getCover(selectedFile);
                            importedBooks book = new importedBooks(titleName, thumbnail, selectedFileString);
                            listBooks.add(book);
                            recyclerFunction(listBooks);
                        }
                    });
            AlertDialog alert=builder.create();
            Log.v(TAG,"Alert dialog successfully created");
            alert.show();
            //toast to show import was successful
            Toast.makeText(getApplicationContext(), "Imported " + fileName + " successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap getCover(Uri uri) {
        PdfiumCore pdfiumCore = new PdfiumCore(getApplicationContext()); //use PdfiumCore to render bitmap
        int pageNum = 0; //use first page of pdf
        try {
            //convert Uri to ParcelFileDescriptor
            ParcelFileDescriptor fd = getApplicationContext().getContentResolver().openFileDescriptor(uri, "r");
            //make new PdfDocument object from the above ParcelFileDescriptor
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            //opens the first page to render
            pdfiumCore.openPage(pdfDocument, pageNum);
            //set width of page to be rendered
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNum);
            //set height of page to be rendered
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum);

            //create new Bitmap to render at quality of RGB_565 so it uses lesser resources
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            //renders bitmap
            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNum, 0, 0, width, height);
            //closes document
            pdfiumCore.closeDocument(pdfDocument);
            //returns rendered bitmap
            return bitmap;
        } catch (Exception ex) {
            Log.v(TAG, "Error occurred at openPdf finding bitmap");
            ex.printStackTrace(); //print error if any
            //if no image found due to error, will use the no image found drawable
            //first convert the drawable to bitmap
            Bitmap noImage = BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
            return noImage; //returns bitmap of no image found
        }

    }

    public String getFileName(Uri uri) {
        String result = null;
        //check if Uri starts with content://
        if (uri.getScheme().equals("content")) {
            //uses cursor to move through the resolved Uri
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    //result will be file name retrieved from cursor
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            //if result is null, get path from uri, split by last '/' and get the string of text after the last '/'
            result = uri.getPath();
            int split = result.lastIndexOf('/');
            if (split != -1) {
                result = result.substring(split + 1);
            }
        }
        return result;
    }
}
