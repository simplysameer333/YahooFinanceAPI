package yahoofinance.custom;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

//https://github.com/DaegiKim/java-stock-ticker
public class Main {
    private static final String API_ENDPOINT="https://query1.finance.yahoo.com/v7/finance/quote?lang=ko-KR&region=KR&corsDomain=finance.yahoo.com";

    public static void main(String[] args) {
        long interval = Long.parseLong(args[0]);
        String symbols = Arrays.stream(args).skip(1).collect(Collectors.joining(","));

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
        	String api_url = API_ENDPOINT + "&symbols=" + URLEncoder.encode(symbols,"UTF-8");
        	System.out.println(api_url); 
            HttpGet request = new HttpGet(api_url);
            
            while (true) {
                HttpResponse result = httpClient.execute(request);
                String json = EntityUtils.toString(result.getEntity());
                System.out.println(json);
                print(new JSONObject(json));
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void print(JSONObject jsonObject) {
        JSONArray result = jsonObject.getJSONObject("quoteResponse").getJSONArray("result");
        result = sort(result);

        StringBuilder sb = new StringBuilder();
        sb.append("\033[H\033[2J");
        sb.append(String.format(ConsoleColors.CYAN_UNDERLINED+"%-15s%10s%10s%11s%10s%15s%12s %-20s\033[0m\n", "Name", "Symbol", "Price", "Diff", "Percent", "Delay", "MarketState", "Long Name"));

        long currentTimeMillis = System.currentTimeMillis();

        for (int i = 0; i < result.length(); i++) {
            JSONObject data = result.getJSONObject(i);

            String shortName = data.optString("shortName");
            shortName = shortName.length()>14?shortName.substring(0, 14):shortName;

            String longName = data.optString("longName", shortName);
            String symbol = data.optString("symbol");
            String marketState = data.optString("marketState");
            long regularMarketTime = data.optLong("regularMarketTime");

            double regularMarketPrice = data.optDouble("regularMarketPrice");
            double regularMarketDayHigh = data.optDouble("regularMarketDayHigh");
            double regularMarketDayLow = data.optDouble("regularMarketDayLow");
            double regularMarketChange = data.optDouble("regularMarketChange");
            double regularMarketChangePercent = data.optDouble("regularMarketChangePercent");

            String color = regularMarketChange==0?"":regularMarketChange>0?ConsoleColors.GREEN_BOLD_BRIGHT:ConsoleColors.RED_BOLD_BRIGHT;

            sb.append(String.format("%-15s", shortName));
            sb.append(String.format("%10s", symbol));

            if(regularMarketDayHigh == regularMarketPrice || regularMarketDayLow == regularMarketPrice) {
                sb.append(String.format(ConsoleColors.WHITE_BOLD+color+"%10.2f"+ConsoleColors.RESET, regularMarketPrice));
            } else {
                sb.append(String.format(ConsoleColors.WHITE_BOLD+"%10.2f"+ConsoleColors.RESET, regularMarketPrice));
            }

            sb.append(String.format(color+"%11s"+ConsoleColors.RESET, String.format("%.2f", regularMarketChange)+" "+(regularMarketChange>0?"▲":regularMarketChange<0?"▼":"-")));
            sb.append(String.format(color+"%10s"+ConsoleColors.RESET, String.format("(%.2f%%)", regularMarketChangePercent)));
            sb.append(String.format("%15s", prettyTime(currentTimeMillis-(regularMarketTime*1000))));
            sb.append(String.format("%12s", marketState));
            sb.append(String.format(" %-20s\n", longName));
        }
        System.out.print(sb);
    }

    private static String prettyTime(long millis) {
        return String.format("%dm, %ds",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private static JSONArray sort(JSONArray result) {
        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            jsonValues.add(result.getJSONObject(i));
        }

        jsonValues.sort((a, b) -> {
            double valA = a.optDouble("regularMarketChangePercent");
            double valB = b.optDouble("regularMarketChangePercent");
            return Double.compare(valB, valA);
        });

        for (JSONObject jsonValue : jsonValues) {
            sortedJsonArray.put(jsonValue);
        }

        return sortedJsonArray;
    }
}

