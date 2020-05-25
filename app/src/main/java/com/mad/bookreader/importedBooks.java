package com.mad.bookreader;


import com.github.barteksc.pdfviewer.PDFView;

public class importedBooks {
    private String Title;
    private int Image;
    private String PdfName;

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
        Title=title;
        Image=image;
        PdfName=pdf;
    }
}
