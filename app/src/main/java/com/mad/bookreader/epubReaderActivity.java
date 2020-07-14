package com.mad.bookreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import in.nashapp.epublibdroid.EpubReaderView;
import nl.siegmann.epublib.epub.EpubReader;

public class epubReaderActivity extends AppCompatActivity {
    EpubReaderView epubReaderView;
    ImageView selectCopy;
    ImageView selectHighlight;
    ImageView selectUnderline;
    ImageView selectStrikethru;
    ImageView selectRead;
    ImageView selectSearch;
    ImageView selectShare;
    ImageView selectExit;
    ImageView readAloud;
    ImageView showTOC;
    ImageView changeTheme;
    LinearLayout bottomContextualBar;
    Context context;
    final static String TAG = "epubreader";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_reader);
        context = this;
        String epubFilePath = this.getIntent().getStringExtra("Bookpath");
        epubReaderView = findViewById(R.id.epub_reader);
        selectCopy = findViewById(R.id.select_copy);
        selectHighlight = findViewById(R.id.select_highlight);
        selectUnderline = findViewById(R.id.select_underline);
        selectStrikethru = findViewById(R.id.select_strikethrough);
        selectRead = findViewById(R.id.select_read);
        selectSearch = findViewById(R.id.select_search);
        selectShare = findViewById(R.id.select_share);
        selectExit = findViewById(R.id.select_exit);
        readAloud = findViewById(R.id.read_aloud);
        showTOC = findViewById(R.id.show_toc);
        changeTheme = findViewById(R.id.change_theme);
        Log.v(TAG, epubFilePath);
        epubReaderView.OpenEpubFile(epubFilePath);
        epubReaderView.GotoPosition(0, (float) 0);
        epubReaderView.setEpubReaderListener(new EpubReaderView.EpubReaderListener() {
            @Override
            public void OnPageChangeListener(int ChapterNumber, int PageNumber, float ProgressStart, float ProgressEnd) {
                Log.v(TAG, "page change: chapter: " + ChapterNumber + " pageno: " + PageNumber);
            }

            @Override
            public void OnChapterChangeListener(int ChapterNumber) {
                Log.v(TAG, "Chapter change: " + ChapterNumber + " ");
            }

            @Override
            public void OnTextSelectionModeChangeListner(Boolean mode) {
                Log.v(TAG, "Text selection mode: " + mode + " ");
                if (mode == true) {
                    bottomContextualBar.setVisibility(View.VISIBLE);
                } else {
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
            }
        });
        readAloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "To do: read aloud: " + epubReaderView.GetChapterContent(), Toast.LENGTH_SHORT).show();
            }
        });
        changeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (epubReaderView.GetTheme() == epubReaderView.THEME_LIGHT) {
                    epubReaderView.SetTheme(epubReaderView.THEME_DARK);
                } else {
                    epubReaderView.SetTheme(epubReaderView.THEME_LIGHT);
                }
            }
        });
        selectRead.setOnClickListener(new View.OnClickListener() {
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
                        if (ChNo >= 0 && !SelectedText.equals("") && !DataString.equals("")) {
                            Toast.makeText(context, "todo: read aloud: " + SelectedText, Toast.LENGTH_SHORT).show();
                        }
                        epubReaderView.ExitSelectionMode();
                    }
                }, 100);
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
                        if (ChNo >=0 &&! SelectedText.equals("") &&! DataString.equals("")) {
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
                        if (ChNo >=0 &&! SelectedText.equals("") &&! DataString.equals("")) {
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
                        if (ChNo >=0 &&! SelectedText.equals("") &&! DataString.equals("")) {
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
                        if (ChNo >=0 &&! SelectedText.equals("") &&! DataString.equals("")) {
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
                        if (ChNo >=0 &&! SelectedText.equals("") &&! DataString.equals("")) {
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
                        if (ChNo >=0 &&! SelectedText.equals("") &&! DataString.equals("")) {
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
}