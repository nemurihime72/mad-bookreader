package com.mad.bookreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbhandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String BOOKDATABASE = "bookDB.db";
    private static final String TABLE_BOOKS = "books";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "booktitle";
    public static final String COLUMN_IMAGE = "bookimage";
    public static final String COLUMN_PDFURI = "bookpdfuri";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, BOOKDATABASE, factory, DATABASE_VERSION);
    }

    //Creates database
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_BOOKS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                 + COLUMN_TITLE + " TEXT, " + COLUMN_IMAGE + " INTEGER, " + COLUMN_PDFURI + " TEXT" + ")";
        db.execSQL(CREATE_BOOKS_TABLE);
    }

    //Adds book to database
    public void addBook(importedBooks book) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, book.getTitle());
        values.put(COLUMN_IMAGE, book.getImage());
        values.put(COLUMN_PDFURI, book.getPdfUri());
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_BOOKS, null, values);
        db.close();
    }

    //Finds a book by its name
    //public void findBook(String bookTitle)

    //Fills up with books in database when app starts
    //public void startBook()

    //Deletes book from database using title of book by finding its ID
    public boolean deleteBook(String bookTitle) {
        boolean result = false;
        String query = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_TITLE + " = \"" + bookTitle + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        importedBooks book = new importedBooks();

        if(cursor.moveToFirst()) {
            int id = Integer.parseInt(cursor.getString(0));
            db.delete(TABLE_BOOKS, COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
