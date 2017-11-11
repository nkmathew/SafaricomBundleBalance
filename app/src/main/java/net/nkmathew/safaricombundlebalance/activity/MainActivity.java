package net.nkmathew.safaricombundlebalance.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import net.nkmathew.safaricombundlebalance.R;
import net.nkmathew.safaricombundlebalance.receiver.BalanceCheckReceiver;
import net.nkmathew.safaricombundlebalance.sqlite.DataBundle;
import net.nkmathew.safaricombundlebalance.sqlite.DatabaseHandler;
import net.nkmathew.safaricombundlebalance.task.BundleBalanceWebViewTask;
import net.nkmathew.safaricombundlebalance.utils.Connectivity;
import net.nkmathew.safaricombundlebalance.utils.Settings;
import net.nkmathew.safaricombundlebalance.utils.Utils;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static net.nkmathew.safaricombundlebalance.utils.Constants.ID_BUNDLE_CHECK_ALARM;

public class MainActivity extends AppCompatActivity implements OnRefreshListener {

    private final String STYLESHEET = "<style type=\"text/css\" media=\"screen\">\n" +
            "table {\n" +
            "  margin: auto;\n" +
            "  border-radius: 5px;\n" +
            "}\n" +
            "td { text-align: center; }" +
            "table, th, td {\n" +
            "  border: 1px solid;\n" +
            "  border-collapse: collapse;\n" +
            "  padding: 4px;\n" +
            "  color: white;\n" +
            "  border-color: #263238;\n" +
            "  border-spacing: 3px;\n" +
            "}" +
            "body {\n" +
            "  background: #263238;\n" +
            "  color: yellow;\n" +
            "}\n" +
            "</style>\n";
    private ProgressDialog progress;
    private View mViewMainActivity;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    /**
     * Start the periodic balance check requests
     */
    public static void startBundleCheckAlarm(Context context) {
        long startTime = System.currentTimeMillis();
        int interval = new Settings(context).getUpdateFrequency();
        Intent intent = new Intent(context, BalanceCheckReceiver.class);
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(context, ID_BUNDLE_CHECK_ALARM, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent);
    }


    /**
     * Stop the periodic balance check requests
     */
    public static void stopBundleCheckAlarm(Context context) {
        Intent alarmIntent = new Intent(context.getApplicationContext(), BalanceCheckReceiver.class);
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(context, ID_BUNDLE_CHECK_ALARM, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewMainActivity = findViewById(R.id.activity_main);

        fetchBundleBalance(mViewMainActivity);
        startBundleCheckAlarm(this);

        int backgroundColor = Color.parseColor("#212121");
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(backgroundColor);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

    }


    /**
     * Display all the records in the sqlite database in a table
     *
     * @param view WebView
     */
    public void showRecentRecords(View view) {
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        Locale currentLocale = Utils.getCurrentLocale(this);
        List<DataBundle> allBundles = databaseHandler.getRecentRecords(300);
        String html = "<table>";
        String header = "<tr>\n" +
                "<td>#</td>\n" +
                "<td>Time</td>\n" +
                "<td>Daily</td>\n" +
                "<td>Lasting</td>\n" +
                "</tr>";
        html += header;
        int counter = 0;
        for (DataBundle bundle : allBundles) {
            counter++;
            String daily = bundle.getDailyData();
            String lasting = bundle.getLastingData();
            String timeRecorded = bundle.getTimeRecorded();
            DateFormat dateFormat = new SimpleDateFormat("MMM d, hh:mm a", currentLocale);
            try {
                Date dTimeRecorded = Utils.parseDateTime(timeRecorded, this);
                timeRecorded = dateFormat.format(dTimeRecorded);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String TABLE_ROW = STYLESHEET +
                    "<tr>" +
                    "<td>%s</td>" +
                    "<td>%s</td>" +
                    "<td>%s</td>" +
                    "<td>%s</td>" +
                    "</tr>";
            html += String.format(currentLocale, TABLE_ROW, counter, timeRecorded, daily,
                    lasting);
        }
        html += "</table>";
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.loadData(html, "text/html; charset=utf-8", "UTF-8");
    }


    @Override
    public void onRefresh() {
        fetchBundleBalance(mViewMainActivity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }


    /**
     * Show spinner
     *
     * @param msg Progress message
     */
    public void showProgressDialog(final String msg) {

        runOnUiThread(new Runnable() {
            public void run() {
                if (progress == null || !progress.isShowing()) {
                    progress = ProgressDialog.show(MainActivity.this, "", msg);
                }
            }
        });
    }


    /**
     * Hide spinner
     */
    public void hideProgressDialog() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (progress.isShowing())
                        progress.dismiss();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Display data in WebView
     */
    public void renderWebView(String html) {
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > 0) {
                    showProgressDialog("Loading your bundle balance...");
                }
                if (newProgress >= 100) {
                    hideProgressDialog();
                }
            }
        });

        webView.loadData(html, "text/html; charset=utf-8", "UTF-8");
    }


    /**
     * Displays your subscription information including your Bonga points, daily data and normal
     * data balances
     *
     * @param view WebView
     */
    private void fetchBundleBalance(View view) {


        String html = null;
        try {
            html = new BundleBalanceWebViewTask().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (html != null) {
            Document document = Jsoup.parse(html);
            JSONObject subscriberInfo = Utils.parseSubscriberInfo(document);
            Utils.saveSubscriberInfo(this, subscriberInfo);
            renderWebView(html);
        } else {
            String toastMessage = "Empty reply received when querying balance";
            String info;
            if (Connectivity.isConnectedWifi(this)) {
                String wifiName = Connectivity.getWifiName(this);
                info = "You are currently connected to a Wifi network, SSID: " + wifiName;
            } else {
                String carrier = Connectivity.getCarrier(this);
                if (Connectivity.isConnected(this)) {
                    info = "Current carrier: " + carrier;
                } else {
                    info = "Mobile data is currently disabled!";
                }
            }
            info = "<b>" + info + "</b>";
            String ERROR_MESSAGE = STYLESHEET +
                    "<b>Request timed out.</b>" +
                    "<br/><br/>\n";
            info = ERROR_MESSAGE + info;
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            renderWebView(info);
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchBundleBalance(mViewMainActivity);
            }
        }, 1000);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.action_name) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Show usage
     *
     * @param view View
     */
    public void showBundleUsage(View view) {
        String message = Utils.getBundleUsage(this);
        message = message.replaceAll("\n", "<br/>");
        message = "<pre>" + message + "</pre>";
        renderWebView(STYLESHEET + message);
    }


    /**
     * Open mobile data settings screen
     *
     * @param view View
     */
    public void openDataUsageScreen(View view) {
        Utils.openDataUsageScreen(this);
    }
}
