package com.mad.bookreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

public class epubReadActivity extends AppCompatActivity {
    WebView webView;
    Navigator mNavigator;
    Button nextCh;
    int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_read);
        webView = findViewById(R.id.webView);

        Intent epubBook = getIntent();
        if (epubBook.hasExtra("BookName") | epubBook.hasExtra("BookUri")) {
            i = 0;
            final String bookUri = epubBook.getStringExtra("BookUri");
            loadChapter(i, bookUri);
            webView.setOnTouchListener(new OnSwipeTouchListener(epubReadActivity.this){
                public void onSwipeRight() {
                    i -= 1;
                    Toast.makeText(epubReadActivity.this, "going to prev chapter", Toast.LENGTH_SHORT).show();
                    loadChapter(i, bookUri);
                }
                public void onSwipeLeft() {
                    i += 1;
                    Toast.makeText(epubReadActivity.this, "going to next chapter", Toast.LENGTH_SHORT).show();
                    loadChapter(i, bookUri);
                }
            });
        }


    }

    private void logTableOfContents(List<TOCReference> tocReferences, int depth) {
        if (tocReferences == null) {
            return;
        }
        for (TOCReference tocReference : tocReferences) {
            StringBuilder tocString = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                tocString.append("\t");
            }
            tocString.append(tocReference.getTitle());
            Log.i("epublib", tocString.toString());

            logTableOfContents(tocReference.getChildren(), depth + 1);
        }
    }

    private void loadChapter(int j, String uri) {
        AssetManager assetManager = getAssets();
        webView = findViewById(R.id.webView);
        try {
            //find InputStream for book
            //InputStream epubInputStream = assetManager.open("books/testbook.epub");
            Uri fileUri = Uri.parse(uri);

            Log.v("epublib", fileUri.getPath().toString());
            File file = new File(fileUri.getPath());
            String[] split = file.getPath().split(":");
            String filePath = split[1];
            File epubFile = new File(filePath);

            if (epubFile.exists()) {
                Log.v("epublib", "file exists! loading epub");
            } else {
                Log.v("epublib", "file does not exist");
            }
            //InputStream epubInputStream = new FileInputStream(epubFile);
            String fileName = epubFile.getName();
            InputStream epubInputStream = assetManager.open("books/testbook.epub");
            //load book from inputStream
            Book book = (new EpubReader()).readEpub(epubInputStream);

            //log the book's authors
            Log.i("epublib", "author(s): " + book.getMetadata().getAuthors());

            //log the book's title
            Log.i("epublib", "title: " + book.getTitle());

            //log the book's coverimage property
            Bitmap coverImage = BitmapFactory.decodeStream((book.getCoverImage()).getInputStream());
            Log.i("epublib", "cover image is: " + coverImage.getWidth() + " by " + coverImage.getHeight() + " pixels");

            //log the table of contents
            logTableOfContents(book.getTableOfContents().getTocReferences(), 0);

            String asdf = book.getContents().toString();
            String baseUrl = "file://mnt/sdcard/Download/";
            String data = new String(book.getContents().get(j).getData());
            webView.loadDataWithBaseURL(baseUrl, data, "text/html", "UTF-8", null);
            Log.i("epublib", "loading chapter: " + j);
        }
        catch (IOException e) {
            Log.e("epublib", e.getMessage());
        }
    }
}