package net.nkmathew.safaricombundlebalance;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/*
 * Created by nkmathew on 04/10/2017.
 */

public class BundleBalanceWebViewTask extends AsyncTask {

    Context context;
    private final String ENDPOINT = "http://www.safaricom.com/bundles/GetSubDetails";

    private final String STYLESHEET =
            "<style type=\"text/css\" media=\"screen\">\n" +
            "body {\n" +
            "  background: #0F2F42;\n" +
            "  color: white;\n" +
            "}\n" +
            "a {\n" +
            "  color: cyan;\n" +
            "  display: hidden;\n" +
            "}\n" +
            "</style>\n";

    public BundleBalanceWebViewTask(Context context) {
        this.context = context;
    }


    @Override
    protected String doInBackground(Object[] params) {
        Document table = null;
        try {
            table = Jsoup.connect(ENDPOINT).get();
            if (table == null) {
                return null;
            } else {
                table.append(STYLESHEET);
            }
            return table.html().replaceAll("Data", "");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("msg", e.toString());
        }
        return null;
    }
}
