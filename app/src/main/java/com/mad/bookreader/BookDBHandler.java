package com.mad.bookreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BookDBHandler extends SQLiteOpenHelper {

    final static String TAG = "DatabaseHandler.java";

    private static final int DATABASE_VERSION = 1;
    private static final String BOOKDATABASE = "bookDB.db";
    private static final String TABLE_BOOKS = "books";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "booktitle";
    public static final String COLUMN_PDFURI = "bookpdfuri";
    public static final String COLUMN_PREVPAGE = "booklastpage";
    public static final String COLUMN_SWIPE = "bookswipepage";
    public static final String COLUMN_FILETYPE = "bookfiletype";

    public BookDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, BOOKDATABASE, factory, DATABASE_VERSION);
    }


    //Creates database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_BOOKS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "  + COLUMN_PDFURI + " TEXT, " + COLUMN_FILETYPE + " TEXT, " + COLUMN_PREVPAGE + " INTEGER, " + COLUMN_SWIPE + " INTEGER" + ")";
        db.execSQL(CREATE_BOOKS_TABLE);
    }

    //Adds book to database
    public void addBook(String pdfName, String pdfURI, String fileType) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, noOfRows());
        values.put(COLUMN_NAME, pdfName);
        values.put(COLUMN_PDFURI, pdfURI);
        values.put(COLUMN_FILETYPE, fileType);
        values.put(COLUMN_PREVPAGE, 0);
        values.put(COLUMN_SWIPE, 0);
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_BOOKS, null, values); //DO ACTUAL INSERT STATEMENT
        Log.v(TAG, "Adding book " + values.get(COLUMN_NAME) + " into database");
        findBookID(0);
        db.close();
    }

    //Fill a list up with books from database
    public List<List<String>> startBooks(List<List<String>> bookList) {
        int rows = noOfRows();
        List<String> nameList = new ArrayList<>();
        List<String> uriList = new ArrayList<>();
        List<String> fileTypeList = new ArrayList<>();
        for(int i = 0; i < rows ; i++){
            ArrayList<String> bookDetails = findBookID(i);
            nameList.add(bookDetails.get(0));
            uriList.add(bookDetails.get(1));
            fileTypeList.add(bookDetails.get(2));
            //fileTypeList.add(bookDetails.get(3));
            Log.v(TAG, "Name: " + nameList.get(0));
            //Log.v(TAG, bookDetails.get(3));
        }
        bookList.add(nameList);
        bookList.add(uriList);
        bookList.add(fileTypeList);
        return bookList;
    }

    //Finds books based on their id
    public ArrayList<String> findBookID(int id){
        String query = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + " = \"" + id + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> bookDetails= new ArrayList<>();

        if (cursor.moveToFirst()) {
            Log.v(TAG, "Adds pdf name, type and uri to list");
            bookDetails.add(cursor.getString(1));
            Log.v(TAG, "Found name: " + cursor.getString(1));
            bookDetails.add(cursor.getString(2));
            Log.v(TAG, "Found URI: " + cursor.getString(2));
            bookDetails.add(cursor.getString(3));
            Log.v(TAG, "Found filetype: " + cursor.getString(3));
            cursor.close();
        } else {
            bookDetails = null;
        }
        db.close();
        Log.v(TAG, "returning book with id of: " + bookDetails.get(0));
        return bookDetails;
    }


    /*public ArrayList<String> findBookName(int id) {
        String query = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + " = \"" + id + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        String pdfName;
        String pdfURI;
        ArrayList<String> bookDetails = new ArrayList<>();

        if (cursor.moveToFirst()) {
            Log.v(TAG, "Adds pdf name and uri to list");
            bookDetails.add(cursor.getString(1));
            bookDetails.add(cursor.getString(2));
            cursor.close();
        } else {
            bookDetails = null;
        }
        db.close();
        Log.v(TAG, "returning book");
        return bookDetails;
    }*/

    public boolean editTitleBook(int colId,String newBookTitle){
        boolean result= false;
        String query="SELECT * FROM "+ TABLE_BOOKS+" WHERE "+COLUMN_ID+" = \""+colId+"\"";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            //int id = Integer.parseInt(cursor.getString(0));
            //String query2=("UPDATE "+TABLE_BOOKS+" SET "+COLUMN_NAME+" = +\""+ newBookTitle+"\"" + " WHERE "+COLUMN_ID+" =\""+id+"\"");
            String query2=("UPDATE "+TABLE_BOOKS+" SET "+COLUMN_NAME+" = +\""+ newBookTitle+"\"" + " WHERE "+COLUMN_ID+" =\""+colId+"\"");
            db.execSQL(query2);
            cursor.close();
            result = true;
        }
        db.close();
        return result;

    }

    //Deletes book from database using title of book by finding its ID
    public boolean deleteBook(int colId) {
        boolean result = false;
        String query = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + " = \"" + colId + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            //int id = Integer.parseInt(cursor.getString(0));
            //db.delete(TABLE_BOOKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            db.delete(TABLE_BOOKS, COLUMN_ID + " = ?", new String[]{String.valueOf(colId)});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    //deletes all books from the database
    public boolean deleteallBooks() {
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_BOOKS,null,null);
        db.close();
        return true;

        /*boolean result = false;
        String query = "SELECT * FROM " + TABLE_BOOKS;
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = noOfRows();
        Cursor cursor = db.rawQuery(query, null);

        for(int i = 0; i < rows; i++) {
            if (cursor.moveToFirst()) {
                int id = Integer.parseInt(cursor.getString(0));
                db.delete(TABLE_BOOKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
                cursor.close();
                result = true;
            }
        }
        db.close();
        return result; */
    }

    //Finds page number last read with pdfName
    public int lastPage(int colId){
        int pdfPage = 0;
        String query = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + " = \"" + colId + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            Log.v(TAG, "Getting previously left page from database");
            pdfPage = Integer.parseInt(cursor.getString(4));
            Log.v(TAG, "Last page is " + pdfPage);
            cursor.close();
            db.close();
        }
        else {
            Log.v(TAG, "cursor did not move to first in lastpage");
        }
        return pdfPage;
    }
    public void updateLastPage (int colId, int lastPageRead) {
        SQLiteDatabase db = this.getWritableDatabase();
        String UPDATE_LAST_PAGE = "UPDATE " + TABLE_BOOKS + " SET " + COLUMN_PREVPAGE + "=\"" + lastPageRead + "\"" +  " WHERE " + COLUMN_ID + "=\"" + colId + "\"";
        Log.v(TAG, UPDATE_LAST_PAGE);
        Log.v(TAG, "Updating last page read for Column ID: " + colId + " to " + lastPageRead);
        db.execSQL(UPDATE_LAST_PAGE);
        db.close();
    }

    public void updatePageSwipe(int colId,int direction){
        SQLiteDatabase db = this.getWritableDatabase();
        String UPDATE_SWIPE = "UPDATE " + TABLE_BOOKS + " SET " + COLUMN_SWIPE + "=\"" + direction +  "\" WHERE " + COLUMN_ID + "=\"" + colId + "\"";
        Log.v(TAG, UPDATE_SWIPE);
        db.execSQL(UPDATE_SWIPE);
        db.close();
    }

    public int pageSwipe(int colId) {
        int pageSwipe = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + " = \"" + colId + "\"";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            Log.v(TAG, "Getting swipe direction from database");
            pageSwipe = Integer.parseInt(cursor.getString(5));
            Log.v(TAG, "Page swipe direction int is " + pageSwipe);
            cursor.close();
            db.close();
        } else {
            Log.v(TAG, "cursor did not move to first in pageswipe");
        }
        db.close();
        return pageSwipe;
    }

    //Finds number of books(rows) in the database
    public @Nullable int noOfRows(){
        int rows = 0;
        Log.v(TAG, "Creating query to get the number of rows in the database");
        String query = "SELECT COUNT("+ COLUMN_ID + ") FROM " + TABLE_BOOKS;
        SQLiteDatabase db = this.getWritableDatabase();

        Log.v(TAG, "Querying the number of books in database");
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            rows = cursor.getInt(0);
            Log.v(TAG, "Number of rows:" + rows);
            cursor.close();
        }
        db.close();
        return rows;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
