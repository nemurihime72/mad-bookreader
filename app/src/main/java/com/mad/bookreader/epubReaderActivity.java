package com.mad.bookreader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import in.nashapp.epublibdroid.EpubReaderView;

public class epubReaderActivity extends AppCompatActivity {
    EpubReaderView epubReaderView;
    ImageView selectCopy;
    ImageView selectHighlight;
    ImageView selectUnderline;
    ImageView selectStrikethru;
    ImageView selectSearch;
    ImageView selectShare;
    ImageView selectExit;
    ImageView showTOC;
    ImageView changeTheme;
    LinearLayout bottomContextualBar;
    Context context;

    String bookName;
    Toolbar epubBar;

    SharedPreferences sharedPreferences;
    public static final String MY_PREFS = "epubDarkModePrefs";
    public static final String KEY_ISDARKMODE = "isDarkMode";

    public static float progressLastRead;
    public static int chapterLastRead;
    public static int columnID;
    BookDBHandler dbHandler;

    final static String TAG = "epubreader";

    String epubUri;
    Uri uri;

    boolean epubBarShowing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_reader);
        //get book name and uri string from intent
        bookName = this.getIntent().getStringExtra("BookName");
        epubUri = this.getIntent().getStringExtra("Bookpath");
        //get column id from intent
        columnID = Integer.parseInt(this.getIntent().getStringExtra("id"));
        //get chapter last read and progress from db
        dbHandler = new BookDBHandler(this, null, null, 1);
        chapterLastRead = dbHandler.lastChapter(columnID);
        progressLastRead = dbHandler.lastProgress(columnID);

        //set up action bar
        epubBar = (Toolbar) findViewById(R.id.epub_read_bar);
        setSupportActionBar(epubBar);
        //set title of action bar to the name of book
        getSupportActionBar().setTitle(bookName);
        //set up back button of action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context = this;

        epubReaderView = findViewById(R.id.epub_reader);
        selectCopy = findViewById(R.id.select_copy);
        selectHighlight = findViewById(R.id.select_highlight);
        selectUnderline = findViewById(R.id.select_underline);
        selectStrikethru = findViewById(R.id.select_strikethrough);
        selectSearch = findViewById(R.id.select_search);
        selectShare = findViewById(R.id.select_share);
        selectExit = findViewById(R.id.select_exit);
        showTOC = findViewById(R.id.show_toc);
        changeTheme = findViewById(R.id.change_theme);
        bottomContextualBar = findViewById(R.id.bottom_contextual_bar);

        //parse string of uri from intent
        uri = Uri.parse(epubUri);
        Log.v(TAG, "uri : " + uri);
        Log.v(TAG, "uri path: " + uri.getPath());
        try {
            //open uri in inputstream (uri may not point to a file so inputstream is opened to read contents)
            InputStream inputStream = getContentResolver().openInputStream(uri);
            //create new file in cache
            File file = new File(getCacheDir().getAbsolutePath() + "/" + bookName);
            //write contents of epub file to the file in cache
            writeFile(inputStream, file);
            //get filepath of the epub file in cache
            String filePath = file.getAbsolutePath();
            Log.v(TAG, "filepath is: " + filePath);
            Log.v(TAG, "chapter list: " + epubReaderView.ChapterList);
            //open epub file from cached file
            epubReaderView.OpenEpubFile(filePath);
            //start from last read chapter and progress
            epubReaderView.GotoPosition(chapterLastRead, progressLastRead);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "File not found!", Toast.LENGTH_SHORT).show();
        }


        //get shared preferences for dark mode
        sharedPreferences = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        Log.v(TAG, "is dark mode?: " + sharedPreferences.getBoolean(KEY_ISDARKMODE, false));
        //if dark mode in shared preference is false, set theme to light, else set theme to dark
        if (!sharedPreferences.getBoolean(KEY_ISDARKMODE, false)) {
            epubReaderView.SetTheme(epubReaderView.THEME_LIGHT);
        } else {
            epubReaderView.SetTheme(epubReaderView.THEME_DARK);

        }
        epubReaderView.setEpubReaderListener(new EpubReaderView.EpubReaderListener() {
            @Override
            public void OnPageChangeListener(int ChapterNumber, int PageNumber, float ProgressStart, float ProgressEnd) {
                Log.v(TAG, "page change: chapter: " + ChapterNumber + " pageno: " + PageNumber);
                Log.v(TAG, "progress start: " + ProgressStart + " | progress end: " + ProgressEnd);
                //update progress of chapter in db
                dbHandler.updateLastProgress(columnID, ProgressStart);
            }

            @Override
            public void OnChapterChangeListener(int ChapterNumber) {
                Log.v(TAG, "Chapter change: " + ChapterNumber + " ");
                //update chapter number last read in db
                dbHandler.updateLastChapter(columnID, ChapterNumber);
            }

            @Override
            public void OnTextSelectionModeChangeListner(Boolean mode) {
                Log.v(TAG, "Text selection mode: " + mode + " ");
                if (mode) {
                    //if text selected, hide action bar
                    getSupportActionBar().hide();
                    //increase height of the reader view to top of original height of action bar
                    ViewGroup.LayoutParams params = epubReaderView.getLayoutParams();
                    params.height = pxToDp(750, getApplicationContext());
                    epubReaderView.setLayoutParams(params);
                    //update reader view height
                    epubReaderView.requestLayout();
                    //show extra options on bottom bar
                    bottomContextualBar.setVisibility(View.VISIBLE);
                } else {
                    //if text is not selected, show action bar
                    getSupportActionBar().show();
                    //set reader view height to match parent
                    ViewGroup.LayoutParams params = epubReaderView.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    epubReaderView.setLayoutParams(params);
                    //update reader view height
                    epubReaderView.requestLayout();
                    //stop displaying extra options on bottom bar
                    bottomContextualBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void OnLinkClicked(String url) {
                Log.v(TAG, "link clicked: " + url + " ");
            }

            @Override
            public void OnBookStartReached() {
                Log.v(TAG, "Start reached");
            }

            @Override
            public void OnBookEndReached() {
                Log.v(TAG, "end reached");
                //if book end is reached, tell user progress is reset
                Toast.makeText(context, "End of book reached, progress resetting", Toast.LENGTH_SHORT).show();
                //save in db last chapter read is 0
                dbHandler.updateLastChapter(columnID, 0);
                //save in db progress of chapter is 0
                dbHandler.updateLastProgress(columnID, 0);
                //when book is loaded up next time, last chapter and progress read will be 0, effectively resetting progress
            }

            @Override
            public void OnSingleTap() {
                Log.v(TAG, "page tapped");
                if (epubBarShowing) {
                    if (epubBar != null) {
                        //if action bar showing, on tap hide action bar, show animation
                        epubBar.animate().translationY(-epubBar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                        //set action bar bool to not showing
                        epubBarShowing = false;
                    }
                } else {
                    if (epubBar != null) {
                        //if action bar is hidden, on tap show action bar, show animation
                        epubBar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                        //set action bar bool to showing
                        epubBarShowing = true;
                    }
                }
            }
        });
        //show table of contents on click
        showTOC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epubReaderView.ListChaptersDialog(epubReaderView.GetTheme());
            }
        });
        //toggle dark mode
        changeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (epubReaderView.GetTheme() == epubReaderView.THEME_LIGHT) {
                    epubReaderView.SetTheme(epubReaderView.THEME_DARK);
                    //if dark mode toggled, save in shared preferences dark mode = on
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(KEY_ISDARKMODE, true);
                    editor.apply();
                } else {
                    //if dark mode is off, save in shared preferences dark mode = off
                    epubReaderView.SetTheme(epubReaderView.THEME_LIGHT);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(KEY_ISDARKMODE, false);
                    editor.apply();
                }
            }
        });
        //highlight selected text
        selectHighlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epubReaderView.ProcessTextSelection();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String SelectedText = "";
                        int ChNo = -1;
                        String DataString = "";
                        try {
                            JSONObject response = new JSONObject(epubReaderView.getSelectedText());
                            SelectedText = response.getString("SelectedText");
                            ChNo = response.getInt("ChapterNumber");
                            DataString = response.getString("DataString");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (ChNo >=0 && !SelectedText.equals("") && !DataString.equals("")) {
                            //Save ChapterNumber,DataString,Color,AnnotateMethod,BookLocation etc in database/Server to recreate highlight
                            if (ChNo == epubReaderView.GetChapterNumber())//Verify ChanpterNumber and BookLocation before suing highlight
                                epubReaderView.Annotate(DataString,epubReaderView.METHOD_HIGHLIGHT,"#ef9a9a");
                        }
                        epubReaderView.ExitSelectionMode();
                    }
                }, 100);
            }
        });
        //underline selected text
        selectUnderline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epubReaderView.ProcessTextSelection();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String SelectedText = "";
                        int ChNo = -1;
                        String DataString = "";
                        try {
                            JSONObject response = new JSONObject(epubReaderView.getSelectedText());
                            SelectedText = response.getString("SelectedText");
                            ChNo = response.getInt("ChapterNumber");
                            DataString = response.getString("DataString");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (ChNo >=0 && !SelectedText.equals("") && !DataString.equals("")) {
                            //Save ChapterNumber,DataString,Color,AnnotateMethod,BookLocation etc in database/Server to recreate highlight
                            if (ChNo == epubReaderView.GetChapterNumber())//Verify ChanpterNumber and BookLocation before suing highlight
                                epubReaderView.Annotate(DataString,epubReaderView.METHOD_UNDERLINE,"#ef9a9a");
                        }
                        epubReaderView.ExitSelectionMode();
                    }
                }, 100);
            }
        });
        //strikethrough selected text
        selectStrikethru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epubReaderView.ProcessTextSelection();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String SelectedText = "";
                        int ChNo = -1;
                        String DataString = "";
                        try {
                            JSONObject response = new JSONObject(epubReaderView.getSelectedText());
                            SelectedText = response.getString("SelectedText");
                            ChNo = response.getInt("ChapterNumber");
                            DataString = response.getString("DataString");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (ChNo >=0 && !SelectedText.equals("") && !DataString.equals("")) {
                            //Save ChapterNumber,DataString,Color,AnnotateMethod,BookLocation etc in database/Server to recreate highlight
                            if (ChNo == epubReaderView.GetChapterNumber())//Verify ChanpterNumber and BookLocation before suing highlight
                                epubReaderView.Annotate(DataString,epubReaderView.METHOD_STRIKETHROUGH,"#ef9a9a");
                        }
                        epubReaderView.ExitSelectionMode();
                    }
                }, 100);
            }
        });
        //copy selected text to clipboard
        selectCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epubReaderView.ProcessTextSelection();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String SelectedText = "";
                        int ChNo = -1;
                        String DataString = "";
                        try {
                            JSONObject response = new JSONObject(epubReaderView.getSelectedText());
                            SelectedText = response.getString("SelectedText");
                            ChNo = response.getInt("ChapterNumber");
                            DataString = response.getString("DataString");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (ChNo >=0 && !SelectedText.equals("") && !DataString.equals("")) {
                            //get selected text and add to clipboard
                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            android.content.ClipData clipData = android.content.ClipData.newPlainText("Copied text", SelectedText);
                            clipboard.setPrimaryClip(clipData);
                            Toast.makeText(context, "Text Copied", Toast.LENGTH_SHORT).show();
                        }
                        epubReaderView.ExitSelectionMode();
                    }
                }, 100);
            }
        });
        //search in web browser selected text
        selectSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epubReaderView.ProcessTextSelection();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String SelectedText = "";
                        int ChNo = -1;
                        String DataString = "";
                        try {
                            JSONObject response = new JSONObject(epubReaderView.getSelectedText());
                            SelectedText = response.getString("SelectedText");
                            ChNo = response.getInt("ChapterNumber");
                            DataString = response.getString("DataString");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (ChNo >=0 && !SelectedText.equals("") && !DataString.equals("")) {
                            if (SelectedText.length() < 120) {
                                //open intent to a browser to a url to search selected word
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/search?q=" + SelectedText));
                                startActivity(browserIntent);
                            } else {
                                Toast.makeText(context, "Selected text is too long to search", Toast.LENGTH_SHORT).show();
                            }
                        }
                        epubReaderView.ExitSelectionMode();
                    }
                }, 100);
            }
        });
        //share selected word
        selectShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epubReaderView.ProcessTextSelection();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String SelectedText = "";
                        int ChNo = -1;
                        String DataString = "";
                        try {
                            JSONObject response = new JSONObject(epubReaderView.getSelectedText());
                            SelectedText = response.getString("SelectedText");
                            ChNo = response.getInt("ChapterNumber");
                            DataString = response.getString("DataString");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (ChNo >=0 && !SelectedText.equals("") && !DataString.equals("")) {
                            //open intent to share selected word
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared using MAD Book Reader");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, SelectedText);
                            startActivity(Intent.createChooser(shareIntent, "Share using"));
                        }
                        epubReaderView.ExitSelectionMode();
                    }
                }, 100);
            }
        });
        //exit selection of word
        selectExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epubReaderView.ExitSelectionMode();
            }
        });
    }
    //convert px to dp
    public static int pxToDp(int px, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) px * density);
    }
    //write to a file from inputstream
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

    //if back button pressed, return home
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        //clears cached files when activity is closed
        deleteCache(context);
        Log.v(TAG, "Destroying!");
    }
}