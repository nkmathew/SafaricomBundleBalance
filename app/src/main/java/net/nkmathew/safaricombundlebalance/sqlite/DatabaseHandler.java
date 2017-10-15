package net.nkmathew.safaricombundlebalance.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.nkmathew.safaricombundlebalance.utils.Utils;

import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private static final String SQL_LATEST_RECORD =
            "SELECT * FROM " + TABLENAME + " ORDER BY " + KEY_TIME_RECORDED + " ASC";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
     * @param dataBundle
     */
    public void saveBundleData(DataBundle dataBundle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DAILY_DATA, dataBundle.getDailyData());
        values.put(KEY_LASTING_DATA, dataBundle.getLastingData());
        values.put(KEY_TIME_RECORDED, dataBundle.getTimeRecorded());
        db.insert(TABLENAME, null, values);
        db.close();
    }


    /**
     * Returns all the records from the db
     *
     * @return
     */
    public List<DataBundle> getAllRecords() {

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(SQL_ALL_RECORDS, null);

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
     * Returns the most recent record
     *
     * @return
     */
    public DataBundle getLatestBundle() {
        return getFirstRecord(SQL_LATEST_RECORD);
    }


    /**
     * Run query and fetch the first record returned
     *
     * @param query
     * @return
     */
    private DataBundle getFirstRecord(final String query) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        boolean hasRecord = cursor.moveToFirst();
        Log.d("msg", "First record: " + cursor.getCount() + " ==> " + query);
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
    public DataBundle getBundleXMinutesAgo(int minutes) {
        Log.d("msg", "Current time: " + Utils.sqlDateTime(new Date()));
        Date targetDate = DateUtils.addMinutes(new Date(), -minutes);

        final String query = "SELECT * FROM " + TABLENAME + " WHERE " +
                KEY_TIME_RECORDED + " > '" + Utils.sqlDateTime(targetDate) + "' ORDER BY " +
                KEY_TIME_RECORDED + " ASC";

        return getFirstRecord(query);
    }


    /**
     * Get count of the records in the db
     *
     * @return
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
     * @return
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
    public void deleteOldRecords() {
        final String deleteQuery = "DELETE FROM " + TABLENAME + " WHERE " + KEY_TIME_RECORDED +
                " <= date('now', '-3 day')";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(deleteQuery, null);
        cursor.close();
    }
}