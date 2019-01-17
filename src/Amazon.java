import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Amazon {
	private static final String PRODUCT_URL = "http://www.amazon.com/gp/product/";
	private static final String LISTING_URL_1 = "http://www.amazon.com/gp/offer-listing/";
	private static final String LISTING_URL_2 = "/ref=olp_tab_all";

	public static String sellerName, fileName;
	public static int targetRating;
	public static double minProfit;
	public static ArrayList<String> unprofitable;

	private static BufferedReader input;
	private static PrintWriter output;

	public void console() throws IOException {
		BufferedReader console = new BufferedReader(new InputStreamReader(
				System.in));
		System.out.println("Welcome to the Amazon repricing software!\n");

		System.out.print("Enter your Amazon seller name: ");
		sellerName = console.readLine();

		System.out.print("Enter your target seller rating (___%): ");
		targetRating = Integer.parseInt(console.readLine());
		System.out.println();

		System.out.print("Enter the file name for your product listings: ");
		fileName = console.readLine();

		unprofitable = new ArrayList<>();
		System.out.print("Enter your desired minimum profit: $");
		minProfit = Double.parseDouble(console.readLine());
		System.out.println();

		console.close();
	}

	public void file() throws IOException {
		input = new BufferedReader(new FileReader(fileName));
		output = new PrintWriter(new BufferedWriter(new FileWriter(
				trimFileName(fileName) + "_out.txt")));

		while (input.ready()) {
			StringTokenizer st = new StringTokenizer(input.readLine());
			String asin = st.nextToken();
			int condition = Integer.parseInt(st.nextToken());

			try {
				String url1 = PRODUCT_URL + asin;
				Product product = new ProductParser(url1).parseProduct();
				System.out.println(product);

				String url2 = LISTING_URL_1 + asin + LISTING_URL_2;
				ListingParser lp = new ListingParser(url2);
				ArrayList<Listing> listings = lp.parseListings();

				Repricer repricer = new Repricer(product, listings);
				Listing myListing = lp.getMyListing();

				repricer.setRatingFilter(targetRating);
				repricer.setConditionFilter(condition);

				double price = repricer.reprice(myListing);
				double profit = repricer.calculateProfit(price,
						myListing.getShipping());
				print(product.toString(), price, profit);
			} catch (MalformedURLException e) {
				System.out.println(e.getMessage() + asin + " not found.");
			}
		}

		input.close();
		output.close();
	}

	public void print(String name, double price, double profit)
			throws IOException {
		System.out.printf("New price: $%.2f%n", price);
		System.out.printf("Profit: %+.2f%n", profit);
		System.out.println();

		if (profit >= minProfit) {
			output.println(name);
			output.printf("%.2f%n", price);
			output.println();
		} else {
			unprofitable.add(name);
		}
	}

	public void run() throws IOException {
		console();
		file();

		if (!unprofitable.isEmpty()) {
			System.out.printf(
					"These products did not meet the $%.2f minimum profit:%n",
					minProfit);
			for (String s : unprofitable) {
				System.out.println(s);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		new Amazon().run();
	}

	private String trimFileName(String fileName) {
		return fileName.substring(0, fileName.indexOf("."));
	}
}
