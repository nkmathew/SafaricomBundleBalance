package net.nkmathew.safaricombundlebalance.sqlite;

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

    public DataBundle(String dailyData, String lastingData, Date timeRecorded) {
        mDailyData = parseBundlebalance(dailyData);
        mLastingData = parseBundlebalance(lastingData);
        mTimeRecorded = Utils.sqlDateTime(timeRecorded);
    }

    public DataBundle(String dailyData, String lastingData) {
        mDailyData = parseBundlebalance(dailyData);
        mLastingData = parseBundlebalance(lastingData);
        mTimeRecorded = Utils.sqlDateTime(new Date());
    }

    // getting ID
    public int getID() {
        return mId;
    }

    // setting id
    public void setID(int id) {
        mId = id;
    }

    // getting name
    public String getDailyData() {
        return mDailyData;
    }

    // setting dailyData
    public void setDailyData(String dailyData) {
        mDailyData = dailyData;
    }

    public String getLastingData() {
        return mLastingData;
    }

    public void setLastingData(String lastingData) {
        mLastingData = lastingData;
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
     * @param sBalance
     * @return
     */
    private String parseBundlebalance(String sBalance) {
        sBalance = sBalance == null ? "0.0" : sBalance;
        sBalance = StringUtils.trim(sBalance);
        sBalance = sBalance.replaceAll("(?i)\\s?MBs", "");
        sBalance = sBalance.replaceAll("(?i)\\s?GBs", "");
        sBalance = sBalance.replaceAll("[^\\d.]", "");
        sBalance = String.format("%.2f", Float.parseFloat(sBalance));
        return sBalance;
    }

    /**
     * Calculate the amount of data used between two recordings
     *
     * @param dataBundle
     * @return
     */
    public float subtract(DataBundle dataBundle) {
        float dailyA = Float.parseFloat(mDailyData);
        float lastingA = Float.parseFloat(mLastingData);
        float lastingB = Float.parseFloat(dataBundle.getLastingData());
        float dailyB = Float.parseFloat(dataBundle.getDailyData());

        return (dailyA + lastingA) - (dailyB + lastingB);
    }
}
