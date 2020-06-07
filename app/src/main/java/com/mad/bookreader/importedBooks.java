package com.mad.bookreader;


import android.graphics.Bitmap;
import android.net.Uri;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class importedBooks {
    private String Title;
    private Bitmap Image;
    private String PdfName;
    private File pdfFile;

    public int getColumnid() {
        return columnid;
    }

    public void setColumnid(int columnid) {
        this.columnid = columnid;
    }

    private int columnid;

    public int getPrevpage() {
        return prevpage;
    }

    public void setPrevpage(int prevpage) {
        this.prevpage = prevpage;
    }

    private int prevpage;

    public int getSwipedirection() {
        return swipedirection;
    }

    public void setSwipedirection(int swipedirection) {
        this.swipedirection = swipedirection;
    }

    private int swipedirection;


    public String getPdfUri() {
        return PdfUri;
    }

    public void setPdfUri(String pdfUri) {
        PdfUri = pdfUri;
    }

    private String PdfUri;

   /* public Uri getPdfUri() {
        return PdfUri;
    }

    public void setPdfUri(Uri pdfUri) {
        PdfUri = pdfUri;
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

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    public importedBooks() {
    }


   /* public importedBooks(String title,int image,String pdf) {
        Title = title;
        Image = image;
        PdfName = pdf;
    }*/
    public importedBooks(String title, Bitmap img, String uri) {
        Title = title;
        Image = img;
        PdfUri = uri;
    }
    public importedBooks(int column,String title,String uri,int prev,int swipe){
        columnid=column;
        Title=title;
        PdfUri=uri;
        prevpage=prev;
        swipedirection=swipe;
    }

}
