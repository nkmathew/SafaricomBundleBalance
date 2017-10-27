package net.nkmathew.safaricombundlebalance.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import net.nkmathew.safaricombundlebalance.sqlite.DataBundle;
import net.nkmathew.safaricombundlebalance.sqlite.DatabaseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author nkmathew
 * @date 13/10/2017.
 */

public class Utils {

    /**
     * Format java.util.Date to an SQL consumable format e.g 2017-09-18 17:32:33
     *
     * @param date Date to format
     * @return SQLite compatible datetime string
     */
    public static String sqlDateTime(Date date, Context context) {
        Locale currentLocale = getCurrentLocale(context);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", currentLocale);
        return dateFormat.format(date);
    }


    /**
     * Parse a datetime string to java.util.Date
     *
     * @return The date equivalent of the string
     */
    public static Date parseDateTime(String datetime, Context context) throws ParseException {
        Locale currentLocale = getCurrentLocale(context);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", currentLocale);
        return format.parse(datetime);
    }


    /**
     * Current datetime in SQL format
     */
    public static String sqlDateTime(Context context) {
        return sqlDateTime(new Date(), context);
    }


    /**
     * Get current locale
     *
     * @param context App context
     * @return The current locale setting
     */
    public static Locale getCurrentLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }


    /**
     * Parses the html found in the subscriber information page
     *
     * @param document Jsoup HTML from the table
     * @return JSON object representing the table
     */
    public static JSONObject parseSubscriberInfo(Document document) {
        JSONObject json = new JSONObject();
        if (document == null) {
            return json;
        }
        Elements tableRows = document.select("tr");
        for (Element row : tableRows) {
            Elements children = row.children();
            if (children.size() < 2) {
                continue;
            }
            String name = children.get(0).text();
            String value = children.get(1).text();
            try {
                json.put(name, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json;
    }


    /**
     * Records the subscriber information in the SQLite database
     *
     * @param jsonObject Object containing subscriber information containing the data bundles
     * @return rowID Row id of the inserted record, -1 if it fails
     */
    public static DataBundle saveSubscriberInfo(Context context, JSONObject jsonObject) {
        Object dailyData = jsonGet(jsonObject, "Daily Data");
        dailyData = dailyData != null ? dailyData : jsonGet(jsonObject, "Daily");

        Object lastingBundle = jsonGet(jsonObject, "Data Bundle");
        lastingBundle = lastingBundle != null ? lastingBundle : jsonGet(jsonObject, "Bundle");

        DataBundle dataBundle = new DataBundle(
                Objects.toString(dailyData),
                Objects.toString(lastingBundle), new Date(), context
        );
        DatabaseHandler dbHandler = new DatabaseHandler(context);

        dbHandler.saveBundleData(dataBundle);

        return dataBundle;
    }


    /**
     * Get a property from a json object and otherwise return null
     *
     * @return The value corresponding to the property
     */
    private static Object jsonGet(JSONObject jsonObject, String propName) {
        try {
            return jsonObject.get(propName);
        } catch (JSONException e) {
            Log.e("msg", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Calculates data usage within a certain time interval from the current time
     *
     * @return Usage
     */
    private static float usageByPeriod(Context context, int minutes) {
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        List<DataBundle> bundles = databaseHandler.getBundlesXMinutesAgo(minutes);
        if (bundles.size() < 2) {
            return 0.0f;
        }
        DataBundle curr = bundles.get(0);
        float total = 0.0f;
        for (int i = 1; i < bundles.size(); i++) {
            DataBundle next = bundles.get(i);
            float diff = curr.subtract(next);
            if (diff > 0.0f) {
                total += diff;
            }
            curr = next;
        }

        return total;
    }


    public static String getBundleUsage(Context context) {

        DatabaseHandler dbHandler = new DatabaseHandler(context);
        List<DataBundle> bundles = dbHandler.getRecentRecords(1);
        if (bundles.size() == 0) {
            return "No records yet...";
        }

        DataBundle recentBundle = bundles.get(0);

        float usageLastHour = usageByPeriod(context, 60);
        float usage6Hours = usageByPeriod(context, 60 * 6);
        float usage12Hours = usageByPeriod(context, 60 * 12);
        float usage24Hours = usageByPeriod(context, 60 * 24);

        Locale locale = Utils.getCurrentLocale(context);
        String sUsageLastHour = String.format(locale, "%.2f", usageLastHour);
        String sUsageLastSixHours = String.format(locale, "%.2f", usage6Hours);
        String sUsageLast12Hours = String.format(locale, "%.2f", usage12Hours);
        String sUsageLast24Hours = String.format(locale, "%.2f", usage24Hours);

        return "Daily: " + recentBundle.getDailyData() +
                ", Data: " + recentBundle.getLastingData() +
                "\nLast Hour: " + sUsageLastHour + " MBs" +
                "\nLast 6 Hours: " + sUsageLastSixHours + " MBs" +
                "\nLast 12 Hours: " + sUsageLast12Hours + " MBs" +
                "\nLast 24 Hours: " + sUsageLast24Hours + " MBs";
    }


    /**
     * Open the Data Usage screen
     */
    public static void openDataUsageScreen(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        ComponentName component = new ComponentName(
                "com.android.settings",
                "com.android.settings.Settings$DataUsageSummaryActivity"
        );
        intent.setComponent(component);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
