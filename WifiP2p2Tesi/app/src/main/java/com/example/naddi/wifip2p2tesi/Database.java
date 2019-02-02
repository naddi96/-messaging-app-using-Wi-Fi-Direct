package com.example.naddi.wifip2p2tesi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "dbApp";
    public static final String TABLE_NAME1 = "peers";
    public static final String col1_t1 = "mac";
    public static final String col2_t1= "name";
    public static final String TABLE_NAME2 = "message";
    public static final String col1_t2 = "id";
    public static final String col2_t2 = "mex";
    public static final String col3_t2= "mac";



    public  boolean insertPeers(String mac,String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col1_t1,mac);
        contentValues.put(col2_t1,name);
        long ret = db.insert(TABLE_NAME1,null,contentValues);
        if(ret == -1){
            return false;
        }else{
            return true;
        }

    }


    public Cursor getMessages(String mac){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select mex from "+ TABLE_NAME2+ " where mac="+"'"+mac+"'";
        Cursor res = db.rawQuery(sql,null);
        return  res;
    }

    public  boolean insertMessage(String mac,String mex){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col2_t2,mex);
        contentValues.put(col3_t2,mac);
        long ret = db.insert(TABLE_NAME2,null,contentValues);
        if(ret == -1){
            return false;
        }else{
            return true;
        }

    }

    public Cursor getPeers(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ TABLE_NAME1,null);
        return  res;
    }




    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null,1);
    }

    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " +  TABLE_NAME1 +"  (mac TEXT,name TEXT,  primary key(mac))");
        sqLiteDatabase.execSQL("create table " + TABLE_NAME2  +" (id integer primary key AUTOINCREMENT ,mac TEXT,mex TEXT, foreign key(mac) references peers(max))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
