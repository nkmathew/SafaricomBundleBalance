package net.nkmathew.safaricombundlebalance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl("http://www.safaricom.com/bundles/GetSubDetails");

    }

    public void getBundleBalance(View view) {
        new BundleBalanceTask(this).execute();
        Toast.makeText(this, "All stored clips have been deleted", Toast.LENGTH_LONG).show();
    }

}