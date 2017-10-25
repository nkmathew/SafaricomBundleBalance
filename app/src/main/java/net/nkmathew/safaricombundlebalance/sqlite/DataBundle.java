package net.nkmathew.safaricombundlebalance.sqlite;

import android.content.Context;

import net.nkmathew.safaricombundlebalance.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Objects;

public class DataBundle {

    private int mId;
    private String mDailyData;
    private String mLastingData;
    private String mTimeRecorded;


    public DataBundle() {
    }


    public DataBundle(String dailyData, String lastingData, String timeRecorded) {
        mDailyData = parseBundleBalance(dailyData);
        mLastingData = parseBundleBalance(lastingData);
        mTimeRecorded = timeRecorded;
    }


    public DataBundle(String dailyData, String lastingData, Date timeRecorded, Context context) {
        mDailyData = parseBundleBalance(dailyData);
        mLastingData = parseBundleBalance(lastingData);
        mTimeRecorded = Utils.sqlDateTime(timeRecorded, context);
    }


    public DataBundle(String dailyData, String lastingData, Context context) {
        mDailyData = parseBundleBalance(dailyData);
        mLastingData = parseBundleBalance(lastingData);
        mTimeRecorded = Utils.sqlDateTime(new Date(), context);
    }


    public int getID() {
        return mId;
    }


    void setID(int id) {
        mId = id;
    }


    public String getDailyData() {
        return mDailyData;
    }


    void setDailyData(String dailyData) {
        mDailyData = dailyData;
    }


    public String getLastingData() {
        return mLastingData;
    }


    void setLastingData(String lastingData) {
        mLastingData = lastingData;
    }


    public String getTimeRecorded() {
        return mTimeRecorded;
    }


    void setTimeRecorded(String timeRecorded) {
        mTimeRecorded = timeRecorded;
    }


    @Override
    public String toString() {
        return "[#" + getID() + "]: " + "'" + getDailyData() + "' | '"
                + getLastingData() + "'" + " @ " + getTimeRecorded();
    }


    /**
     * Parse the bundle balance numbers from the human description i.e "5 MBs" --> "5"
     *
     * @param strBalance Human description of the bundle data
     * @return Number in string form without the data units
     */
    private String parseBundleBalance(String strBalance) {
        strBalance = strBalance == null ? "0.0" : strBalance;
        strBalance = StringUtils.trim(strBalance);
        strBalance = strBalance.replaceAll("(?i)\\s?MBs", "");
        strBalance = strBalance.replaceAll("(?i)\\s?GBs", "");
        strBalance = strBalance.replaceAll("[^\\d.]", "");
        strBalance = Objects.equals(strBalance, "") ? "0.0" : strBalance;
        strBalance = String.format("%.2f", Float.parseFloat(strBalance));
        return strBalance;
    }


    /**
     * Calculate the amount of data used between two recordings
     *
     * @param dataBundle Another DataBundle object
     * @return Bundle difference between the two objects
     */
    public float subtract(DataBundle dataBundle) {
        float dailyA = Float.parseFloat(mDailyData);
        float lastingA = Float.parseFloat(mLastingData);
        float lastingB = Float.parseFloat(dataBundle.getLastingData());
        float dailyB = Float.parseFloat(dataBundle.getDailyData());

        return (dailyA + lastingA) - (dailyB + lastingB);
    }
}
