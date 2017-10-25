package net.nkmathew.safaricombundlebalance.utils;

import android.content.Context;
import android.os.Build;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
}
