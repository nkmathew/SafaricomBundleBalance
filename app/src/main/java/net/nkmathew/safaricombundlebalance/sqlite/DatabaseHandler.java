package net.nkmathew.safaricombundlebalance.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.nkmathew.safaricombundlebalance.utils.Utils;

import org.apache.commons.lang3.time.DateUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.text.MessageFormat.format;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SafaricomBundleBalance";
    private static final String TABLENAME = "BundleBalanceData";
    private static final String KEY_ID = "id";
    private static final String KEY_DAILY_DATA = "daily_data";
    private static final String KEY_LASTING_DATA = "lasting_data";
    private static final String KEY_TIME_RECORDED = "time_recorded";

    private static final String SQL_CREATE_CONTACTS_TABLE =
            "CREATE TABLE " + TABLENAME + "(" + KEY_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                    KEY_DAILY_DATA + " TEXT, " +
                    KEY_LASTING_DATA + " TEXT, " +
                    KEY_TIME_RECORDED + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLENAME;
    private static final String SQL_TRUNCATE_TABLE = "DELETE FROM " + TABLENAME;
    private static final String SQL_ALL_RECORDS = "SELECT * FROM " + TABLENAME;

    private static final String SQL_LATEST_RECORDS =
            format("SELECT * FROM {0} ORDER BY {1} DESC", TABLENAME, KEY_TIME_RECORDED);


    private Context mContext;


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CONTACTS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }


    /**
     * Save records in db
     *
     * @param dataBundle Bundle data information
     */
    public long saveBundleData(DataBundle dataBundle) {
        List<DataBundle> recentRecords = getBundlesXMinutesAgo(1);
        if (recentRecords.size() > 0) {
            // Disallow multiple insertions per minute
            return -1;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DAILY_DATA, dataBundle.getDailyData());
        values.put(KEY_LASTING_DATA, dataBundle.getLastingData());
        values.put(KEY_TIME_RECORDED, dataBundle.getTimeRecorded());
        long rowID = db.insert(TABLENAME, null, values);
        db.close();
        return rowID;
    }


    /**
     * Returns the specified number of records from the database
     *
     * @return list of recent records
     */
    public List<DataBundle> getRecentRecords(int limit) {
        String query = format("{0} LIMIT {1}", SQL_LATEST_RECORDS, limit);
        return getRecords(query);
    }


    /**
     * Returns all the records from the db
     *
     * @return list containing all the records
     */
    public List<DataBundle> getAllRecords() {
        return getRecords(SQL_ALL_RECORDS);
    }


    /**
     * Get list of all the records returned by the supplied query
     *
     * @return List containing the records
     */
    private List<DataBundle> getRecords(final String query) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        List<DataBundle> listDataBundles = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                DataBundle dataBundle = new DataBundle();
                int id = cursor.getInt(0);
                String dailyData = cursor.getString(1);
                String lastingData = cursor.getString(2);
                String timeRecorded = cursor.getString(3);
                dataBundle.setID(id);
                dataBundle.setDailyData(dailyData);
                dataBundle.setLastingData(lastingData);
                dataBundle.setTimeRecorded(timeRecorded);
                listDataBundles.add(dataBundle);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return listDataBundles;
    }


    /**
     * Run query and fetch the first record returned
     *
     * @param query Query to run
     * @return The first record from the query results
     */
    private DataBundle getFirstRecord(final String query) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        boolean hasRecord = cursor.moveToFirst();
        DataBundle dataBundle = null;

        if (hasRecord) {
            dataBundle = new DataBundle();
            int id = cursor.getInt(0);
            String dailyData = cursor.getString(1);
            String lastingData = cursor.getString(2);
            String timeRecorded = cursor.getString(3);
            dataBundle.setID(id);
            dataBundle.setDailyData(dailyData);
            dataBundle.setLastingData(lastingData);
            dataBundle.setTimeRecorded(timeRecorded);
        }

        cursor.close();
        return dataBundle;
    }


    /**
     * Get the bundle balance one hour ago
     */
    public List<DataBundle> getBundlesXMinutesAgo(int minutes) {
        minutes = Math.abs(minutes);
        Date targetDate = DateUtils.addMinutes(new Date(), -minutes);

        final String query = String.format("SELECT * FROM %s WHERE %s > '%s' ORDER BY %s ASC",
                TABLENAME, KEY_TIME_RECORDED, Utils.sqlDateTime(targetDate, mContext),
                KEY_TIME_RECORDED);

        return getRecords(query);
    }


    /**
     * Get count of the records in the db
     *
     * @return The number of records in the database
     */
    public int getRecordCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_ALL_RECORDS, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }


    /**
     * Delete all records from the table
     *
     * @return number of records deleted
     */
    public int truncateTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SQL_TRUNCATE_TABLE, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }


    /**
     * Delete old day records
     */
    public int deleteOldRecords() {
        String dateTime = Utils.sqlDateTime(DateUtils.addDays(new Date(), -3), mContext);
        String query = MessageFormat.format("{0} <= ?", KEY_TIME_RECORDED);
        SQLiteDatabase database = this.getWritableDatabase();
        return database.delete(TABLENAME, query, new String[]{dateTime});
    }


    /**
     * Delete a single record
     */
    public int deleteRecord(DataBundle bundle) {
        return deleteRecord(bundle.getID());
    }


    /**
     * Delete a single record by its ID
     */
    public int deleteRecord(int id) {
        String query = MessageFormat.format("{0} = ?", KEY_ID);
        SQLiteDatabase database = this.getWritableDatabase();
        return database.delete(TABLENAME, query, new String[]{String.valueOf(id)});
    }
}
