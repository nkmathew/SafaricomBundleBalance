package net.nkmathew.safaricombundlebalance.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import net.nkmathew.safaricombundlebalance.R;
import net.nkmathew.safaricombundlebalance.sqlite.DatabaseHandler;
import net.nkmathew.safaricombundlebalance.task.BundleBalanceTask;
import net.nkmathew.safaricombundlebalance.utils.Constants;
import net.nkmathew.safaricombundlebalance.utils.Settings;
import net.nkmathew.safaricombundlebalance.utils.Utils;

import org.json.JSONObject;

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
            json = (JSONObject) new BundleBalanceTask().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (json == null || json.length() == 0) {
            return;
        }

        Utils.saveSubscriberInfo(mContext, json);
        String message = Utils.getBundleUsage(mContext);

        if (mSettings.showNotification()) {
            notifyBundleBalance(message);
        }

        if (mSettings.showToast()) {
            if (mSettings.vibrate()) {
                Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(50);
            }
            if (mSettings.playSound()) {
                Uri ringtoneURI = mSettings.getRingtone();
                Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneURI);
                ringtone.play();
            }
            for (int i = 0; i < 3; i++) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
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
     * Display the bundle balance notification
     *
     * @param message Notification message
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
