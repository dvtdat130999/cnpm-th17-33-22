package com.example.pdfscanner.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "userManager";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "users";

    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    public static final String SQL_TABLE_USERS = " CREATE TABLE " + TABLE_NAME
            + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_USERNAME + " TEXT, "
            + KEY_PASSWORD + " TEXT"
            + " ) ";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       // String create_users_table = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT)", TABLE_NAME, KEY_ID, KEY_USERNAME, KEY_PASSWORD);
        db.execSQL(SQL_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_users_table = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(drop_users_table);

        onCreate(db);
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

//    public User getUser(User userUsername) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_NAME, null, KEY_USERNAME + " = ?", new String[] { String.valueOf(userUsername) },null, null,null);
//        if(cursor != null)
//            cursor.moveToFirst();
//        User user = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
//        return user;
//    }

    public User Authenticate(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, KEY_USERNAME + " = ?", new String[] { String.valueOf(username) },null, null,null);

        if (cursor != null && cursor.moveToFirst()&& cursor.getCount()>0) {
            //if cursor has value then in user database there is user associated
            User user1 = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2));

            //Match both passwords check they are same or not
            if (password.equalsIgnoreCase(user1.getPassword())) {
                return user1;
            }
        }

        //if user password does not matches or there is no record with that email then return @false
        return null;
    }

     public List<User> getAllUsers() {
        List<User>  userList = new ArrayList<>();
        String query = "SELECT * FROM  " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false) {
            User user = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            userList.add(user);
            cursor.moveToNext();
        }
        return userList;
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());

        db.update(TABLE_NAME, values, KEY_ID + " = ?", new String[] { String.valueOf(user.getId()) });
        db.close();
    }

    public void deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?", new String[] { String.valueOf(userId) });
        db.close();
    }
//    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){}
//    public Cursor rawQuery(String sql, String[] selectionArgs){}
}