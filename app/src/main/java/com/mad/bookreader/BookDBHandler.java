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

    public BookDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, BOOKDATABASE, factory, DATABASE_VERSION);
    }


    //Creates database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_BOOKS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "  + COLUMN_PDFURI + " TEXT, " + COLUMN_PREVPAGE + " INTEGER, " + COLUMN_SWIPE + " INTEGER" + ")";
        db.execSQL(CREATE_BOOKS_TABLE);
    }

    //Adds book to database
    public void addBook(String pdfName, String pdfURI) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, noOfRows());
        values.put(COLUMN_NAME, pdfName);
        values.put(COLUMN_PDFURI, pdfURI);
        values.put(COLUMN_PREVPAGE, 0);
        values.put(COLUMN_SWIPE, 0);
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_BOOKS, null, values); //DO ACTUAL INSERT STATEMENT
        Log.v(TAG, "Adding book " + values.get(COLUMN_NAME) + " into database");
        findBookID(0);
        db.close();
    }

    //Fill a list up with books from database
    public List<List<String>> startBooks(List<List<String>> pdfList) {
        int rows = noOfRows();
        List<String> pdfNameList = new ArrayList<>();
        List<String> pdfURIList = new ArrayList<>();
        for(int i = 0; i < rows ; i++){
            ArrayList<String> bookDetails = findBookID(i);
            pdfNameList.add(bookDetails.get(0));
            pdfURIList.add(bookDetails.get(1));
            Log.v(TAG, "Name: " + pdfNameList.get(0));
        }
        pdfList.add(pdfNameList);
        pdfList.add(pdfURIList);
        return pdfList;
    }

    //Finds books based on their id
    public ArrayList<String> findBookID(int id){
        String query = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + " = \"" + id + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> bookDetails= new ArrayList<>();

        if (cursor.moveToFirst()) {
            Log.v(TAG, "Adds pdf name and uri to list");
            bookDetails.add(cursor.getString(1));
            Log.v(TAG, "Found: " + cursor.getString(1));
            bookDetails.add(cursor.getString(2));
            cursor.close();
        } else {
            bookDetails = null;
        }
        db.close();
        Log.v(TAG, "returning book: " + bookDetails.get(0));
        return bookDetails;
    }
    public importedBooks findBookName(String name){
        String query = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_NAME + " = \"" + name + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        importedBooks bookDetails=new importedBooks();
        String title;
        String uri;
        int columnid;
        int prevpage;
        int swipe;

        if (cursor.moveToFirst()) {
            columnid=cursor.getInt(0);
            title=cursor.getString(1);
            uri=cursor.getString(2);
            prevpage=cursor.getInt(3);
            swipe=cursor.getInt(4);
            cursor.close();
        } else {
            return null;
        }
        db.close();
        bookDetails.setTitle(title);
        bookDetails.setPdfUri(uri);
        bookDetails.setColumnid(columnid);
        bookDetails.setPrevpage(prevpage);
        bookDetails.setSwipedirection(swipe);
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

    public boolean editTitleBook(String bookTitle,String newBookTitle){
        boolean result= false;
        String query="SELECT * FROM "+ TABLE_BOOKS+" WHERE "+COLUMN_NAME+" = \""+bookTitle+"\"";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            int id = Integer.parseInt(cursor.getString(0));
            String query2=("UPDATE "+TABLE_BOOKS+" SET "+COLUMN_NAME+" = +\""+ newBookTitle+"\"" + " WHERE "+COLUMN_NAME+" =\""+bookTitle+"\"");
            db.execSQL(query2);
            cursor.close();
            result = true;
        }
        db.close();
        return result;

    }

    //Deletes book from database using title of book by finding its ID
    public boolean deleteBook(String bookTitle) {
        boolean result = false;
        String query = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_NAME + " = \"" + bookTitle + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            int id = Integer.parseInt(cursor.getString(0));
            db.delete(TABLE_BOOKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
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
    public int lastPage(String pdfName){
        int pdfPage = 0;
        String query = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_NAME + " = \"" + pdfName + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            Log.v(TAG, "Getting previously left page from database");
            pdfPage = Integer.parseInt(cursor.getString(3));
            Log.v(TAG, "Last page is " + pdfPage);
            cursor.close();
            db.close();
        }
        else {
            Log.v(TAG, "cursor did not move to first in lastpage");
        }
        return pdfPage;
    }
    public void updateLastPage (String pdfName, int lastPageRead) {
        SQLiteDatabase db = this.getWritableDatabase();
        String UPDATE_LAST_PAGE = "UPDATE " + TABLE_BOOKS + " SET " + COLUMN_PREVPAGE + "=\"" + lastPageRead + "\"" +  " WHERE " + COLUMN_NAME + "=\"" + pdfName + "\"";
        Log.v(TAG, "Updating last page read for " + pdfName);
        db.execSQL(UPDATE_LAST_PAGE);
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

        return rows;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
