package com.cloqi.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cloqi.youpayframework.Currency;
import com.cloqi.youpayframework.Expense;
import com.cloqi.youpayframework.Person;
import com.cloqi.youpayframework.YouPayEvent;
import com.cloqi.youpayimpl.CurrencyImpl;
import com.cloqi.youpayimpl.ExpenseImpl;
import com.cloqi.youpayimpl.PersonImpl;
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
    private static final int DATABASE_VERSION = 12;
    private static final String DATABASE_NAME = "cloqi";
    private static final String TABLE_LOGIN = "login";
    private static final String TABLE_EVENT = "events";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_EVENT_USER = "event_user";
    private static final String TABLE_EXPENSE = "expenses";
    private static final String TABLE_EXPENSE_USER = "expense_user";

    private static final String KEY_LOGIN_ID = "id";
    public static final String KEY_LOGIN_DB_ID = "login_id";
    public static final String KEY_LOGIN_NAME = "login_name";
    public static final String KEY_LOGIN_EMAIL = "login_email";
    public static final String KEY_LOGIN_TOKEN = "token";

    public static final String KEY_EVENT_ID = "id";
    public static final String KEY_EVENT_DB_ID = "event_id";
    public static final String KEY_EVENT_NAME = "event_name";
    public static final String KEY_EVENT_CURRENCY = "event_currency";
    public static final String KEY_EVENT_COLOR = "event_color";

    public static final String KEY_USER_ID = "id";
    public static final String KEY_USER_DB_ID = "user_id";
    public static final String KEY_USER_REAL_NAME = "user_name";
    public static final String KEY_USER_EMAIL = "user_email";

    public static final String KEY_EVENT_USER_ID = "id";

    public static final String KEY_EXPENSE_ID = "id";
    public static final String KEY_EXPENSE_DB_ID = "expense_id";
    public static final String KEY_EXPENSE_TITLE = "expense_title";
    public static final String KEY_EXPENSE_PAYER_EMAIL = "expense_payer_id";
    public static final String KEY_EXPENSE_AMOUNT = "expense_amount";
    public static final String KEY_EXPENSE_CURRENCY = "expense_currency";

    public static final String KEY_EXPENSE_USER = "id";

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
                + KEY_LOGIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_LOGIN_NAME + " TEXT,"
                + KEY_LOGIN_EMAIL + " TEXT UNIQUE,"
                + KEY_LOGIN_TOKEN + " TEXT,"
                + KEY_LOGIN_DB_ID + " TEXT" + ");";
        db.execSQL(CREATE_LOGIN_TABLE);
        String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_EVENT + "("
                + KEY_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EVENT_DB_ID + " TEXT UNIQUE,"
                + KEY_EVENT_NAME + " TEXT,"
                + KEY_EVENT_CURRENCY + " TEXT,"
                + KEY_EVENT_COLOR + " TEXT"
                + ");";
        db.execSQL(CREATE_EVENT_TABLE);
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_DB_ID + " TEXT UNIQUE,"
                + KEY_USER_REAL_NAME + " TEXT,"
                + KEY_USER_EMAIL + " TEXT UNIQUE"
                + ");";
        db.execSQL(CREATE_USERS_TABLE);
        String CREATE_EVENT_USER_TABLE = "CREATE TABLE " + TABLE_EVENT_USER + "("
                + KEY_EVENT_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EVENT_DB_ID + " TEXT UNIQUE,"
                + KEY_USER_DB_ID + " TEXT UNIQUE"
                + ");";
        db.execSQL(CREATE_EVENT_USER_TABLE);
        String CREATE_EXPENSE_TABLE = "CREATE TABLE " + TABLE_EXPENSE + "("
                + KEY_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EXPENSE_DB_ID + " TEXT UNIQUE,"
                + KEY_EXPENSE_TITLE + " TEXT,"
                + KEY_EXPENSE_PAYER_EMAIL + " TEXT,"
                + KEY_EXPENSE_AMOUNT + " INTEGER,"
                + KEY_EXPENSE_CURRENCY + " TEXT,"
                + KEY_EVENT_DB_ID + " TEXT"
                + ");";
        db.execSQL(CREATE_EXPENSE_TABLE);
        String CREATE_EXPENSE_USER_TABLE = "CREATE TABLE " + TABLE_EXPENSE_USER + "("
                + KEY_EVENT_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EXPENSE_DB_ID + " STRING UNIQUE,"
                + KEY_USER_DB_ID + " STRING UNIQUE"
                + ");";
        db.execSQL(CREATE_EXPENSE_USER_TABLE);
        Log.d(TAG, "Database tables created");
    }

    //Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE_USER);
        //Create tables again
        onCreate(db);
    }

    /**
     * ****************************************
     * **************** LOGIN *****************
     * ****************************************
     */

    /**
     * Storing user details in database
     */
    public void addLogin(String id, String name, String email, String token) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOGIN_DB_ID, id);
        values.put(KEY_LOGIN_NAME, name);
        values.put(KEY_LOGIN_EMAIL, email);
        values.put(KEY_LOGIN_TOKEN, token);

        long db_id = db.insert(TABLE_LOGIN, null, values); //inserting row
        db.close(); //Closing database connection
        Log.d(TAG, "New user inserted into sqlite: " + db_id);
    }

    /**
     * Storing user details from JSON object
     */
    public void addLogin(JSONObject json) {
        //User successfully stored in MySQL, now store the user in sqlite
        try {
            String token = json.getString("token");
            JSONObject user = json.getJSONObject("user");
            String id = user.getString("id");
            String name = user.getString("name");
            String email = user.getString("email");
            //Inserted row in users table
            addLogin(id, name, email, token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setToken(String token, String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOGIN_TOKEN, token);
        String whereClause = KEY_LOGIN_DB_ID + "=" + id;

        long db_id = db.update(TABLE_LOGIN, values, whereClause, null);//Updateing row
        db.close(); //Closing database connection
        Log.d(TAG, "New user inserted into sqlite: " + db_id);
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getLoginDetails() {
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst(); //get first user
        if (cursor.getCount() > 0) {
            user.put(KEY_LOGIN_NAME, cursor.getString(1));
            user.put(KEY_LOGIN_EMAIL, cursor.getString(2));
            user.put(KEY_LOGIN_TOKEN, cursor.getString(3));
            user.put(KEY_LOGIN_DB_ID, cursor.getString(4));
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching user from sqlite: " + user.toString());
        return user;
    }

    /**
     * Delete all users
     */
    public void deleteLoginDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOGIN, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }

    /**
     * ****************************************
     * **************** EVENTS ****************
     * ****************************************
     */


    public void addEvent(String id, String name, String currency, String color) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_DB_ID, id);
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
                    cursor.getString(2),
                    getCurrency(cursor.getString(3)),
                    cursor.getString(4)));
            cursor.moveToNext();
        }
        cursor.close();
        return events;
    }

    public YouPayEvent getEvent(String id) {
        YouPayEvent event = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_EVENT + " WHERE " + KEY_EVENT_DB_ID + "=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{id});
        if (cursor.moveToFirst()) {
            event = new YouPayEventImpl(
                    cursor.getString(cursor.getColumnIndex(KEY_EVENT_DB_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_EVENT_NAME)),
                    getCurrency(cursor.getString(cursor.getColumnIndex(KEY_EVENT_CURRENCY))),
                    cursor.getString(cursor.getColumnIndex(KEY_EVENT_COLOR)));
            /*for (Expense e : getExpenses(id)){
                event.addExpense(e);
            }
            for (Person p: getUsersForEvent(id)){
                event.addPerson(p);
            }*/
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


    /**
     * ****************************************
     * ************** CURRENCY ****************
     * ****************************************
     */

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

    public String[] getCurrencyNames(){
        ArrayList<Currency> currencies = getCurrencies();
        String[] output = new String[currencies.size()];
        for (int i = 0; i < currencies.size(); i++) {
            output[i] = currencies.get(i).getCode();
        }
        return output;
    }

    /**
     * ****************************************
     * **************** USERS *****************
     * ****************************************
     */

    public ArrayList<Person> getUsers(){
        ArrayList<Person> persons = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_USERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            persons.add(new PersonImpl(
                    cursor.getString(2),
                    cursor.getString(3)

            ));
            cursor.moveToNext();
        }
        cursor.close();
        return persons;
    }

    public ArrayList<Person> getUsersForEvent(String id){
        ArrayList<Person> persons = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_USERS + "tu, " + TABLE_EVENT_USER + " teu"
                + " WHERE tu." + KEY_USER_DB_ID + " = teu." + KEY_USER_DB_ID
                + " AND teu." + KEY_EVENT_DB_ID + " = '" + id + "';";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            persons.add(new PersonImpl(
                    cursor.getString(cursor.getColumnIndex(KEY_USER_REAL_NAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_USER_EMAIL))
            ));
            cursor.moveToNext();
        }
        cursor.close();
        return persons;
    }

    public ArrayList<Person> getUsersForExpense(String id){
        ArrayList<Person> persons = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_USERS + "tu, " + TABLE_EXPENSE_USER + " teu"
                + " WHERE tu." + KEY_USER_DB_ID + " = teu." + KEY_USER_DB_ID
                + " AND teu." + KEY_EXPENSE_DB_ID + " = '" + id + "';";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            persons.add(new PersonImpl(
                    cursor.getString(cursor.getColumnIndex(KEY_USER_REAL_NAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_USER_EMAIL))
            ));
            cursor.moveToNext();
        }
        cursor.close();
        return persons;
    }



    public Person getPerson(String email) {
        Person person = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_USER_EMAIL + "=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});
        if (cursor.moveToFirst()) {
            person = new PersonImpl(
                    cursor.getString(cursor.getColumnIndex(KEY_USER_REAL_NAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_USER_EMAIL)));
        }
        cursor.close();
        return person;
    }

    /**
     * ****************************************
     * ************* EXPENSES *****************
     * ****************************************
     */

    public ArrayList<Expense> getExpenses(String id){
        ArrayList<Expense> expenses = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + KEY_EXPENSE_DB_ID + " = '" + id + "';";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            expenses.add(new ExpenseImpl(
                    cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_TITLE)),
                    getPerson(cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_PAYER_EMAIL))),
                    cursor.getInt(cursor.getColumnIndex(KEY_EXPENSE_AMOUNT)),
                    getCurrency(cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_CURRENCY))),
                    getUsersForExpense(id)
            ));
            cursor.moveToNext();
        }
        cursor.close();
        return expenses;
    }

    public Expense getExpense(String id) {
        Expense expense = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + KEY_EXPENSE_DB_ID + "=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{id});
        if (cursor.moveToFirst()) {
            expense = new ExpenseImpl(
                    cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_TITLE)),
                    getPerson(cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_PAYER_EMAIL))),
                    cursor.getInt(cursor.getColumnIndex(KEY_EXPENSE_AMOUNT)),
                    getCurrency(cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_CURRENCY))),
                    getUsersForExpense(id)
            );
        }
        cursor.close();
        return expense;
    }

    public String[] getExpensesTitles(String id){
        ArrayList<Expense> expenses = getExpenses(id);
        String[] output = new String[expenses.size()];
        for (int i = 0; i < expenses.size(); i++) {
            output[i] = expenses.get(i).getTitle();
        }
        return output;
    }

}