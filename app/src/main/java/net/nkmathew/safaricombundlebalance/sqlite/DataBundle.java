package net.nkmathew.safaricombundlebalance.sqlite;

import android.content.Context;

import net.nkmathew.safaricombundlebalance.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class DataBundle {

    private int mId;
    private String mDailyData;
    private String mLastingData;
    private String mTimeRecorded;


    public DataBundle() {
    }


    public DataBundle(String dailyData, String lastingData, String timeRecorded) {
        mDailyData = parseBundlebalance(dailyData);
        mLastingData = parseBundlebalance(lastingData);
        mTimeRecorded = timeRecorded;
    }


    public DataBundle(String dailyData, String lastingData, Date timeRecorded, Context context) {
        mDailyData = parseBundlebalance(dailyData);
        mLastingData = parseBundlebalance(lastingData);
        mTimeRecorded = Utils.sqlDateTime(timeRecorded, context);
    }


    public DataBundle(String dailyData, String lastingData, Context context) {
        mDailyData = parseBundlebalance(dailyData);
        mLastingData = parseBundlebalance(lastingData);
        mTimeRecorded = Utils.sqlDateTime(new Date(), context);
    }


    public int getID() {
        return mId;
    }


    public void setID(int id) {
        mId = id;
    }


    public String getDailyData() {
        return mDailyData;
    }


    public void setDailyData(String dailyData) {
        mDailyData = dailyData;
    }


    public String getLastingData() {
        return mLastingData;
    }


    public void setLastingData(String dailyData) {
        mDailyData = dailyData;
    }


    public String getTimeRecorded() {
        return mTimeRecorded;
    }


    public void setTimeRecorded(String timeRecorded) {
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
    private String parseBundlebalance(String strBalance) {
        strBalance = strBalance == null ? "0.0" : strBalance;
        strBalance = StringUtils.trim(strBalance);
        strBalance = strBalance.replaceAll("(?i)\\s?MBs", "");
        strBalance = strBalance.replaceAll("(?i)\\s?GBs", "");
        strBalance = strBalance.replaceAll("[^\\d.]", "");
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
