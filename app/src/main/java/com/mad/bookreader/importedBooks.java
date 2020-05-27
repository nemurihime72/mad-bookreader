package com.mad.bookreader;


import android.net.Uri;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class importedBooks {
    private String Title;
    private int Image;
    private String PdfName;
    /*private File pdfFile;
    private Uri pdfUri;

    public Uri getPdfUri() {
        return pdfUri;
    }

    public void setPdfUri(Uri pdfUri) {
        this.pdfUri = pdfUri;
    }

    public File getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(File pdfFile) {
        this.pdfFile = pdfFile;
    }*/

    public String getPdfName() {
        return PdfName;
    }

    public void setPdfName(String pdfName) {
        PdfName = pdfName;
    }


    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }

    public importedBooks() {
    }


    public importedBooks(String title,int image,String pdf) {
        Title = title;
        Image = image;
        PdfName = pdf;
    }
/*    public importedBooks(String title, int img, Uri uri) {
        Title = title;
        Image = img;
        pdfUri = uri;
    }

 */
}
