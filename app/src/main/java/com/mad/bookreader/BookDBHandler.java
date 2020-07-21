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
    public static final String COLUMN_CHAPTER = "bookchapter";
    public static final String COLUMN_PROGRESS = "bookprogress";

    public BookDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, BOOKDATABASE, factory, DATABASE_VERSION);
    }


    //Creates database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_BOOKS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_NAME + " TEXT, "  + COLUMN_PDFURI + " TEXT, " + COLUMN_FILETYPE + " TEXT, "
                + COLUMN_PREVPAGE + " INTEGER, " + COLUMN_SWIPE + " INTEGER, " + COLUMN_CHAPTER + " INTEGER, "
                + COLUMN_PROGRESS + " REAL" +  ")";
        db.execSQL(CREATE_BOOKS_TABLE);
    }


    //Adds book to database
    public void addBook(int id, String pdfName, String pdfURI, String fileType) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_NAME, pdfName);
        values.put(COLUMN_PDFURI, pdfURI);
        values.put(COLUMN_FILETYPE, fileType);
        values.put(COLUMN_PREVPAGE, 0);
        values.put(COLUMN_SWIPE, 0);
        values.put(COLUMN_CHAPTER, 0);
        values.put(COLUMN_PROGRESS, 0);
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_BOOKS, null, values); //DO ACTUAL INSERT STATEMENT
        Log.v(TAG, "Adding book " + values.get(COLUMN_NAME) + " into database");
        findBookID(id);
        db.close();
    }

    //Fill a list up with books from database
    public List<List<String>> startBooks(List<List<String>> bookList) {
        int rows = noOfRows();
        List<String> idList = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        List<String> uriList = new ArrayList<>();
        List<String> fileTypeList = new ArrayList<>();
        ArrayList<Integer> bookIDList = bookIdList();
        for (Integer i: bookIDList
             ) {
            Log.v(TAG, String.valueOf(bookIDList));

        }
        for (int i = 0; i < bookIDList.size(); i++) {
            ArrayList<String> bookDetails = findBookID(bookIDList.get(i));
            Log.v(TAG, "IDs : " + bookIDList.get(0));
            idList.add(String.valueOf(bookIDList.get(i)));
            nameList.add(bookDetails.get(1));
            uriList.add(bookDetails.get(2));
            fileTypeList.add(bookDetails.get(3));
            Log.v(TAG, "Name: " + nameList.get(0));
        }
        bookList.add(idList);
        bookList.add(nameList);
        bookList.add(uriList);
        bookList.add(fileTypeList);
        return bookList;
    }

    public ArrayList<Integer> bookIdList() {
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_BOOKS + " ORDER BY " + COLUMN_ID + " ASC";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Integer> idList = new ArrayList<Integer>();
        if (cursor != null){
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                idList.add(cursor.getInt(0));
                cursor.moveToNext();
                }

                //move to the next row
                cursor.moveToNext();
            }
        cursor.close();
        db.close();
        return idList;
        }

    //Finds books based on their id
    public ArrayList<String> findBookID(int id){
        String query = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + " = \"" + id + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> bookDetails= new ArrayList<>();

        if (cursor.moveToFirst()) {
            bookDetails.add(cursor.getString(0));
            Log.v(TAG, "Found id: " + cursor.getString(0));
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
            Log.v(TAG, "Deleting book with id of " + colId);
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
    }

    //Finds page number last read with id
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
    //update last page of pdf read for id pointing to that pdf
    //for use when page changes
    public void updateLastPage (int colId, int lastPageRead) {
        SQLiteDatabase db = this.getWritableDatabase();
        String UPDATE_LAST_PAGE = "UPDATE " + TABLE_BOOKS + " SET " + COLUMN_PREVPAGE + "=\"" + lastPageRead + "\"" +  " WHERE " + COLUMN_ID + "=\"" + colId + "\"";
        Log.v(TAG, UPDATE_LAST_PAGE);
        Log.v(TAG, "Updating last page read for Column ID: " + colId + " to " + lastPageRead);
        db.execSQL(UPDATE_LAST_PAGE);
        db.close();
    }

    //finds last chapter read for epub and returns integer
    public int lastChapter(int colId) {
        int epubChapter = 0;
        String query = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + " = \"" + colId + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            Log.v(TAG, "Getting previously left chapter from db");
            epubChapter = Integer.parseInt(cursor.getString(6));
            Log.v(TAG, "Last chapter read is " + epubChapter);
            cursor.close();
            db.close();
        } else {
            Log.v(TAG, "cursor did not move to first in lastChapter");
        }
        return epubChapter;
    }

    //update the last chapter read for an epub book by finding id corresponding to book
    //for use when chapter changes
    public void updateLastChapter(int colId, int lastChapterRead) {
        SQLiteDatabase db = this.getWritableDatabase();
        String UPDATE_LAST_CHAPTER = "UPDATE " + TABLE_BOOKS + " SET " + COLUMN_CHAPTER + "=\"" + lastChapterRead + "\"" +  " WHERE " + COLUMN_ID + "=\"" + colId + "\"";
        Log.v(TAG, UPDATE_LAST_CHAPTER);
        Log.v(TAG, "Updating last chapter read for Column ID: " + colId + " to " + lastChapterRead);
        db.execSQL(UPDATE_LAST_CHAPTER);
        db.close();
    }

    //finds progress of chapter read and returns a float between 0 and 1
    public float lastProgress(int colId) {
        float epubProgress = 0;
        String query = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + " =\"" + colId + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            Log.v(TAG, "Getting previously left progress from db");
            epubProgress = Float.parseFloat(cursor.getString(7));
            Log.v(TAG, "Last progress read is " + epubProgress);
            cursor.close();
            db.close();
        } else {
            Log.v(TAG, "cursor did not move to first in lastProgress");
        }
        return epubProgress;
    }

    //update progress of chapter read for epub book by finding id corresponding to book
    //for use when progress of chapter changes
    public void updateLastProgress(int colId, float lastProgressRead) {
        SQLiteDatabase db = this.getWritableDatabase();
        String UPDATE_LAST_PROGRESS = "UPDATE " + TABLE_BOOKS + " SET " + COLUMN_PROGRESS + " =\"" + lastProgressRead + "\"" + " WHERE " + COLUMN_ID + "=\"" + colId + "\"";
        Log.v(TAG, UPDATE_LAST_PROGRESS);
        Log.v(TAG, "Updating last progress read for column ID: " + colId + " to " + lastProgressRead);
        db.execSQL(UPDATE_LAST_PROGRESS);
        db.close();
    }

    //updates page swipe direction for pdf reader
    public void updatePageSwipe(int colId,int direction){
        SQLiteDatabase db = this.getWritableDatabase();
        String UPDATE_SWIPE = "UPDATE " + TABLE_BOOKS + " SET " + COLUMN_SWIPE + "=\"" + direction +  "\" WHERE " + COLUMN_ID + "=\"" + colId + "\"";
        Log.v(TAG, UPDATE_SWIPE);
        db.execSQL(UPDATE_SWIPE);
        db.close();
    }

    //gets page swipe direction for pdf reader
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

    //gets the number of the last id in db
    public int lastRowId() {
        int id = 0;
        String query = "SELECT MAX(" + COLUMN_ID + ") FROM " + TABLE_BOOKS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
            Log.v(TAG, "Last ID = " + id);
            cursor.close();
        }
        db.close();
        return id;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(db);

    }
}
