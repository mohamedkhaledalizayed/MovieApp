package com.mohamed.smile.movieapp;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MoviesProvider extends ContentProvider{


    public static final String PROVIDER_NAME="com.mohamed.smile.movieapp";
    public static final String URL="content://"+PROVIDER_NAME+"/movies";
    public static final Uri CONTENT_URL=Uri.parse(URL);


    SQLiteDatabase db;
    public static final String COL_1 = "MOVIE_ID";
    public static final String COL_2 = "MOVIE_TITLE";
    public static final String COL_3 = "MOVIE_DATE";
    public static final String COL_4 = "MOVIE_RATE";
    public static final String COL_5 = "MOVIE_TYPE";
    public static final String COL_6 = "MOVIE_OVERVIEW";
    public static final String COL_7 = "MOVIE_POSTER";
    public static final String COL_8 = "MOVIE_BackDrop";

    public static final String DATABASE_NAME = "MovieApp.db";
    public static final String TABLE_NAME = "movies";
    public static final int DATABASE_VERSION = 1;
    public static final String CREATE_DB_TABLE="CREATE TABLE " + TABLE_NAME +" (MOVIE_ID  TEXT PRIMARY KEY," +
            "MOVIE_TITLE    TEXT," +
            "MOVIE_DATE     TEXT," +
            "MOVIE_RATE     TEXT," +
            "MOVIE_TYPE     TEXT," +
            "MOVIE_OVERVIEW TEXT," +
            "MOVIE_POSTER   TEXT," +
            "MOVIE_BackDrop TEXT)";
    ////////////////
    public class DataBase extends SQLiteOpenHelper {
        public DataBase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(db);
        }
        public boolean insertMovie(String id, String title, String date, String rate,String type,String overView,String poster,String backdrop) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_1,id);
            contentValues.put(COL_2,title);
            contentValues.put(COL_3,date);
            contentValues.put(COL_4,rate);
            contentValues.put(COL_5,type);
            contentValues.put(COL_6,overView);
            contentValues.put(COL_7,poster);
            contentValues.put(COL_8,backdrop);
            long result = db.insert(TABLE_NAME,null ,contentValues);
            if(result == -1)
                return false;
            else
                return true;
        }
        public Cursor getAllMovies(){
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor res=db.rawQuery("select * from "+TABLE_NAME, null);
            return res;
        }
        public Cursor findMovie(String id){
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor res=db.rawQuery("select * from "+TABLE_NAME+" where MOVIE_ID="+ id, null);
            return res;
        }
        public Integer deleteMovie(String id) {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_NAME, "MOVIE_ID = ?", new String[] {id});
        }
    }
    ////////////////
    @Override
    public boolean onCreate() {
        Context context=getContext();
        DataBase dataBase=new DataBase(context);
        db=dataBase.getWritableDatabase();
        return (db == null)? false : true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {

        SQLiteQueryBuilder builder=new SQLiteQueryBuilder();
        builder.setTables(TABLE_NAME);

        if (s==null){
            Cursor cursor=builder.query(db,null,null,null,null,null,null);
            cursor.setNotificationUri(getContext().getContentResolver(),uri);
            return cursor;
        }else {
            Cursor cursor=builder.query(db,null,s,null,null,null,null);
            cursor.setNotificationUri(getContext().getContentResolver(),uri);
            return cursor;
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues Values) {
        long row=db.insert(TABLE_NAME,"",Values);
        if (row > 0){
            Uri _uri= ContentUris.withAppendedId(CONTENT_URL,row);
            getContext().getContentResolver().notifyChange(_uri,null);
            return _uri;
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int count =0;
        count=db.delete(TABLE_NAME,s,null);
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}