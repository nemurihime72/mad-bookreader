package com.mad.bookreader;


import android.graphics.Bitmap;
import android.net.Uri;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class importedBooks {
    private String Title;
    private Bitmap Image;
    private String BookUri;
    private int Id;
    private String FileType;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getFileType() {
        return FileType;
    }

    public void setFileType(String fileType) {
        this.FileType = fileType;
    }

    public String getBookUri() {
        return BookUri;
    }

    public void setBookUri(String bookUri) {
        BookUri = bookUri;
    }


    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    public importedBooks() {
    }


    public importedBooks(int id,String title, Bitmap img, String uri, String fileType) {
        Id=id;
        Title = title;
        Image = img;
        BookUri = uri;
        FileType = fileType;
    }

}
