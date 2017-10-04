package net.nkmathew.safaricombundlebalance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progress;
    private final String ENDPOINT = "http://www.safaricom.com/bundles/GetSubDetails";

    private final String ERROR_MESSAGE =
         "<style type=\"text/css\" media=\"screen\">\n" +
                 "body {\n" +
                 "  background: #0F2F42;\n" +
                 "  color: yellow;\n" +
                 "}\n" +
                 "</style>\n" +
                 "<b>Request timed out. Could be because you're not using Safaricom currently.</b>" +
                 "<br/><br/>\n";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.activity_main);
        getBundleBalance(view);
    }

    public void showProgressDialog(final String msg) {

        runOnUiThread(new Runnable() {
            public void run() {
                if (progress == null || !progress.isShowing()) {
                    progress = ProgressDialog.show(MainActivity.this, "", msg);
                }
            }
        });
    }

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

    public void getBundleBalance(View view) {
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

        Object html = null;
        try {
            html = (new BundleBalanceWebViewTask(this)).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (html != null) {
            webView.loadData(html.toString(), "text/html; charset=utf-8", "UTF-8");
        } else {
            String toastMessage = "Empty reply received when querying balance";
            String info = null;
            if (Connectivity.isConnectedWifi(this)) {
                String wifiName = Connectivity.getWifiName(this);
                info = "You are currently connected to a Wifi connection, SSID: " + wifiName;
            } else {
                String carrier = Connectivity.getCarrier(this);
                info = "Current carrier: " + carrier;
            }
            info = "<b>" + info + "</b>";
            info = ERROR_MESSAGE + info;
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            webView.loadData(info, "text/html; charset=utf-8", "UTF-8");
        }

    }


}