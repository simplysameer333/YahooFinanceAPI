package yahoofinance;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.junit.Test;

import yahoofinance.custom.ConsoleColors;
import yahoofinance.histquotes.Interval;
import yahoofinance.histquotes2.HistQuotes2Request;
import yahoofinance.histquotes2.QueryInterval;
import yahoofinance.query2v8.HistQuotesQuery2V8Request;

public class CustomOutputTest {

	@Test
	public void displayTicker() throws IOException {

		while (true) {
			Stock stock = YahooFinance.get("INTC");
			print(stock);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void displayStockState() throws IOException {
		Stock stock = YahooFinance.get("INTC");
		System.out.println(stock);
		stock.print();
	}

	// @Test
	public void displayHistoryData() throws IOException {
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.DAY_OF_MONTH, -1); //

		// Stock google = YahooFinance.get("GOOG", from, to, Interval.WEEKLY);

		HistQuotes2Request hist = new HistQuotes2Request("GOOG", from, to, Interval.MINUTE);

		// System.out.println(hist.getResult());

		HistQuotesQuery2V8Request t1 = new HistQuotesQuery2V8Request("GOOG", from, to, QueryInterval.MINUTE);
		String json = t1.getJson();
		// System.out.println("===" + json);

		JSONObject j1 = new JSONObject(json);
		// System.out.println(j1.toString());

	}

	private static void print(Stock stock) {

		StringBuilder sb = new StringBuilder();
		sb.append("\033[H\033[2J");
		sb.append(String.format(ConsoleColors.CYAN_UNDERLINED + "%-15s%10s%10s%11s%10s%15s%12s %-20s\033[0m\n", "Name",
				"Symbol", "Price", "Diff", "Percent", "Delay", "MarketState", "Long Name"));

		long currentTimeMillis = System.currentTimeMillis();

		String shortName = stock.getName();
		// shortName = shortName.length()>14?shortName.substring(0, 14):shortName;

		String longName = stock.getLongName();
		String symbol = stock.getSymbol();
		String marketState = stock.getMarketState();
		long regularMarketTime = Long.parseLong(stock.getRegularMarketTime());

		double regularMarketPrice = Double.parseDouble(stock.getRegularMarketPrice());
		double regularMarketDayHigh = Double.parseDouble(stock.getRegularMarketDayHigh());
		double regularMarketDayLow = Double.parseDouble(stock.getRegularMarketDayLow());
		double regularMarketChange = Double.parseDouble(stock.getRegularMarketChange());
		double regularMarketChangePercent = Double.parseDouble(stock.getRegularMarketChangePercent());

		String color = regularMarketChange == 0 ? ""
				: regularMarketChange > 0 ? ConsoleColors.GREEN_BOLD_BRIGHT : ConsoleColors.RED_BOLD_BRIGHT;

		sb.append(String.format("%-15s", shortName));
		sb.append(String.format("%10s", symbol));

		if (regularMarketDayHigh == regularMarketPrice || regularMarketDayLow == regularMarketPrice) {
			sb.append(String.format(ConsoleColors.WHITE_BOLD + color + "%10.2f" + ConsoleColors.RESET,
					regularMarketPrice));
		} else {
			sb.append(String.format(ConsoleColors.WHITE_BOLD + "%10.2f" + ConsoleColors.RESET, regularMarketPrice));
		}

		sb.append(String.format(color + "%11s" + ConsoleColors.RESET, String.format("%.2f", regularMarketChange) + " "
				+ (regularMarketChange > 0 ? "▲" : regularMarketChange < 0 ? "▼" : "-")));
		sb.append(String.format(color + "%10s" + ConsoleColors.RESET,
				String.format("(%.2f%%)", regularMarketChangePercent)));
		sb.append(String.format("%15s", prettyTime(currentTimeMillis - (regularMarketTime * 1000))));
		sb.append(String.format("%12s", marketState));
		sb.append(String.format(" %-20s\n", longName));

		System.out.print(sb);
	}

	private static String prettyTime(long millis) {
		return String.format("%dm, %ds", TimeUnit.MILLISECONDS.toMinutes(millis),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}
}
