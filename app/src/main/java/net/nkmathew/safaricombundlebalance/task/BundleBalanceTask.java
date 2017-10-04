package net.nkmathew.safaricombundlebalance.task;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
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

    private final String ENDPOINT = "http://www.safaricom.com/bundles/GetSubDetails";

    public BundleBalanceTask(Context context) {
        this.context = context;
    }


    @Override
    protected JSONObject doInBackground(Object[] params) {
        Document doc = null;
        JSONObject json = new JSONObject();
        try {
            doc = Jsoup.connect(ENDPOINT).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc == null) {
            return json;
        }
        Elements tableRows = doc.select("tr");
        for (Element row : tableRows) {
            Elements children = row.children();
            if (children.size() < 2) {
                continue;
            }
            String name = children.get(0).text();
            String value = children.get(1).text();
            try {
                json.put(name, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json;
    }
}
