package yahoofinance.custom;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.json.JSONObject;

public class CandleStickChart {

	public static void main(String[] args) {

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			String url = "https://query2.finance.yahoo.com/v8/finance/chart/GOOG?period1=1604577600&period2=1604664000&interval=1m&events=div%7Csplit";

			System.out.println(url);
			HttpGet request = new HttpGet(url);

			HttpResponse result = httpClient.execute(request);
			String json = EntityUtils.toString(result.getEntity());
			JSONObject j1 = new JSONObject(json);
			System.out.println(j1);

		} catch (IOException e) {
			e.printStackTrace();

		}

	}

}
