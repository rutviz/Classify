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
    private static final String table_name_global = "Global";
    private static final String table_name_recyclebin = "Recyclebin";
    private static final String table_name_auto_delete = "AutoDelete";

    private static final String id = "id";
    private static final String path = "path";
    private static final String category = "category";
    private static final String date = "date";
    private static final String variablename = "variable";
    private static final String variablevalue = "variablevalue";
    private static final String oldpath = "oldpath";
    private static final String delete_time = "deletetime";
    private static final String modified_date = "modifieddate";
    private static final String newpath = "newpath";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String Create_table = "CREATE TABLE " + table_name + "("
                + id + " INTEGER PRIMARY KEY AUTOINCREMENT," + path + " TEXT,"
                + category + " TEXT" + ","+date+" TEXT )";
        String Create_table2 = "CREATE TABLE " + table_name_global + "("
                +variablename + " TEXT," + variablevalue + " VARCHAR2 )";
        String Create_table3 = "CREATE TABLE " + table_name_recyclebin + "("
                +oldpath + " VARCHAR2," +delete_time + " VARCHAR2,"+modified_date + " VARCHAR2,"+ newpath + " VARCHAR2 )";

        String Create_table4 = "CREATE TABLE " + table_name_auto_delete + "("
                + id + " INTEGER PRIMARY KEY AUTOINCREMENT," + path + " TEXT,"
                + category + " TEXT" + ","+date+" TEXT )";
        sqLiteDatabase.execSQL(Create_table);
        sqLiteDatabase.execSQL(Create_table2);
        sqLiteDatabase.execSQL(Create_table3);
        sqLiteDatabase.execSQL(Create_table4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreate(sqLiteDatabase);
    }

    public void createtable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String Create_table4 = "CREATE TABLE " + table_name_auto_delete + "("
                + id + " INTEGER PRIMARY KEY AUTOINCREMENT," + path + " TEXT,"
                + category + " TEXT" + ","+date+" TEXT )";
        db.execSQL(Create_table4);
    }


    public void globaladdData(String variable_name,String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db1 = this.getReadableDatabase();
        String query = "SELECT "+variablename+" FROM "+table_name_global+" WHERE "+variablename+" = '"+variable_name+"'";
        Cursor cursor = db1.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        if(count==0)
        {
            ContentValues values = new ContentValues();
            values.put(variablename, variable_name);
            values.put(variablevalue, value);
            db.insert(table_name_global, null, values);
            getData();
        }
        else {
        }
        db.close();
        db1.close();
    }
    public void globalEditaddData(String variable_name,String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db1 = this.getReadableDatabase();
        String query = "SELECT "+variablename+" FROM "+table_name_global+" WHERE "+variablename+" = '"+variable_name+"'";
            ContentValues values = new ContentValues();
            values.put(variablename, variable_name);
            values.put(variablevalue, value);
            db.insert(table_name_global, null, values);
            getData();
        db.close();
        db1.close();
    }
    public void recyclebinaddData(String old_path,String deletetime,String modifieddate,String new_path) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db1 = this.getReadableDatabase();
        String query = "SELECT "+oldpath+" FROM "+table_name_recyclebin+" WHERE "+oldpath+" = '"+old_path+"'";
        Cursor cursor = db1.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        if(count==0)
        {
            ContentValues values = new ContentValues();
            values.put(oldpath, old_path);
            values.put(delete_time, deletetime);
            values.put(modified_date, modifieddate);
            values.put(newpath, new_path);
            db.insert(table_name_recyclebin, null, values);
            getData();
        }
        else {
        }
        db.close();
        db1.close();
    }
    public void AutoDeleteGlobalAddData() {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db1 = this.getReadableDatabase();
        for(String cate : Global_Share.aList)
        {
            String query = "SELECT "+variablename+" FROM "+table_name_global+" WHERE "+variablename+" = '"+cate+"'";
            Cursor cursor = db1.rawQuery(query, null);
            int count = cursor.getCount();
            cursor.close();
            if(count==0)
            {
                Log.d("hey","1");
                ContentValues values = new ContentValues();
                values.put(variablename, cate);
                values.put(variablevalue, "0");
                db.insert(table_name_global, null, values);
            }
        }
        db.close();
        db1.close();
    }



    public String AutoDeleteGetData(String category_delete)
    {
        String value = "";
        String query = "SELECT "+variablevalue+" FROM "+ table_name_global + " WHERE " +variablename+" = '"+category_delete+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                value = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return value;
    }

    public List<String> recyclebingetdata(){
        List<String> paths = new ArrayList<>();
        String temp;
        String query = "SELECT "+newpath+" FROM "+ table_name_recyclebin+" ORDER BY "+delete_time+" DESC" ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                temp = cursor.getString(0);
                paths.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return paths;
    }
    public String recyclegetdatevalue(String path){
        String value = "";
        String query = "SELECT "+modified_date+" FROM "+ table_name_recyclebin + " WHERE " +newpath+" = '"+path+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                value = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return value;
    }
    public String recyclegetvalue(String path){
        String value = "";
        String query = "SELECT "+oldpath+" FROM "+ table_name_recyclebin + " WHERE " +newpath+" = '"+path+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                value = cursor.getString(0);
                Log.d("valuee", value);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return value;
    }


    void deleteimagepathfromrecycle(String value){
        String query = "DELETE FROM " + table_name_recyclebin +" WHERE "+ newpath +" = '"+value+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
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
    public void addDataForAutoDelete(Classify_path classify) {
        SQLiteDatabase db = this.getWritableDatabase();
        // if(getDataCount()<1)
        {
            ContentValues values = new ContentValues();
            values.put(path, classify.getPath()); // Contact Name
            values.put(category, classify.getCategory()); // Contact Phone Number
            values.put(date, classify.getDate()); // Contact Phone Number
            // Inserting Row
            db.insert(table_name_auto_delete, null, values);
        }
        db.close();
    }

    ArrayList<String> getCategoryFromAutoDelete(){
        ArrayList<String> category_list= new ArrayList<>();

        String query = "SELECT DISTINCT "+category+" FROM "+table_name_auto_delete;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                category_list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return category_list;
    }
    ArrayList<String> getCategoryFromAutoDeleteSelected(){
        ArrayList<String> category_list= new ArrayList<>();
        String query = "SELECT DISTINCT "+category+" FROM "+table_name_auto_delete;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                category_list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return category_list;
    }

    public int getCategoryCountFromAutoDelete(String cate) {
        String countQuery = "SELECT  * FROM " + table_name_auto_delete + " WHERE " +category+" = '"+cate+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public ArrayList<String> getUniqueDataPathOfAutoDelete(String Search) {
        ArrayList<String> Data = new ArrayList<>();
        String selectQuery;
        selectQuery = "SELECT  "+path+" FROM " + table_name_auto_delete + " WHERE "+category+" = '"+Search+"' ORDER BY "+date+" DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Data.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return Data;
    }

    public String globalgetvalue(String variable_name){
        String value = "";
        String query = "SELECT "+variablevalue+" FROM "+ table_name_global + " WHERE " +variablename+" = '"+variable_name+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                value = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return value;
    }

    public void globalsetvalue(String variable_name,String variable_value){

        String query = "UPDATE "+table_name_global+" SET "+ variablevalue +"=" +variable_value+ " WHERE " +variablename+" = '"+variable_name+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);

    }
    public void update_category(String New_category,String oldpath){
        String query = "UPDATE "+table_name+" SET "+ category +"= '" +New_category+ "' WHERE " +path+" = '"+oldpath+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);

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
    public int getpathCountFromCat(String cate) {

        if(cate.equals("All"))
        {
            String countQuery = "SELECT  * FROM " + table_name;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            int count = cursor.getCount();
            cursor.close();
            return count;
        }else {

            String countQuery = "SELECT  * FROM " + table_name + " WHERE " +category+" = '"+cate+"'";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            int count = cursor.getCount();
            cursor.close();
            return count;
        }

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

    public ArrayList<String> getUniqueDataPath(String Search) {
        ArrayList<String> Data = new ArrayList<>();
        String selectQuery;
        if(Search.equals("All"))
        {
            selectQuery = "SELECT  "+path+" FROM " + table_name + " ORDER BY "+date+" DESC";
        }
        else
        {
            selectQuery = "SELECT  "+path+" FROM " + table_name + " WHERE "+category+" = '"+Search+"' ORDER BY "+date+" DESC";
        }


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Adding contact to list
                Data.add(cursor.getString(0));
//                Log.d("Database",classify.getPath());
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return Data;
    }

    ArrayList<String> getCategory(){
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
    void deleteimagepathFromAutoDelete(String value){
        String query = "DELETE FROM " + table_name_auto_delete +" WHERE "+ path +" = '"+value+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        Log.d("path123","Path deleted: "+value);
    }


}
