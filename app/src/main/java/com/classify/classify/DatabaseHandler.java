package com.classify.classify;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rutviz Vyas on 24-11-2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Classify";
    private static final String table_name = "Classification";

    private static final String id = "id";
    private static final String path = "path";
    private static final String category = "category";
    private static final String date = "date";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String Create_table = "CREATE TABLE " + table_name + "("
                + id + " INTEGER PRIMARY KEY AUTOINCREMENT," + path + " TEXT,"
                + category + " TEXT" + ","+date+" TEXT )";
        sqLiteDatabase.execSQL(Create_table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreate(sqLiteDatabase);
    }

    public void addData(Classify_path classify) {
        SQLiteDatabase db = this.getWritableDatabase();
        // if(getDataCount()<1)
        {
            ContentValues values = new ContentValues();
            values.put(path, classify.getPath()); // Contact Name
            values.put(category, classify.getCategory()); // Contact Phone Number
            values.put(date, classify.getDate()); // Contact Phone Number
            // Inserting Row
            db.insert(table_name, null, values);
            getData();
        }
        db.close();
    }

    public void getDeleteRow(int ids)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + table_name+ " WHERE " +id+ "= '"+ids+"'" );
        database.close();
    }

    public Cursor getPathOfnum(int ids)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        /*database.execSQL("SELECT FROM " + table_name+ " WHERE " +id+ "= '"+ids+"'" );*/
        String query = "SELECT path FROM " + table_name+ " WHERE " +id+ "= '"+ids+"'" ;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int getDataCount() {
        String countQuery = "SELECT  * FROM " + table_name;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getpathCount(String Path) {
        String countQuery = "SELECT  * FROM " + table_name + " WHERE " +path+" = '"+Path+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public ArrayList<Classify_path> getData() {
        ArrayList<Classify_path> Data = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + table_name;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Classify_path classify = new Classify_path();
                classify.setPath(cursor.getString(1));
                classify.setCategory(cursor.getString(2));
                classify.setDate(cursor.getString(3));
                // Adding contact to list
                Data.add(classify);
//                Log.d("Database",classify.getPath());
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return Data;
    }

    public ArrayList<Classify_path> getUniqueData(String Search) {
        ArrayList<Classify_path> Data = new ArrayList<>();
        String selectQuery;
        if(Search.equals("All"))
        {
            selectQuery = "SELECT  * FROM " + table_name + " ORDER BY "+date+" DESC";
        }
        else
        {
            selectQuery = "SELECT  * FROM " + table_name + " WHERE "+category+" = '"+Search+"' ORDER BY "+date+" DESC";
        }


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Classify_path classify = new Classify_path();
                classify.setPath(cursor.getString(1));
                classify.setCategory(cursor.getString(2));
                classify.setDate(cursor.getString(3));
                // Adding contact to list
                Data.add(classify);
//                Log.d("Database",classify.getPath());
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return Data;
    }
    void Cleardata()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE " + table_name);
        String Create_table = "CREATE TABLE " + table_name + "("
                + id + " INTEGER PRIMARY KEY AUTOINCREMENT," + path + " TEXT,"
                + category + " TEXT" + ")";;
        db.execSQL(Create_table);
    }

    ArrayList<String> getCategory()
    {
        ArrayList<String> category_list= new ArrayList<>();

        String query = "SELECT DISTINCT "+category+" FROM "+table_name;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                category_list.add(cursor.getString(0));
//                Log.d("Database",classify.getPath());
            } while (cursor.moveToNext());
        }
        cursor.close();
        return category_list;
    }
    String getSingleCategory(String image_path)
    {
        String category="";
        String selectQuery = "SELECT  * FROM " + table_name + " WHERE "+path+" = '"+image_path+"' ORDER BY "+date+" DESC";;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                category = cursor.getString(2);
//                Log.d("Database",classify.getPath());
            } while (cursor.moveToNext());
        }
        cursor.close();
        return category;
    }

    List<String> getImagepathlist(){
        List<String> paths = new ArrayList<>();
        String selectQuery = "SELECT  "+path+" FROM " + table_name;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String temp;
        if (cursor.moveToFirst()) {
            do {
                temp = cursor.getString(0);
                paths.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return paths;
    }



    void deleteimagepath(String value){
        String query = "DELETE FROM " + table_name +" WHERE "+ path +" = '"+value+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        Log.d("path123","Path deleted: "+value);

    }
}
