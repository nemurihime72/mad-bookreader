package com.mad.bookreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.PathUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;


public class MainActivity extends AppCompatActivity {

    final static String TAG = "MainActivity.java";

    List<importedBooks> listBooks = new ArrayList<>();
    private recyclerAdaptor rAdaptor;
    SharedPreferences sharedPreferences;
    SharedPreferences sortingPreferences;
    int checkedItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        sharedPreferences=getSharedPreferences("nightModePrefs", Context.MODE_PRIVATE);
        checkNightModeSwitch();
        /*To add my own toolbar instead of the default*/
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        Log.v(TAG,"Top toolbar set");

        //Call the recyclerView function
        Log.v(TAG,"Displaying recyclerview of book items");
        recyclerFunction(listBooks);

        Log.v(TAG, "Displaying previously imported books");
        displayBooks(listBooks);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "permission to read storage granted");

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "permission to read storage denied");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    //Check if nightmode is turned on from the sharedpreference
    public void checkNightModeSwitch() {
        if (sharedPreferences.getBoolean("isNightMode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    // To convert from px measurement to dp
    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    // Get current screen width in px
    public static int getScreenWidth() {
        Log.v(TAG,"Screen width in px : "+(Resources.getSystem().getDisplayMetrics().widthPixels));
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private void recyclerFunction(List<importedBooks> bList) {
        //function to display recyclerview
        RecyclerView rView = (RecyclerView) findViewById(R.id.bookCycler);
        rAdaptor = new recyclerAdaptor(bList);
        int spanCount=pxToDp(getScreenWidth())/120;
        Log.v(TAG, "span count is " + spanCount);
        GridLayoutManager gLayoutManager = new GridLayoutManager(this, spanCount);
        rView.setLayoutManager(gLayoutManager);
        rView.setAdapter(rAdaptor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView)searchItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                rAdaptor.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //Dialog to choose sorting type
    public Dialog onCreateSortingDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        String [] options = {"Title", "Type"};

        dialogBuilder.setTitle("Sort by")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case 0:
                                OrderDialog().show();
                                break;
                            case 1:
                                TypeDialog().show();
                                break;
                        }
                    }
                });
        return dialogBuilder.create();
    }

    //Dialog to sort with type of file
    public Dialog TypeDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Filter");

        //Sets dialog options
        String[] type = {"Online", "EPUB", "PDF"};
        boolean[] checkedItems = {false, false, false};
        dialogBuilder.setTitle("Sort by")
                .setMultiChoiceItems(type, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                }
            });
        dialogBuilder.setNegativeButton("Cancel", null);

        //Sets "OK" button
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] type = {"online", "epub", "pdf"};
                List<importedBooks> filteredList = new ArrayList<>();
                ListView lv = ((AlertDialog) dialog).getListView();
                SparseBooleanArray posList= lv.getCheckedItemPositions();
                Log.v(TAG,"Confirm type");

                for (int i = 0; i < lv.getCount(); i++) {
                    Log.v(TAG, "" + posList.valueAt(i));
                    if (posList.get(i)){
                        for(int k = 0; k < listBooks.size(); k++){
                            if(listBooks.get(k).getFileType().equals(type[i])){
                                Log.v(TAG, "Filtered");
                                filteredList.add(listBooks.get(k));
                            }
                            else{
                                continue;
                            }
                        }
                    }
                    else{
                        continue;
                    }
                }
                recyclerFunction(filteredList);
            }
        });

        return dialogBuilder.create();

    }

    //Dialog to sort titles by ascending and descending
    public Dialog OrderDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Sort by");
        String[] order = {"Ascending", "Descending"};
        dialogBuilder.setSingleChoiceItems(order, checkedItem, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "Dialog Clicked: " + which);
                checkedItem = which;
                Log.v(TAG, "" + checkedItem);
            }
        });


        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(checkedItem == 0) {
                        Collections.sort(listBooks, importedBooks.bookComparator);
                        Log.v(TAG,"Sorted in ascending order");
                }
                else
                {
                        Collections.sort(listBooks, importedBooks.bookComparatorReversed);
                        Log.v(TAG,"Sorted in descending order");
                }
                recyclerFunction(listBooks);
            }
        });

        dialogBuilder.setNegativeButton("Cancel", null);


        return dialogBuilder.create();
    }

    public Dialog importDialog() {
        AlertDialog.Builder importBuilder = new AlertDialog.Builder(this);
        String[] options = {"Online PDF", "PDF/EPUB"};

        importBuilder.setTitle("Import books")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                onlineDialog().show();
                                break;
                            case 1:
                                //check if read storage permission has been granted. if not, prompt for permission
                                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    Toast.makeText(getApplicationContext(), "Storage access is needed to read books", Toast.LENGTH_LONG).show();
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                                } else {
                                    //intent to import only pdf
                                    Intent intent = new Intent().setType("*/*").setAction(Intent.ACTION_OPEN_DOCUMENT);
                                    String[] extraMimeTypes = {"application/pdf", "application/epub+zip"};
                                    intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes);
                                    intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                                    //starts that intent
                                    startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
                                }
                                break;
                        }
                    }
                });
        return importBuilder.create();
    }

    public Dialog onlineDialog(){
        final BookDBHandler db = new BookDBHandler(this,null, null, 1);
        android.app.AlertDialog.Builder onlinebuilder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialogue, null);
        final EditText onlineUrl = (EditText) view.findViewById(R.id.fileTitle);
        ColorStateList colorStateList = ContextCompat.getColorStateList(this, R.color.textcolor);
        ColorStateList colorStateList2 = ContextCompat.getColorStateList(this, R.color.hintcolor);
        onlineUrl.setHint("Enter URL here");
        onlineUrl.setTextColor(colorStateList);
        onlineUrl.setHintTextColor(colorStateList2);
        onlinebuilder.setView(view).setTitle("Import online pdf")
                .setPositiveButton("Import", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = onlineUrl.getText().toString();
                        Log.v(TAG, "url is " + url);
                        if (url.equals("") || url.equals(null)) {
                            Toast.makeText(getApplicationContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                        } else {
                            int id = 0;
                            if (db.noOfRows() == 0) {
                                id = 0;
                            } else {
                                id = db.lastRowId() + 1;
                            }
                            Bitmap thumbnail = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pdficon);
                            importedBooks book = new importedBooks(id, url, thumbnail, url, "online");
                            db.addBook(id, url, url, "online");
                            listBooks.add(book);
                            recyclerFunction(listBooks);
                        }
                    }
                });
        return onlinebuilder.create();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final BookDBHandler db = new BookDBHandler(this,null, null, 1);
        switch (item.getItemId()) {
            case R.id.deleteallbooks:
                if (listBooks.size()==0){
                    Log.v(TAG,"No books to delete");
                    Toast.makeText(this,"No books imported",Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.v(TAG,"Delete all books");
                    androidx.appcompat.app.AlertDialog.Builder delAlert=new androidx.appcompat.app.AlertDialog.Builder(this);
                    delAlert.setMessage("Would you like to delete all the books?");
                    delAlert.setCancelable(true);
                    delAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.v(TAG,"Deleting all book");
                            //clear db of all entries
                            db.deleteallBooks();
                            //clear recyclerview list of any books
                            listBooks.clear();
                            //reload recyclerview with 0 books
                            displayBooks(listBooks);
                            Log.v(TAG,"All books deleted");
                            Toast.makeText(getApplicationContext(),"All books deleted",Toast.LENGTH_SHORT).show();
                        }
                    });
                    delAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.v(TAG,"User choose not to delete all books");
                            Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                        }
                    });
                    androidx.appcompat.app.AlertDialog alert = delAlert.create();
                    alert.setTitle("Delete books");
                    alert.show();
                }
                return true;

            case R.id.action_settings:
                Log.v(TAG,"Settings selected");
                Intent settingsIntent=new Intent(MainActivity.this,settingsMain.class);
                startActivity(settingsIntent);
                return true;

            case R.id.action_import:
                Log.v(TAG,"Import files selected");
                importDialog().show();
                return true;

            case R.id.action_sort:
                Log.v(TAG,"Sorting selected");
                onCreateSortingDialog().show();
                return true;

            case R.id.action_about:
                Log.v(TAG, "About page selected");
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void displayBooks(List<importedBooks> bookList){
        List<List<String>> storedBooks = new ArrayList<>();
        importedBooks book;
        BookDBHandler db = new BookDBHandler(this, null, null, 1);
        Log.v(TAG, "Loading books in DB");
        //load books from db
        storedBooks = db.startBooks(storedBooks);
        Log.v(TAG, "storeBooks books: " + storedBooks.get(1).size());
        for (int i = 0; i < storedBooks.get(1).size(); i++){
            Uri uri = Uri.parse(storedBooks.get(2).get(i));
            if (storedBooks.get(3).get(i).equals("online")){
                //for online pdf, use pdf picture for thumbnail
                Bitmap image=BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.pdficon);
                book = new importedBooks(Integer.parseInt(storedBooks.get(0).get(i)),storedBooks.get(1).get(i), image, storedBooks.get(2).get(i), storedBooks.get(3).get(i));
                bookList.add(book);
                Log.v(TAG, "Book added: " + book.getTitle());
            }
            else if (storedBooks.get(3).get(i).equals("pdf")){
                Bitmap image =  getPdfCover(uri);
                book = new importedBooks(Integer.parseInt(storedBooks.get(0).get(i)),storedBooks.get(1).get(i), image, storedBooks.get(2).get(i), storedBooks.get(3).get(i));
                bookList.add(book);
                Log.v(TAG, "Book added: " + book.getTitle());
            } else if (storedBooks.get(3).get(i).equals("epub")) {
                Bitmap image = getEpubCover(uri);
                book = new importedBooks(Integer.parseInt(storedBooks.get(0).get(i)),storedBooks.get(1).get(i), image, storedBooks.get(2).get(i), storedBooks.get(3).get(i));
                bookList.add(book);
                Log.v(TAG, "Book added: " + book.getTitle());
            }


        }
        recyclerFunction(bookList);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //checks if requestCode and resultCode matches from above intent
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            final String fileName;
            //get Uri from data passed from intent
            final Uri selectedFile = data.getData();
            final String fileType = getFileType(selectedFile);
            Log.v(TAG, "file type is " + fileType);
            //check if filetype is valid or not, if invalid, toast message invalid.
            if (!fileType.equals("epub") && !fileType.equals("pdf")) {
                Toast.makeText(this, "Please select a pdf or epub file", Toast.LENGTH_SHORT).show();
            } else {
                getContentResolver().takePersistableUriPermission(selectedFile, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //convert Uri to string for use when adding as object to list
                final String selectedFileString = selectedFile.toString();
                Log.v(TAG, selectedFileString);
                fileName = getFileName(selectedFile);
                Log.v(TAG, fileName);
                //final int id = 0;
                final BookDBHandler db = new BookDBHandler(this, null, null, 1);

                Log.v(TAG, "Alert dialog to prompt for book title creating");
                //alert dialog to prompt to edit name if wanted
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = LayoutInflater.from(this).inflate(R.layout.dialogue, null);
                final EditText editTextTitle = (EditText) view.findViewById(R.id.fileTitle);
                editTextTitle.setHint(fileName);
                editTextTitle.setText(fileName);
                builder.setView(view).setTitle("Set title for book")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String titleName = editTextTitle.getText().toString();
                                if (titleName.equals("") || titleName.equals(null)) {
                                    Toast.makeText(getApplicationContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (fileType.equals("pdf")) {
                                        int id = 0;
                                        if (db.noOfRows() == 0) {
                                            id = 0;
                                        } else {
                                            //get last id number and adds 1 (increment)
                                            id = db.lastRowId() + 1;
                                        }
                                        Log.v("TESTEST","LAST ROW ID: "+db.lastRowId());
                                        Log.v("TESTTEST","ID IS "+id);
                                        //get thumbnail for display
                                        Bitmap thumbnail = getPdfCover(selectedFile);
                                        //create new book object
                                        importedBooks book = new importedBooks(id, titleName, thumbnail, selectedFileString, fileType);
                                        //add book data to db
                                        db.addBook(id, titleName, selectedFileString, fileType);
                                        //add book to list of books
                                        listBooks.add(book);
                                        //reload recyclerview with updated list of books
                                        recyclerFunction(listBooks);
                                    } else if (fileType.equals("epub")) {
                                        int id = 0;
                                        if (db.noOfRows() == 0) {
                                            id = 0;
                                        } else {
                                            //get last id number and adds 1 (increment)
                                            id = db.lastRowId() + 1;
                                        }
                                        //get thumbnail for display
                                        Bitmap thumbnail = getEpubCover(selectedFile);
                                        //create new book object
                                        importedBooks book = new importedBooks(id, titleName, thumbnail, selectedFileString, fileType);
                                        //add book data to db
                                        db.addBook(id, titleName, selectedFileString, fileType);
                                        //add book to list of books
                                        listBooks.add(book);
                                        //reload recyclerview with updated list of books
                                        recyclerFunction(listBooks);

                                    }


                                }
                            }
                        });
                AlertDialog alert = builder.create();
                Log.v(TAG, "Alert dialog successfully created");
                alert.show();
                //toast to show import was successful
                Toast.makeText(getApplicationContext(), "Imported " + fileName + " successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap getPdfCover(Uri uri) {
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
            //return converted drawable bitmap
            return BitmapFactory.decodeResource(getResources(), R.drawable.no_image); //returns bitmap of no image found
        }

    }

    //write an inputstream to a file
    public void writeFile(InputStream in, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

      public Bitmap getEpubCover(Uri uri) {
        //gets file name
        String fileName = getFileName(uri);
        try {
            //open uri in an inputstream
            InputStream inputStream = getContentResolver().openInputStream(uri);
            //create new file in cache in folder of file name
            File file = new File(getCacheDir().getAbsolutePath() + "/" + fileName);
            //writes file to the cache using inputstream
            writeFile(inputStream, file);
            //get filepath of the epub file written to cache
            String filePath = file.getAbsolutePath();
            //get cover image using filepath
            return loadEpubCover(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //if file not found, return no image
            return BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
        }

    }
    public Bitmap loadEpubCover(String filePath) {
        try {
            Log.v(TAG, filePath);
            //load file from filepath
            File epubFile = new File(filePath);
            //load file into inputstream
            InputStream epubInputStream = new FileInputStream(epubFile);
            //load book from inputStream
            Book book = (new EpubReader()).readEpub(epubInputStream);
            //get bitmap of cover image
            Bitmap coverImage = BitmapFactory.decodeStream((book.getCoverImage()).getInputStream());
            Log.i("epublib", "cover image is: " + coverImage.getWidth() + " by " + coverImage.getHeight() + " pixels");

            return coverImage;

        } catch (Exception e) {
            e.printStackTrace();
            //if no image found or exception is caught, bitmap returned is no image
            return BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
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
    //delete files in cache
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public String getFileType(Uri uri) {
        //get file name of uri
        String fileName = getFileName(uri);
        //returns substring of filename that contains the file extension type (epub or pdf)
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
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
        //restart recyclerview when returning from read activity to show progress update
        recyclerFunction(listBooks);
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
        deleteCache(this);
        Log.v(TAG, "Destroying!");
    }
}
