package com.cloqi.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cloqi.youpayframework.Currency;
import com.cloqi.youpayframework.YouPayEvent;
import com.cloqi.youpayimpl.CurrencyImpl;
import com.cloqi.youpayimpl.YouPayEventImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * Created by Lunding on 17/04/15.
 */
public class SQLiteHandler extends SQLiteOpenHelper {

    //Constants
    public static final String TAG = SQLiteHandler.class.getSimpleName();
    private static final int DATABASE_VERSION = 9;
    private static final String DATABASE_NAME = "cloqi";
    private static final String TABLE_LOGIN = "login";
    private static final String TABLE_EVENT = "event";

    private static final String KEY_USER_ID = "id";
    public static final String KEY_USER_DB_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_TOKEN = "token";

    public static final String KEY_EVENT_ID = "id";
    public static final String KEY_EVENT_NAME = "event_name";
    public static final String KEY_EVENT_CURRENCY = "currency";
    public static final String KEY_EVENT_COLOR = "color";

    //Fields


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        //TODO:: REMOVE THIS SHIT
        currencies = new ArrayList<>();
        currencies.add(new CurrencyImpl("DKK", "Dansk krone", 100));
        currencies.add(new CurrencyImpl("SEK", "Svensk krone", 80));
        currencies.add(new CurrencyImpl("NOK", "Norsk krone", 90));
        currencies.add(new CurrencyImpl("USD", "Amerikansk dollar", 600));
        currencies.add(new CurrencyImpl("GBR", "Britisk pund", 900));
    }

    //Creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_NAME + " TEXT,"
                + KEY_USER_EMAIL + " TEXT UNIQUE,"
                + KEY_USER_TOKEN + " TEXT,"
                + KEY_USER_DB_ID + " TEXT" + ");";
        db.execSQL(CREATE_LOGIN_TABLE);
        String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_EVENT + "("
                + KEY_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EVENT_NAME + " TEXT,"
                + KEY_EVENT_CURRENCY + " TEXT,"
                + KEY_EVENT_COLOR + " TEXT"
                + ");";
        db.execSQL(CREATE_EVENT_TABLE);
        Log.d(TAG, "Database tables created");
    }

    //Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        //Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String id, String name, String email, String token) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_DB_ID, id);
        values.put(KEY_USER_NAME, name);
        values.put(KEY_USER_EMAIL, email);
        values.put(KEY_USER_TOKEN, token);

        long db_id = db.insert(TABLE_LOGIN, null, values); //inserting row
        db.close(); //Closing database connection
        Log.d(TAG, "New user inserted into sqlite: " + db_id);
    }

    /**
     * Storing user details from JSON object
     */
    public void addUser(JSONObject json) {
        //User successfully stored in MySQL, now store the user in sqlite
        try {
            String token = json.getString("token");
            JSONObject user = json.getJSONObject("user");
            String id = user.getString("id");
            String name = user.getString("name");
            String email = user.getString("email");
            //Inserted row in users table
            addUser(id, name, email, token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setToken(String token, String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_TOKEN, token);
        String whereClause = KEY_USER_DB_ID + "=" + id;

        long db_id = db.update(TABLE_LOGIN, values, whereClause, null);//Updateing row
        db.close(); //Closing database connection
        Log.d(TAG, "New user inserted into sqlite: " + db_id);
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst(); //get first user
        if (cursor.getCount() > 0) {
            user.put(KEY_USER_NAME, cursor.getString(1));
            user.put(KEY_USER_EMAIL, cursor.getString(2));
            user.put(KEY_USER_TOKEN, cursor.getString(3));
            user.put(KEY_USER_DB_ID, cursor.getString(4));
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching user from sqlite: " + user.toString());
        return user;
    }

    /**
     * Getting user login status return true if rows are there in table
     */
    public int getRowCount() {
        String countQuery = "SELECT * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        cursor.close();
        db.close();
        return rowCount;
    }

    /**
     * Delete all users
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOGIN, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }

    /**
     * **************** EVENTS ****************
     */


    public void addEvent(String name, String currency, String color) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_NAME, name);
        values.put(KEY_EVENT_CURRENCY, currency);
        values.put(KEY_EVENT_COLOR, color);

        long db_id = db.insert(TABLE_EVENT, null, values); //inserting row
        db.close(); //Closing database connection
        Log.d(TAG, "New event inserted into sqlite: " + db_id);
    }


    public ArrayList<YouPayEvent> getEvents() {
        ArrayList<YouPayEvent> events = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            events.add(new YouPayEventImpl(
                    cursor.getString(1),
                    getCurrency(cursor.getString(2)),
                    cursor.getString(3)));
            cursor.moveToNext();
        }
        cursor.close();
        return events;
    }

    public YouPayEvent getEvent(int id) {
        YouPayEvent event = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_EVENT + " WHERE " + KEY_EVENT_ID + "=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            event = new YouPayEventImpl(
                    cursor.getString(1),
                    getCurrency(cursor.getString(2)),
                    cursor.getString(3));
        }
        cursor.close();
        return event;
    }

    /**
     * Delete all events
     */
    public void deleteEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENT, null, null);
        db.close();
        Log.d(TAG, "Deleted all events from sqlite");
    }

    //TODO: remove this shit
    private ArrayList<Currency> currencies;

    public ArrayList<Currency> getCurrencies(){
        return currencies;
    }

    public Currency getCurrency(String code){
        for (Currency c: currencies) {
            if (c.getCode().equals(code)){
                return c;
            }
        }
        return null;
    }

}
