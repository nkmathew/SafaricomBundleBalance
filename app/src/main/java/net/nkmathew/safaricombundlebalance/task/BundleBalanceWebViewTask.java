package net.nkmathew.safaricombundlebalance.task;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static net.nkmathew.safaricombundlebalance.utils.Constants.ENDPOINT_BUNDLES;

/*
 * Created by nkmathew on 04/10/2017.
 */

public class BundleBalanceWebViewTask extends AsyncTask<Void, Void, String> {


    public BundleBalanceWebViewTask() {
    }


    @Override
    protected String doInBackground(Void... params) {
        Document table;
        try {
            table = Jsoup.connect(ENDPOINT_BUNDLES).get();
            if (table == null) {
                return null;
            } else {
                String STYLESHEET = "<style type=\"text/css\" media=\"screen\">\n" +
                        "body {\n" +
                        "  background: #263238;\n" +
                        "  color: white;\n" +
                        "}\n" +
                        "a {\n" +
                        "  color: #263238;\n" +
                        "  display: hidden;\n" +
                        "}\n" +
                        "</style>\n";
                table.append(STYLESHEET);
            }
            return table.html().replaceAll("Data", "").replaceAll("Date", "");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("msg", e.toString());
        }
        return null;
    }
}
