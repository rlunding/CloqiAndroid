package com.cloqi.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 *
 * Created by Lunding on 17/04/15.
 */
public class SQLiteHandler extends SQLiteOpenHelper {

    //Constants
    public static final String TAG = SQLiteHandler.class.getSimpleName();
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "cloqi";
    private static final String TABLE_LOGIN = "login";

    private static final String KEY_ID = "id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_NAME = "user_name";
    public static final String KEY_EMAIL = "user_email";
    public static final String KEY_TOKEN = "token";

    //Fields


    public SQLiteHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creating tables
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_TOKEN + " TEXT,"
                + KEY_USER_ID + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        Log.d(TAG, "Database tables created");
    }

    //Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        //Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String id, String name, String email, String token){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, id);
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_TOKEN, token);

        long db_id = db.insert(TABLE_LOGIN, null, values); //inserting row
        db.close(); //Closing database connection
        Log.d(TAG, "New user inserted into sqlite: " + db_id);
    }

    /**
     * Storing user details from JSON object
     */
    public void addUser(JSONObject json){
        //User successfully stored in MySQL, now store the user in sqlite
        try {
            String token = json.getString("token");
            JSONObject user = json.getJSONObject("user");
            String id = user.getString("id");
            String name = user.getString("name");
            String email = user.getString("email");
            //Inserted row in users table
            addUser(id, name, email, token);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void setToken(String token){
        //TODO:: update token insert
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst(); //get first user
        if (cursor.getCount() > 0){
            user.put(KEY_NAME, cursor.getString(1));
            user.put(KEY_EMAIL, cursor.getString(2));
            user.put(KEY_TOKEN, cursor.getString(3));
            user.put(KEY_USER_ID, cursor.getString(4));
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching user from sqlite: " + user.toString());
        return user;
    }

    /**
     * Getting user login status return true if rows are there in table
     */
    public int getRowCount(){
        String countQuery = "SELECT * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        cursor.close();
        db.close();
        return rowCount;
    }

    /**
     * Delete all tables and create them again
     */
    public void deleteUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOGIN, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }
}
