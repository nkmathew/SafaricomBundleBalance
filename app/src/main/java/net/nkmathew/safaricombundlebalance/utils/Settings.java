package net.nkmathew.safaricombundlebalance.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.net.MalformedURLException;
import java.net.URL;

/*
 * Created by nkmathew on 10/10/2017.
 */

public class Settings {

    SharedPreferences mSharedPreferences;

    public Settings(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Whether to show notifications
     *
     * @return
     */
    public boolean showNotification() {
        return mSharedPreferences.getBoolean("pref_show_notification", false);
    }

    /**
     * Whether to show bundle balance in toast messages
     *
     * @return
     */
    public boolean showToast() {
        return mSharedPreferences.getBoolean("pref_show_toast", false);
    }

    /**
     * Get update frequency in milliseconds
     *
     * @return
     */
    public int getUpdateFrequency() {
        String updateFrequency = mSharedPreferences.getString("pref_update_frequency", "30");
        int frequency = Integer.parseInt(updateFrequency);
        return frequency * 60 * 1000;
    }

    /**
     * Whether to play sound with notification
     *
     * @return
     */
    public boolean playSound() {
        return mSharedPreferences.getBoolean("pref_notification_play_sound", false);
    }

    /**
     * Whether to vibrate
     *
     * @return
     */
    public boolean vibrate() {
        return mSharedPreferences.getBoolean("pref_notification_vibrate", false);
    }

    /**
     * Notification priority
     *
     * @return
     */
    public int notificationPriority() {
        String priority = mSharedPreferences.getString("pref_notification_priority", "1");
        return Integer.parseInt(priority);
    }

    /**
     * Get path to set notification ringtone
     */
    public Uri getRingtone() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String ringtone = mSharedPreferences.getString("pref_notification_ringtone", uri.toString());
        uri = Uri.parse(ringtone);
        return uri;
    }
}
