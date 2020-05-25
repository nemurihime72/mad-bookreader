package com.mad.bookreader;


import com.github.barteksc.pdfviewer.PDFView;

public class importedBooks {
    private String Title;
    private int Image;

    public PDFView getPdf() {
        return pdf;
    }

    public void setPdf(PDFView pdf) {
        this.pdf = pdf;
    }

    private PDFView pdf;

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


    public importedBooks(String title,int image) {
        Title=title;
        Image=image;
    }
}
