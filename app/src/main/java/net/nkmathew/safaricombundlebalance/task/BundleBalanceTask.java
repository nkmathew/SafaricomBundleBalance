package net.nkmathew.safaricombundlebalance.task;

import android.os.AsyncTask;

import net.nkmathew.safaricombundlebalance.utils.Utils;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static net.nkmathew.safaricombundlebalance.utils.Constants.ENDPOINT_BUNDLES;

/**
 * @author nkmathew
 * @date 04/10/2017
 */

public class BundleBalanceTask extends AsyncTask<Void, JSONObject, Object> {


    /**
     * Fetches bundle balance from Safaricom's subscription information page
     *
     * @return Bundle information
     */
    private JSONObject getBundlesInformation(String bundlesEndpoint) {
        Document document = null;
        try {
            document = Jsoup.connect(bundlesEndpoint).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Utils.parseSubscriberInfo(document);
    }


    @Override
    protected JSONObject doInBackground(Void[] params) {
        return getBundlesInformation(ENDPOINT_BUNDLES);
    }

}
