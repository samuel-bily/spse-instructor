package com.bily.samuel.spseinstructor.lib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by samuel on 2.2.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "quiz";
    private static final String TABLE_USER = "user";

    private static final String KEY_ID = "id";
    private static final String KEY_IDU = "id_u";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER +  " (" + KEY_ID + " INTEGER PRIMARY KEY,"+ KEY_IDU + " INTEGER," + KEY_NAME + " TEXT," + KEY_EMAIL + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void storeUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IDU, user.getIdu());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_NAME, user.getName());
        db.insert(TABLE_USER, null, values);
    }

    public User getUser(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE 1", null);
        User user = new User();
        try {
            if (c.moveToFirst()) {
                user.setIdu(c.getInt(c.getColumnIndex(KEY_IDU)));
                user.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                user.setEmail(c.getString(c.getColumnIndex(KEY_EMAIL)));
                c.close();
                return user;
            }
        }catch (IllegalStateException e){
            Log.e("IllegalStateException", e.toString());
        }
        c.close();
        return null;
    }

    public void dropTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }
}
