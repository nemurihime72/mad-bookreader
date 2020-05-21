package com.mad.bookreader;

import android.widget.ImageView;

public class importedBooks {
    private String Title;
    private int Image;

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
