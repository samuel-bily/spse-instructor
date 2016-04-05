package com.bily.samuel.spseinstructor.lib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by samuel on 2.2.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "quiz";
    private static final String TABLE_USER = "user";
    private static final String TABLE_STAT_TESTS = "stat_tests";
    private static final String TABLE_STAT_USERS = "stat_users";
    private static final String TABLE_STAT_QUESTIONS = "stat_questions";

    private static final String KEY_ID = "id";
    private static final String KEY_IDU = "id_u";
    private static final String KEY_IDT = "id_t";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ANSWER = "answer";
    private static final String KEY_STAT = "stat";
    private static final String KEY_RIGHT = "right";

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER +  " (" + KEY_ID + " INTEGER PRIMARY KEY,"+ KEY_IDU + " INTEGER," + KEY_NAME + " TEXT," + KEY_EMAIL + " TEXT" + ")";

    private static final String CREATE_TABLE_STAT_TESTS = "CREATE TABLE " + TABLE_STAT_TESTS + " (" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_IDT + " INTEGER," + KEY_NAME + " TEXT," + KEY_STAT + " DOUBLE" + ")";
    private static final String CREATE_TABLE_STAT_USERS = "CREATE TABLE " + TABLE_STAT_USERS + " (" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_IDU + " INTEGER," + KEY_IDT + " INTEGER,"+ KEY_NAME + " TEXT," + KEY_STAT + " DOUBLE" + ")";
    private static final String CREATE_TABLE_STAT_QUESTIONS = "CREATE TABLE " + TABLE_STAT_QUESTIONS + " (" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_IDU + " INTEGER," + KEY_IDT + " INTEGER," + KEY_RIGHT + " INTEGER," + KEY_NAME + " TEXT," + KEY_ANSWER+" TEXT"+")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_STAT_QUESTIONS);
        db.execSQL(CREATE_TABLE_STAT_TESTS);
        db.execSQL(CREATE_TABLE_STAT_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void storeTest(Test test){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IDT, test.getId_t());
        values.put(KEY_NAME, test.getName());
        values.put(KEY_STAT, test.getStat());
        db.insert(TABLE_STAT_TESTS, null, values);
    }

    public void storeUsers(Test test){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IDU, test.getId_t());
        values.put(KEY_NAME, test.getName());
        values.put(KEY_STAT, test.getStat());
        values.put(KEY_IDT, test.getId());
        db.insert(TABLE_STAT_USERS, null, values);
    }

    public void changeName(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME,name);
        db.update(TABLE_USER,cv,"1",new String[]{});
    }

    public void storeQuestions(Question q){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, q.getName());
        values.put(KEY_IDT, q.getIdT());
        values.put(KEY_IDU, q.getIdU());
        values.put(KEY_ANSWER, q.getStat());
        values.put(KEY_RIGHT, q.getRight());
        db.insert(TABLE_STAT_QUESTIONS, null, values);
    }

    public void storeUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IDU, user.getIdu());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_NAME, user.getName());
        db.insert(TABLE_USER, null, values);
    }

    public ArrayList<Test> getTests(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Test> tests = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_STAT_TESTS,null);
        for(int i = 0; i < c.getCount(); i++){
            c.moveToPosition(i);
            Test t = new Test();
            t.setId_t(c.getInt(c.getColumnIndex(KEY_IDT)));
            t.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            t.setStat(c.getDouble(c.getColumnIndex(KEY_STAT)));
            tests.add(t);
        }
        c.close();
        return tests;
    }

    public ArrayList<Test> getUsers(int idt){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Test> tests = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_STAT_USERS + " WHERE " + KEY_IDT + " = " + idt,null);
        for(int i = 0; i < c.getCount(); i++){
            c.moveToPosition(i);
            Test t = new Test();
            t.setId_t(c.getInt(c.getColumnIndex(KEY_IDU)));
            t.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            t.setStat(c.getDouble(c.getColumnIndex(KEY_STAT)));
            tests.add(t);
        }
        c.close();
        return tests;
    }

    public ArrayList<Question> getQuestions(int idt, int idu){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Question> questions = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_STAT_QUESTIONS + " WHERE " + KEY_IDT + " = " + idt + " AND " + KEY_IDU + " = " + idu,null);
        for(int i = 0; i < c.getCount(); i++){
            c.moveToPosition(i);
            Question q = new Question();
            q.setRight(c.getInt(c.getColumnIndex(KEY_RIGHT)));
            q.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            q.setStat(c.getString(c.getColumnIndex(KEY_ANSWER)));
            questions.add(q);
        }
        c.close();
        return questions;
    }

    public void dropTests(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STAT_TESTS, null, null);
    }
    public void dropUsers(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STAT_USERS, KEY_IDT + "=" + id, null);
    }
    public void dropQuestions(int idt, int idu){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STAT_QUESTIONS, KEY_IDT + "=" + idt + " AND " + KEY_IDU + " = " + idu , null);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAT_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAT_TESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAT_USERS);
        onCreate(db);
    }
}
