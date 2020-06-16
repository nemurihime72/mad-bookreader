package com.mad.bookreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

public class epubReadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_read);
        AssetManager assetManager = getAssets();

        try {
            //find InputStream for book
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
        } catch (IOException e) {
            Log.e("epublib", e.getMessage());
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
}