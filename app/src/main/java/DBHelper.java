import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Loan Vo on 11/30/17.
 */

public class DBHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BabyActivities";

    // Table Profile
    private static final String TABLE_PROFILE = "baby_profile";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DOB = "DOB";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_HEAD_SIZE = "head_size";

    //Table Bottle Feed
    private static final String TABLE_BOTTLE_FEED = "Bottle_Feeding";
    private static final String BABY_ID = "id";
    private static final String KEY_TIME = "time";
    private static final String KEY_QUANTITY= "quantity";

    // Table Breast feed
    private static final String TABLE_BREAST_FEED = "Breast_Feeding";
    private static final String BR_ID = "id";
    private static final String KEY_LEFT_TIME = "time";
    private static final String KEY_RIGHT_TIME = "time";

    // Table Diaper
    private static final String TABLE_DIAPER = "Diaper";
    private static final String D_ID = "id";
    private static final String KEY_POO = "poo";
    private static final String KEY_PEE = "pee";
    private static final String KEY_BOTH = "both";

    // Table Sleep
    private static final String TABLE_SLEEP = "Sleep";
    private static final String S_ID = "id";
    private static final String KEY_SLEEP_TIME = "time";

    // Table Walk
    private static final String TABLE_WALK = "Walk";
    private static final String W_ID = "id";
    private static final String KEY_WALK_TIME = "time";
    private static final String KEY_ORIGINAL_STEPS = "orginal_steps";
    private static final String KEY_CURRENT_STEPS = "current_steps";
    private static final String KEY_STATUS = "walking_status";

    // Table Supply
    private static final String TABLE_SUPPLY = "Supply";
    private static final String SU_ID = "id";
    private static final String KEY_DIAPER = "diaper";
    private static final String KEY_FORMULA = "formula";
    private static final String KEY_DATE = "date";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String querry1 = ("CREATE TABLE " + TABLE_PROFILE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_NAME + " TEXT, " + KEY_DOB + " DATE, " + KEY_GENDER + " TEXT, "
                + KEY_WEIGHT + " DOUBLE, " + KEY_HEIGHT + " DOUBLE, "+ KEY_HEAD_SIZE +  " DOUBLE)");
        db.execSQL (querry1);

        String querry2 = ("CREATE TABLE " + TABLE_BOTTLE_FEED + "(" + BABY_ID + " INTEGER, " +
                KEY_TIME + " TEXT, " + KEY_QUANTITY + " INT, " +  " FOREIGN KEY (" + BABY_ID + ") REFERENCES "
                + TABLE_PROFILE + "(" + KEY_ID + ")");
        db.execSQL (querry2);

        String querry3 = ("CREATE TABLE " + TABLE_BREAST_FEED + "(" + BR_ID + " INTEGER, " +
                KEY_LEFT_TIME + " TEXT, " + KEY_RIGHT_TIME + " TEXT, " +  " FOREIGN KEY (" + BR_ID + ") REFERENCES "
                + TABLE_PROFILE + "(" + KEY_ID + ")");
        db.execSQL (querry3);

        String querry4 = ("CREATE TABLE " + TABLE_DIAPER + "(" + D_ID + " INTEGER, " +
                KEY_POO + " INT, " + KEY_PEE + " INT, " + KEY_BOTH + " INT, "+  " FOREIGN KEY (" + D_ID + ") REFERENCES "
                + TABLE_PROFILE + "(" + KEY_ID + ")");
        db.execSQL (querry4);

        String querry5 = ("CREATE TABLE " + TABLE_SLEEP + "(" + S_ID + " INTEGER, " +
                KEY_SLEEP_TIME + " TEXT, "  +  " FOREIGN KEY (" + S_ID + ") REFERENCES "
                + TABLE_PROFILE + "(" + KEY_ID + ")");
        db.execSQL (querry5);

        String querry6 = ("CREATE TABLE " + TABLE_WALK + "(" + W_ID + " INTEGER, " +
                KEY_WALK_TIME + " TEXT, " + KEY_ORIGINAL_STEPS + " DOUBLE, " + KEY_CURRENT_STEPS + " DOUBLE, "
                + KEY_STATUS+ " TEXT, " +  " FOREIGN KEY (" + W_ID + ") REFERENCES "
                + TABLE_PROFILE + "(" + KEY_ID + ")");
        db.execSQL (querry6);

        String querry7 = ("CREATE TABLE " + TABLE_SUPPLY + "(" + SU_ID + " INTEGER, " +
                KEY_DIAPER + " INT, " + KEY_FORMULA + " INT," + KEY_DATE +  " DATE, FOREIGN KEY (" + SU_ID + ") REFERENCES "
                + TABLE_PROFILE + "(" + KEY_ID + ")");
        db.execSQL (querry7);
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
        onCreate(db);
    }


}
