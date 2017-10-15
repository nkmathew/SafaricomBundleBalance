package net.nkmathew.safaricombundlebalance.task;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.SyncStateContract;

import net.nkmathew.safaricombundlebalance.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static net.nkmathew.safaricombundlebalance.utils.Constants.ENDPOINT_BUNDLES;

/*
 * Created by nkmathew on 04/10/2017.
 */

public class BundleBalanceTask extends AsyncTask {

    Context context;

    public BundleBalanceTask(Context context) {
        this.context = context;
    }

    /**
     * Fetches bundle balance from Safaricom's subscription information page
     * @return
     */
    public JSONObject getBundlesInformation(String bundlesEndpoint) {
        Document doc = null;
        JSONObject json = new JSONObject();
        try {
            doc = Jsoup.connect(bundlesEndpoint).get();
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

    @Override
    protected JSONObject doInBackground(Object[] params) {
        return getBundlesInformation(ENDPOINT_BUNDLES);
    }

}