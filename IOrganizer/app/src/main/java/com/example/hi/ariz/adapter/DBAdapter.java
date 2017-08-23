package com.example.hi.ariz.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by HI on 17-July-17.
 */
public class DBAdapter {

    static final String KEY_ROWID = "_id";
    static final String KEY_ALBUMNAME = "album_name";
    static final String KEY_DATE = "date";
    static final String KEY_PICNAME = "pic_name";
    static final String TAG = "DBAdapter";
    static final String DATABASE_NAME = "MyDB";
    static final String DATABASE_TABLE = "Albums";
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_CREATE =
            "create table Albums (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "album_name text not null,pic_name text not null);";
    Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
       DBHelper = new DatabaseHelper(context);
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context,DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            try {
                sqLiteDatabase.execSQL(DATABASE_CREATE);

            } catch (SQLException e) {
                e.printStackTrace();

            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Albums");
            onCreate(sqLiteDatabase);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException
    {
     //  DBHelper = new DatabaseHelper(context);
        db = DBHelper.getWritableDatabase();
        return this;
    }
    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---insert a pic into the database---
    public long insertPic(String albumName,String picName)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ALBUMNAME, albumName);
        initialValues.put(KEY_PICNAME, picName);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular reminder---
    public boolean deletepic(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }


	//---retrieves all the pics in a album---
    public Cursor getPicsInAlbum(String albumName) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE,  new String[] {KEY_ROWID,KEY_ALBUMNAME,KEY_PICNAME}, KEY_ALBUMNAME + "='" +albumName+"'", null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---retrieves pic in a album---
    public Cursor getPic(String picName) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE,  new String[] {KEY_ROWID,KEY_ALBUMNAME,KEY_PICNAME}, KEY_PICNAME + "='" +picName+"'", null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

   
}
