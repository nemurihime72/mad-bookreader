package com.mad.bookreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;

public class epubReadActivity extends AppCompatActivity {
    final static String TAG = "epublib";
    WebView webView;
    Navigator mNavigator;
    ImageButton nextCh;
    ImageButton prevCh;
    int i;
    int noOfCh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_read);
        webView = findViewById(R.id.webView);
        nextCh = findViewById(R.id.nextCh);
        prevCh = findViewById(R.id.prevCh);
        Intent epubBook = getIntent();
        if (epubBook.hasExtra("BookName") | epubBook.hasExtra("BookUri")) {
            i = 0;
            final String bookUri = epubBook.getStringExtra("Bookpath");
            loadChapter(i, bookUri);
            File epub = new File(bookUri);
            InputStream epubInputStream = null;
            try {
                epubInputStream = new FileInputStream(epub);
                Book book = (new EpubReader()).readEpub(epubInputStream);
                List<TOCReference> tocReferences = book.getTableOfContents().getTocReferences();
                Log.v(TAG, String.valueOf(tocReferences.size()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            webView.setOnTouchListener(new OnSwipeTouchListener(epubReadActivity.this){
                public void onSwipeRight() {
                    i -= 1;
                    if (i <= 0) {
                        i = 0;
                    }
                    Toast.makeText(epubReadActivity.this, "going to prev chapter", Toast.LENGTH_SHORT).show();
                    loadChapter(i, bookUri);
                }
                public void onSwipeLeft() {
                    i += 1;
                    Toast.makeText(epubReadActivity.this, "going to next chapter", Toast.LENGTH_SHORT).show();
                    loadChapter(i, bookUri);
                }
            });
            nextCh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    i += 1;
                    loadChapter(i, bookUri);
                }
            });
            prevCh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    i -= 1;
                    if (i <= 0) {
                        i = 0;
                    }
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

    private void loadChapter(int j, String filePath) {
        AssetManager assetManager = getAssets();
        webView = findViewById(R.id.webView);
        try {
            //find InputStream for book
            //InputStream epubInputStream = assetManager.open("books/testbook.epub");
            Log.v(TAG, filePath);
            File epubFile = new File(filePath);
            DownloadResources(epubFile, epubFile.getName());
            if (epubFile.exists()) {
                Log.v("epublib", "file exists! loading epub");
            } else {
                Log.v("epublib", "file does not exist");
            }
            //InputStream epubInputStream = new FileInputStream(epubFile);
            String fileName = epubFile.getName();
            InputStream epubInputStream = new FileInputStream(epubFile);
            //load book from inputStream
            Book book = (new EpubReader()).readEpub(epubInputStream);


            //log the book's authors
            //Log.i("epublib", "author(s): " + book.getMetadata().getAuthors());

            //log the book's title
            //Log.i("epublib", "title: " + book.getTitle());

            //log the book's coverimage property
            //Bitmap coverImage = BitmapFactory.decodeStream((book.getCoverImage()).getInputStream());
            //Log.i("epublib", "cover image is: " + coverImage.getWidth() + " by " + coverImage.getHeight() + " pixels");

            //log the table of contents
            //logTableOfContents(book.getTableOfContents().getTocReferences(), 0);

            String asdf = book.getContents().toString();
            String baseUrl = epubFile.getParent();
            String imagePath = epubFile.getParent() + "/Images/" + fileName + "/images";
            String cssPath = epubFile.getParent() + "/Styles/" + fileName + "/css";
            Log.v(TAG, "image path is: " + imagePath);
            Log.v(TAG, "css path is: " + cssPath);
            String data = new String(book.getContents().get(j).getData());
            //data = data.replaceAll("images/", imagePath);
            //data = data.replaceAll("css/", cssPath);
            webView.loadDataWithBaseURL("file://", data, "text/html", "UTF-8", null);
            Log.i("epublib", "loading chapter: " + j);
        }
        catch (IOException e) {
            Log.e("epublib", e.getMessage());
        }
    }

    private void DownloadResources(File file, String filename) {
        try {
            InputStream epub = new FileInputStream(file);
            Book book = (new EpubReader()).readEpub(epub);

            Resources resources = book.getResources();
            Collection<Resource> resourceCollection = resources.getAll();
            Iterator<Resource> iterator = resourceCollection.iterator();

            while (iterator.hasNext()) {
                Resource rs = iterator.next();

                if ((rs.getMediaType() == MediatypeService.JPG) || (rs.getMediaType() == MediatypeService.GIF) || (rs.getMediaType() == MediatypeService.PNG)) {
                    File oppath1 = new File(file.getParent(), "Images/" + filename + "/" + rs.getHref().replace("Images/", ""));
                    oppath1.getParentFile().mkdirs();
                    oppath1.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(oppath1);
                    fileOutputStream.write(rs.getData());
                    fileOutputStream.close();
                } else if (rs.getMediaType() == MediatypeService.CSS) {
                    File oppath = new File(file.getParent(), "Styles/" + filename + "/" + rs.getHref().replace("Styles/", ""));
                    oppath.getParentFile().mkdirs();
                    oppath.createNewFile();
                    FileOutputStream fos = new FileOutputStream(oppath);
                    fos.write(rs.getData());
                    fos.close();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}