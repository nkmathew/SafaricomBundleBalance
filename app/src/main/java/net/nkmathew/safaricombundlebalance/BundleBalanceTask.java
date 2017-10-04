package net.nkmathew.safaricombundlebalance;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/*
 * Created by nkmathew on 04/10/2017.
 */

public class BundleBalanceTask extends AsyncTask {

    Context context;

    public BundleBalanceTask(Context context) {
        this.context = context;
    }


    @Override
    protected String doInBackground(Object[] params) {
        Log.d("msg", "Task called...");
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.safaricom.com/bundles/GetSubDetails").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc == null) return null;
        Elements tableRows = doc.select("tr");
        for (Element row : tableRows) {
            Elements children = row.children();
            if (children.size() < 2) {
                continue;
            }
            String name = children.get(0).text();
            String value = children.get(1).text();
            Log.d("msg", name + " => " + value);
        }
        return null;
    }
}
