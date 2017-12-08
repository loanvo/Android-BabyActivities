package cs175.babysactivities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Loan Vo on 11/30/17.
 */

public class DBHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "BabyActivities";

    // Table Profile
    private static final String TABLE_PROFILE = "baby_profile";
    private static final String KEY_NAME = "name";
    private static final String KEY_DOB = "DOB";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_HEAD_SIZE = "head_size";

    //Table Bottle Feed
    private static final String TABLE_BOTTLE_FEED="bottle_feeding";
    private static final String B_NAME = "name";
    private static final String KEY_TIME = "time";
    private static final String KEY_QUANTITY= "quantity";


    // Table Breast feed
    private static final String TABLE_BREAST_FEED = "Breast_Feeding";
    private static final String BR_NAME = "name";
    private static final String KEY_LEFT_TIME = "lefttime";
    private static final String KEY_RIGHT_TIME = "righttime";

    // Table Diaper
    private static final String TABLE_DIAPER = "Diaper";
    private static final String D_NAME = "name";
    private static final String KEY_TYPE = "type";


    // Table Sleep
    private static final String TABLE_SLEEP = "Sleep";
    private static final String S_NAME = "name";
    private static final String KEY_SLEEP_TIME = "time";

    // Table Walk
    private static final String TABLE_WALK = "Walk";
    private static final String W_NAME = "name";
    private static final String KEY_WALK_TIME = "time";
    private static final String KEY_ORIGINAL_STEPS = "orginal_steps";
    private static final String KEY_CURRENT_STEPS = "current_steps";
    private static final String KEY_STATUS = "walking_status";

    // Table Supply
    private static final String TABLE_SUPPLY = "Supply";
    private static final String SU_NAME = "name";
    private static final String KEY_DIAPER = "diaper";
    private static final String KEY_FORMULA = "formula";
    private static final String KEY_DATE = "date";

    // Table activity tracker
    private static final String TABLE_TRACKER = "tracker";
    private static final String TR_NAME = "name";
    private static final String START_TYPE = "start_type";
    private static final String STOP_TYPE = "stop_type";
    private static final String START_TIME = "start";
    private static final String STOP_TIME = "stop";

    // Table Log
    private static final String TABLE_LOG = "log_table";
    private static final String LOG_ID = "id";
    private static final String L_NAME = "name";
    private static final String LOG = "logs";
    private static final String LOG_DATE = "logs_date";

    ActivityLog activityLog;


    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String querry1 = ("CREATE TABLE " + TABLE_PROFILE + "(" +
                KEY_NAME + " TEXT, " + KEY_DOB + " TEXT, " + KEY_GENDER + " TEXT, "
                + KEY_WEIGHT + " DOUBLE, " + KEY_HEIGHT + " DOUBLE, "+ KEY_HEAD_SIZE +  " DOUBLE)");
        db.execSQL (querry1);

        String querry2 = ("CREATE TABLE " + TABLE_BOTTLE_FEED + "(" + B_NAME + " TEXT, " +
                KEY_TIME + " TEXT, " + KEY_QUANTITY + " INT, " +  " FOREIGN KEY (" + B_NAME + ") REFERENCES "
                + TABLE_PROFILE + " (" + KEY_NAME + "))");
        db.execSQL (querry2);

        String querry3 = ("CREATE TABLE " + TABLE_BREAST_FEED + "(" + BR_NAME + " TEXT, " +
                KEY_LEFT_TIME + " TEXT, " + KEY_RIGHT_TIME + " TEXT, " +  " FOREIGN KEY (" + BR_NAME + ") REFERENCES "
                + TABLE_PROFILE + "(" + KEY_NAME + "))");
        db.execSQL (querry3);

        String querry4 = ("CREATE TABLE " + TABLE_DIAPER + "(" + D_NAME + " TEXT, " +
                KEY_TYPE + " TEXT, "+  " FOREIGN KEY (" + D_NAME + ") REFERENCES "
                + TABLE_PROFILE + "(" + KEY_NAME + "))");
        db.execSQL (querry4);

        String querry5 = ("CREATE TABLE " + TABLE_SLEEP + "(" + S_NAME + " TEXT, " +
                KEY_SLEEP_TIME + " TEXT, "  +  " FOREIGN KEY (" + S_NAME + ") REFERENCES "
                + TABLE_PROFILE + "(" + KEY_NAME + "))");
        db.execSQL (querry5);

        String querry6 = ("CREATE TABLE " + TABLE_WALK + "(" + W_NAME + " TEXT, " +
                KEY_WALK_TIME + " TEXT, " + KEY_ORIGINAL_STEPS + " INT, " + KEY_CURRENT_STEPS + " INT, "
                + KEY_STATUS + " TEXT, " +  " FOREIGN KEY (" + W_NAME + ") REFERENCES "
                + TABLE_PROFILE + "(" + KEY_NAME + "))");
        db.execSQL (querry6);

        String querry7 = ("CREATE TABLE " + TABLE_SUPPLY + "("
                + KEY_DIAPER + " INT, " + KEY_FORMULA + " INT, " + KEY_DATE +  " TEXT" +  ")");
        db.execSQL (querry7);

        String querry8 = ("CREATE TABLE " + TABLE_TRACKER + "(" + TR_NAME + " TEXT, " +
                START_TYPE + " TEXT, " + START_TIME + " TEXT," + STOP_TYPE +" TEXT, " + STOP_TIME +
                " TEXT, FOREIGN KEY (" + SU_NAME + ") REFERENCES " + TABLE_PROFILE + "(" + KEY_NAME + "))");
        db.execSQL (querry8);

        String querry9 = ("CREATE TABLE " + TABLE_LOG + "(" + LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                L_NAME + " TEXT, " + LOG + " TEXT, " +   LOG_DATE + " TEXT, " +" FOREIGN KEY (" + L_NAME + ") REFERENCES "
                + TABLE_PROFILE + "(" + KEY_NAME + "))");
        db.execSQL (querry9);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int version1, int version2) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIAPER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOTTLE_FEED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BREAST_FEED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SLEEP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUPPLY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);

        onCreate(db);
    }

    public void createProfile(BabyProfile babyProfile){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, babyProfile.getName());
        values.put(KEY_DOB, babyProfile.getDOB().toString());
        values.put(KEY_HEIGHT, babyProfile.getHeight());
        values.put(KEY_WEIGHT, babyProfile.getWeight());
        values.put(KEY_HEAD_SIZE, babyProfile.getHeadSize());

        db.insert(TABLE_PROFILE, null, values);
        db.close();
    }



    public void removeBabyProfile(String name){
        String selectQuery = "DELETE FROM " + TABLE_PROFILE + " WHERE " + KEY_NAME + " = " + "'" + name + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(selectQuery);
    }

    public BabyProfile getBabyInfo(){
        String selectQuery = "SELECT * FROM " + TABLE_PROFILE;
        BabyProfile babyProfile = new BabyProfile();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do {
                babyProfile.setName(cursor.getString(0));
                babyProfile.setDOB(cursor.getString(1));
                babyProfile.setWeight(cursor.getDouble(3));
                babyProfile.setHeight(cursor.getDouble(4));
                babyProfile.setHeadsize(cursor.getDouble(5));
            }while(cursor.moveToNext());
        }
        return babyProfile;
    }

    public BabyProfile getBabyInfoByName(String name){
        String selectQuery = "SELECT * FROM " + TABLE_PROFILE + " WHERE " + KEY_NAME + " = " + name;
        BabyProfile babyProfile = new BabyProfile();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do {
                babyProfile.setName(cursor.getString(0));
                babyProfile.setDOB(cursor.getString(1));
                babyProfile.setWeight(cursor.getDouble(3));
                babyProfile.setHeight(cursor.getDouble(4));
                babyProfile.setHeadsize(cursor.getDouble(5));
            }while(cursor.moveToNext());
        }
        return babyProfile;
    }

    public String getBabyName(){
        String name= "";
        String query = "SELECT " + KEY_NAME + " FROM " + TABLE_PROFILE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                name = cursor.getString(0);
            }
        }
        return name;
    }

    public void insertBottleFeedTime(ActivityData data, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(B_NAME, name);
        values.put(KEY_TIME, data.getBottleTime());
        values.put(KEY_QUANTITY, data.getQuanity());
        db.insert(TABLE_BOTTLE_FEED, null, values);
        db.close();
    }

    public void insertStatus(ActivityData data, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(B_NAME, name);
        values.put(START_TYPE, data.getStartType());
        values.put(START_TIME, data.getStart());
        values.put(STOP_TYPE, data.getStopType());
        values.put(STOP_TIME, data.getStop());

        db.insert(TABLE_TRACKER, null, values);
        db.close();
    }

    public void updateStatus(String startType, ActivityData data, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, name);
        values.put(STOP_TYPE, data.getStopType());
        values.put(STOP_TIME, data.getStop().toString());

        db.update(TABLE_TRACKER, values, START_TYPE + "=?", new String[]{startType});
        db.close();
    }

    public void removeStatus(String type){
        String query = "DELETE FROM " + TABLE_TRACKER + " WHERE " + START_TYPE + " = " + '"' + type +'"';
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    public ActivityData getStatus(){
        ActivityData data = new ActivityData();
        String query = "SELECT " + START_TYPE + " , " + START_TIME + " FROM " + TABLE_TRACKER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    data.setStartType(cursor.getString(0));
                    data.setStart(cursor.getString(1));
                } while (cursor.moveToNext());
            }
        }
        return data;
    }

    public void insetLog(ActivityLog activityLog){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(L_NAME, activityLog.getName());
        values.put(LOG, activityLog.getLog());
        values.put(LOG_DATE, activityLog.getLogDate());
        db.insert(TABLE_LOG, null, values);
        db.close();
    }

    public LinkedList<String> getLog(){
        LinkedList<String> logs = new LinkedList<>();
        ActivityLog activityLog = new ActivityLog();
        String query = "SELECT " + LOG + " FROM " + TABLE_LOG + " ORDER BY " + LOG_ID + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    String aLog = cursor.getString(0);
                    activityLog.setLog(aLog);
                    logs.addLast(aLog);
                } while (cursor.moveToNext());
            }
        }
        return logs;
    }

    public List<ActivityLog> getAllLog(){
        List<ActivityLog> logs = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_LOG + " ORDER BY " + LOG_DATE + " ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    activityLog = new ActivityLog();
                    activityLog.setName(cursor.getString(1));
                    activityLog.setLog(cursor.getString(2));
                    activityLog.setLogDate(cursor.getString(3));
                    logs.add(activityLog);
                } while (cursor.moveToNext());
            }
        }
        return logs;
    }

    public void insertSupply(int formula, int diaper){
        DateTime date = new DateTime();
        String dateTime = date.toString(DateTimeFormat.shortDateTime());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DIAPER, diaper);
        values.put(KEY_FORMULA, formula);
        values.put(KEY_DATE, dateTime);

        db.insert(TABLE_SUPPLY, null, values);
        db.close();
    }

    public Supplies getSupplies(){
        Supplies supplies = new Supplies();
        String query = "SELECT * FROM " + TABLE_SUPPLY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                supplies.setDiaper(Integer.parseInt(cursor.getString(0)));
                supplies.setFormula(Integer.parseInt(cursor.getString(1)));
                supplies.setDate(cursor.getString(2));
            }
        }
        return supplies;
    }

    public void updateSupply(int formula, int diaper, String d){
         DateTime date = new DateTime();
         String dateTime = date.toString(DateTimeFormat.shortDateTime());
         SQLiteDatabase db = this.getWritableDatabase();
         String update = "UPDATE " + TABLE_SUPPLY + " SET " + KEY_FORMULA + " = " + formula
                 + ", " + KEY_DIAPER + " = " + diaper + ", " + KEY_DATE + " = '" + dateTime
                 + "' WHERE " + KEY_DATE + " = '" + d + "'";
         db.execSQL(update);
         /*ContentValues values = new ContentValues();
         values.put(KEY_DIAPER, diaper);
         values.put(KEY_FORMULA, formula);
         values.put(KEY_DATE, dateTime);
         db.update(TABLE_SUPPLY, values, null, null);  */

    }

    public void insertDiaper(String type, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(D_NAME, name);
        values.put(KEY_TYPE, type);

        db.insert(TABLE_DIAPER, null, values);
        db.close();
    }

    public List<String> getDiaper(){
        List<String> list = new ArrayList<>();
        String query = "SELECT " + KEY_TYPE + " FROM " + TABLE_DIAPER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    list.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        }
        return list;
    }
    public ArrayList<ActivityData> getBottleFeed(){
        ActivityData data = new ActivityData();
        ArrayList<ActivityData> list = new ArrayList<>();
        String query = "SELECT " + KEY_TIME + " , " + KEY_QUANTITY + " FROM " + TABLE_BOTTLE_FEED;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    data.setBottleTime(Long.parseLong(cursor.getString(0)));
                    data.setQuanity(Integer.parseInt(cursor.getString(1)));
                    list.add(data);
                } while (cursor.moveToNext());
            }
        }
        return list;
    }
    public void insertWalk(String name, int steps){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(W_NAME, name);
        values.put(KEY_ORIGINAL_STEPS, steps);

        db.insert(TABLE_WALK, null, values);
        db.close();
    }

    public int getSteps(){
        int steps = 0;
        String query = "SELECT " + KEY_ORIGINAL_STEPS  + " FROM " + TABLE_WALK;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                steps = (Integer.parseInt(cursor.getString(0)));
            }
        }
        return steps;
    }
/*
    public void insertBreastFeedTime(ActivityData data, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BR_NAME, name);
        values.put(KEY_LEFT_TIME, data.getLeftTime());
        values.put(KEY_RIGHT_TIME, data.getRightTime());
        db.insert(TABLE_BREAST_FEED, null, values);
        db.close();
    }

    public ArrayList<ActivityData> getBreastFeed(){
        ActivityData data = new ActivityData();
        ArrayList<ActivityData> list = new ArrayList<>();
        String query = "SELECT " + KEY_LEFT_TIME + " , " + KEY_RIGHT_TIME + " FROM " + TABLE_BREAST_FEED;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    data.setLeftTime(Long.parseLong(cursor.getString(0)));
                    data.setRightTime(Integer.parseInt(cursor.getString(1)));
                    list.add(data);
                } while (cursor.moveToNext());
            }
        }
        return list;
    }*/
}
