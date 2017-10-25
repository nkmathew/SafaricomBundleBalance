package net.nkmathew.safaricombundlebalance.task;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static net.nkmathew.safaricombundlebalance.utils.Constants.ENDPOINT_BUNDLES;

/**
 * @author nkmathew
 * @date   04/10/2017
 */

public class BundleBalanceTask extends AsyncTask <Void, JSONObject, Object> {

    /**
     * Fetches bundle balance from Safaricom's subscription information page
     *
     * @return Bundle information
     */
    private JSONObject getBundlesInformation(String bundlesEndpoint) {
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
    protected JSONObject doInBackground(Void[] params) {
        return getBundlesInformation(ENDPOINT_BUNDLES);
    }

}
