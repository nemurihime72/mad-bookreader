package com.mad.bookreader;


import android.graphics.Bitmap;
import android.net.Uri;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class importedBooks {
    private String Title;
    private Bitmap Image;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    private String FileName;
    private File pdfFile;


    public String getFileType() {
        return FileType;
    }

    public void setFileType(String fileType) {
        this.FileType = fileType;
    }

    private String FileType;


    public String getBookUri() {
        return BookUri;
    }

    public void setBookUri(String bookUri) {
        BookUri = bookUri;
    }

    private String BookUri;


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


    public importedBooks(String title, Bitmap img, String uri, String fileType) {
        Title = title;
        Image = img;
        BookUri = uri;
        FileType = fileType;
    }

}
