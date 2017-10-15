package net.nkmathew.safaricombundlebalance.utils;

import android.content.Context;
import android.os.Build;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by nkmathew on 13/10/2017.
 */

public class Utils {

    /**
     * Format java.util.Date to an SQL consumable format e.g 2017-09-18 17:32:33
     *
     * @param date
     * @return
     */
    public static String sqlDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(date);
    }


    /**
     * Parse a datetime string to java.util.Date
     * @return
     */
    public static Date parseDateTime(String datetime) throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        return format.parse(datetime);
    }

    /**
     * Current datetime in SQL format
     */
    public static String sqlDateTime() {
        return sqlDateTime(new Date());
    }


    /**
     * Get current locale
     *
     * @param context
     * @return
     */
    public static Locale getCurrentLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }
}
