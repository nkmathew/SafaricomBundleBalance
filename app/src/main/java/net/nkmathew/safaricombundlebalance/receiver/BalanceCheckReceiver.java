package net.nkmathew.safaricombundlebalance.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import net.nkmathew.safaricombundlebalance.R;
import net.nkmathew.safaricombundlebalance.sqlite.DataBundle;
import net.nkmathew.safaricombundlebalance.sqlite.DatabaseHandler;
import net.nkmathew.safaricombundlebalance.task.BundleBalanceTask;
import net.nkmathew.safaricombundlebalance.utils.Constants;
import net.nkmathew.safaricombundlebalance.utils.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BalanceCheckReceiver extends BroadcastReceiver {

    Context mContext;
    Settings mSettings;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        this.mSettings = new Settings(context);
        deleteOldRecords(context);
        JSONObject json = null;
        try {
            json = (JSONObject) new BundleBalanceTask(context).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (json == null || json.length() == 0) {
            return;
        }

        String dailyData = null;
        String lastingBundle = null;

        try {
            dailyData = (String) json.get("Daily Data");
        } catch (JSONException e) {
            Log.e("msg", e.getMessage());
            e.printStackTrace();
        }

        try {
            lastingBundle = (String) json.get("Data Bundle");
        } catch (JSONException e) {
            Log.e("msg", e.getMessage());
            e.printStackTrace();
        }

        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        DataBundle dataBundle = new DataBundle(dailyData, lastingBundle, new Date());
        databaseHandler.saveBundleData(dataBundle);

        float usageLastHour = calculateUsageByPeriod(60);
        float usage6Hours = calculateUsageByPeriod(60 * 6);
        float usage12Hours = calculateUsageByPeriod(60 * 12);

        String sUsageLastHour = String.format("%.2f", usageLastHour);
        String sUsageLastSixHours = String.format("%.2f", usage6Hours);
        String sUsageLast12Hours = String.format("%.2f", usage12Hours);

        String message = "Daily: " + dailyData + ", Data: " + lastingBundle +
                "\nLast Hour: " + sUsageLastHour + " MBs" +
                "\nLast 6 Hours: " + sUsageLastSixHours + " MBs" +
                "\nLast 12 Hours: " + sUsageLast12Hours + " MBs";

        if (mSettings.showNotification()) {
            notifyBundleBalance(message);
        }

        if (mSettings.showToast()) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Calculates data usage within a certain time interval from the current time
     *
     * @return Usage
     */
    private float calculateUsageByPeriod(int minutes) {
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        List<DataBundle> bundles = databaseHandler.getBundlesXMinutesAgo(minutes);
        if (bundles.size() < 2) {
            return 0.0f;
        }
        DataBundle curr = bundles.get(0);
        float total = 0.0f;
        for (int i = 1; i < bundles.size(); i++) {
            DataBundle next = bundles.get(i);
            float diff = curr.subtract(next);
            Log.d("msg", "Difference: " + diff);
            if (diff > 0.0f) {
                total += diff;
            }
            curr = next;
        }

        return total;
    }


    /**
     * Delete old records
     */
    private void deleteOldRecords(final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new DatabaseHandler(context).deleteOldRecords();
            }
        }, 1000);
    }


    /**
     * Display the bandle balance notification
     *
     * @param message
     */
    private void notifyBundleBalance(String message) {

        Uri ringtone = mSettings.getRingtone();
        int defaults = Notification.DEFAULT_LIGHTS;
        int notificationPriority = mSettings.notificationPriority();
        defaults = mSettings.vibrate() ? defaults | Notification.DEFAULT_VIBRATE : defaults;

        Bitmap bitmapLargeIcon = BitmapFactory
                .decodeResource(mContext.getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmapLargeIcon)
                .setContentTitle("Bundle Balance")
                .setPriority(notificationPriority)
                .setDefaults(NotificationCompat.FLAG_AUTO_CANCEL)
                .setDefaults(defaults)
                .setSound(ringtone)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getService(mContext, 0, new Intent(), 0))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);

        mBuilder = mSettings.playSound() ? mBuilder.setSound(ringtone) : mBuilder;

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(Constants.ID_BUNDLE_CHECK_ALARM, mBuilder.build());
    }

}
