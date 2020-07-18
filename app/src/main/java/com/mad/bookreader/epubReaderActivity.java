package com.mad.bookreader;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONObject;

import in.nashapp.epublibdroid.EpubReaderView;
import nl.siegmann.epublib.epub.EpubReader;

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

    Toolbar epubBar;
    int epubBarHeight, AnimDuration = 600;
    ValueAnimator mVaActionBar;

    SharedPreferences sharedPreferences;
    public static final String MY_PREFS = "epubDarkModePrefs";
    public static final String KEY_ISDARKMODE = "isDarkMode";

    int pageNo;
    public static int pageLastRead;
    public static int chapterLastRead;
    public static int columnID;
    BookDBHandler dbHandler;

    final static String TAG = "epubreader";
    boolean epubBarShowing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_reader);
        String epubFilePath = this.getIntent().getStringExtra("Bookpath");
        String bookName = this.getIntent().getStringExtra("BookName");
        columnID = Integer.parseInt(this.getIntent().getStringExtra("id"));
        dbHandler = new BookDBHandler(this, null, null, 1);
        pageLastRead = dbHandler.lastPage(columnID);
        chapterLastRead = dbHandler.lastChapter(columnID);


        sharedPreferences = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        epubBar = (Toolbar) findViewById(R.id.epub_read_bar);
        setSupportActionBar(epubBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        context = this;

        getSupportActionBar().setTitle(bookName);

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

        Log.v(TAG, epubFilePath);
        epubReaderView.OpenEpubFile(epubFilePath);
        epubReaderView.GotoPosition(chapterLastRead, (float) 0);

        if (sharedPreferences.getBoolean(KEY_ISDARKMODE, true)) {
            epubReaderView.SetTheme(epubReaderView.THEME_DARK);
        } else {
            epubReaderView.SetTheme(epubReaderView.THEME_LIGHT);
        }
        epubReaderView.setEpubReaderListener(new EpubReaderView.EpubReaderListener() {
            @Override
            public void OnPageChangeListener(int ChapterNumber, int PageNumber, float ProgressStart, float ProgressEnd) {
                Log.v(TAG, "page change: chapter: " + ChapterNumber + " pageno: " + PageNumber);
            }

            @Override
            public void OnChapterChangeListener(int ChapterNumber) {
                Log.v(TAG, "Chapter change: " + ChapterNumber + " ");
                dbHandler.updateLastChapter(columnID, ChapterNumber);
            }

            @Override
            public void OnTextSelectionModeChangeListner(Boolean mode) {
                Log.v(TAG, "Text selection mode: " + mode + " ");
                if (mode) {
                    getSupportActionBar().hide();
                    ViewGroup.LayoutParams params = epubReaderView.getLayoutParams();
                    params.height = dpToPx(750, getApplicationContext());
                    epubReaderView.setLayoutParams(params);
                    epubReaderView.requestLayout();
                    bottomContextualBar.setVisibility(View.VISIBLE);
                } else {
                    getSupportActionBar().show();
                    ViewGroup.LayoutParams params = epubReaderView.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    epubReaderView.setLayoutParams(params);
                    epubReaderView.requestLayout();
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
            }

            @Override
            public void OnSingleTap() {
                Log.v(TAG, "page tapped");
                if (epubBarShowing) {
                    if (epubBar != null) {
                        /*ViewGroup.LayoutParams params = epubReaderView.getLayoutParams();
                        params.height = dpToPx(680, getApplicationContext());
                        epubReaderView.setLayoutParams(params);
                        epubReaderView.requestLayout();*/
                        epubBar.animate().translationY(-epubBar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                        epubBarShowing = false;
                    }
                } else {
                    if (epubBar != null) {
                        /*ViewGroup.LayoutParams params = epubReaderView.getLayoutParams();
                        params.height = dpToPx(630, getApplicationContext());
                        epubReaderView.setLayoutParams(params);
                        epubReaderView.requestLayout();*/
                        epubBar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                        epubBarShowing = true;
                    }
                }
            }
        });
        showTOC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epubReaderView.ListChaptersDialog(epubReaderView.GetTheme());
            }
        });
        changeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (epubReaderView.GetTheme() == epubReaderView.THEME_LIGHT) {
                    epubReaderView.SetTheme(epubReaderView.THEME_DARK);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(KEY_ISDARKMODE, true);
                    editor.apply();
                } else {
                    epubReaderView.SetTheme(epubReaderView.THEME_LIGHT);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(KEY_ISDARKMODE, false);
                    editor.apply();
                }
            }
        });
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
        selectUnderline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epubReaderView.ProcessTextSelection();;
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
        selectStrikethru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epubReaderView.ProcessTextSelection();;
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
        selectCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epubReaderView.ProcessTextSelection();;
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
        selectSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epubReaderView.ProcessTextSelection();;
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
        selectShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epubReaderView.ProcessTextSelection();;
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
        selectExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epubReaderView.ExitSelectionMode();
            }
        });
    }

    protected void hideActionBar() {
        if (epubBarHeight == 0) {
            epubBarHeight = epubBar.getHeight();
        }

        if (mVaActionBar != null && mVaActionBar.isRunning()) {
            return;
        }
        mVaActionBar = ValueAnimator.ofInt(epubBarHeight, 0);
        mVaActionBar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ((AppBarLayout.LayoutParams)epubBar.getLayoutParams()).height = (Integer)animation.getAnimatedValue();
                epubBar.requestLayout();

            }
        });
        mVaActionBar.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (getSupportActionBar() != null) {
                    getSupportActionBar().hide();
                }
            }
        });

        mVaActionBar.setDuration(AnimDuration);
        mVaActionBar.start();
    }

    protected void showActionBar() {
        if (mVaActionBar != null && mVaActionBar.isRunning()) {
            return;
        }

        mVaActionBar = ValueAnimator.ofInt(0, epubBarHeight);
        mVaActionBar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ((AppBarLayout.LayoutParams)epubBar.getLayoutParams()).height = (Integer)animation.getAnimatedValue();
                epubBar.requestLayout();
            }
        });

        mVaActionBar.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

                if (getSupportActionBar() != null) {
                    getSupportActionBar().show();
                }
            }
        });
        mVaActionBar.setDuration(AnimDuration);
        mVaActionBar.start();
    }

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
        Log.v(TAG, "Destroying!");
    }
}