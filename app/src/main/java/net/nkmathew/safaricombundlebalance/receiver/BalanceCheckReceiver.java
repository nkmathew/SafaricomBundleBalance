package net.nkmathew.safaricombundlebalance.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import net.nkmathew.safaricombundlebalance.task.BundleBalanceTask;
import net.nkmathew.safaricombundlebalance.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class BalanceCheckReceiver extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d("msg", "Receiver called...");
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
        String dataBundle = null;
        try {
            dailyData = (String) json.get("Daily Data");
            dataBundle = (String) json.get("Data Bundle");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String message = "Daily: " + dailyData + ", Data: " + dataBundle;
        notifyBundleBalance(message);
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    private void notifyBundleBalance(String message) {

        Bitmap bitmapLargeIcon = BitmapFactory
                .decodeResource(context.getResources(), R.mipmap.ic_launcher);

        int noSound = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmapLargeIcon)
                .setContentTitle("Bundle Balance")
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(noSound)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(666, mBuilder.build());
    }


}