package net.nkmathew.safaricombundlebalance.utils;

import android.content.Context;
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
}
