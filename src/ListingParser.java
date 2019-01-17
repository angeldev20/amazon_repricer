import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ListingParser extends Parser {
	private static final String PRICE_SELECTOR = "span[class*=olpOfferPrice]";
	private static final String SHIPPING_SELECTOR = "p[class=olpShippingInfo]";
	private static final String TAX_SELECTOR = ""; // TODO
	private static final String SELLER_SELECTOR = "p[class*=olpSellerName]";
	private static final String RATING_SELECTOR = "p[class=a-spacing-small]";
	private static final String CONDITION_SELECTOR = "h3[class*=olpCondition]";
	private static final String NEXT_PAGE_SELECTOR = "a[href^=/gp/offer-listing/]";

	private String url;
	private Document doc;
	private ArrayList<Listing> listings;

	private int amazonIndex;
	private ArrayList<String> seller;
	private ArrayList<Double> price, shipping, tax;
	private ArrayList<Integer> rating, totalRatings, condition;

	public ListingParser(String url) throws IOException {
		this.url = url;
		this.doc = parseURL(url);
		listings = new ArrayList<>();

		amazonIndex = -1;
		seller = new ArrayList<>();
		price = new ArrayList<>();
		shipping = new ArrayList<>();
		tax = new ArrayList<>();
		rating = new ArrayList<>();
		totalRatings = new ArrayList<>();
		condition = new ArrayList<>();
	}

	public ArrayList<Listing> parseListings() throws IOException {
		while (true) {
			parsePrice(doc);
			parseShipping(doc);
			parseTax(doc);
			parseSeller(doc);
			parseRating(doc);
			parseTotalRatings(doc);
			parseCondition(doc);

			url = parseNextPage(doc);
			if (url == null) {
				break;
			}
			doc = parseURL(url);
		}

		listings = getListings();
		return listings;
	}

	private void parsePrice(Document doc) {
		Elements price = doc.select(PRICE_SELECTOR);
		for (Element e : price) {
			this.price.add(Double.parseDouble(e.text().replaceAll("[$,]", "")));
		}
	}

	private void parseShipping(Document doc) {
		Elements shipping = doc.select(SHIPPING_SELECTOR);
		for (Element e : shipping) {
			if (e.text().contains("FREE Shipping")) {
				this.shipping.add(0.0);
			} else {
				this.shipping.add(Double.parseDouble(e.text().replaceAll(
						"[^\\d.]", "")));
			}
		}
	}

	private void parseTax(Document doc) {
		// TODO - cssQuery doesn't work
	}

	private void parseSeller(Document doc) {
		Elements seller = doc.select(SELLER_SELECTOR);
		for (Element e : seller) {
			if (e.text().isEmpty()) {
				amazonIndex = this.seller.size();
				this.seller.add("Amazon.com");
			} else {
				this.seller.add(e.text());
			}
		}
	}

	private void parseRating(Document doc) {
		Elements rating = doc.select(RATING_SELECTOR);
		for (int i = 0; i < rating.size(); i++) {
			String text = rating.get(i).text();
			if (this.rating.size() == amazonIndex) {
				this.rating.add(100);
				i--;
			} else if (text.startsWith("Just Launched")) {
				this.rating.add(100);
			} else {
				this.rating.add(Integer.parseInt(text.substring(0,
						text.indexOf("%"))));
			}
		}
	}

	private void parseTotalRatings(Document doc) {
		Elements totalRatings = doc.select(RATING_SELECTOR);
		for (int i = 0; i < totalRatings.size(); i++) {
			String text = totalRatings.get(i).text();
			if (this.totalRatings.size() == amazonIndex) {
				this.totalRatings.add(Integer.MAX_VALUE);
				i--;
			} else if (text.startsWith("Just Launched")) {
				this.totalRatings.add(0);
			} else {
				String count = text.substring(text.indexOf("(") + 1)
						.replaceAll("\\D", "");
				this.totalRatings.add(Integer.parseInt(count));
			}
		}
	}

	private void parseCondition(Document doc) {
		Elements condition = doc.select(CONDITION_SELECTOR);
		for (Element e : condition) {
			switch (e.text()) {
			case "New":
				this.condition.add(Condition.NEW);
				break;
			case "Used - Like New":
				this.condition.add(Condition.USED_LIKE_NEW);
				break;
			case "Used - Very Good":
				this.condition.add(Condition.USED_VERY_GOOD);
				break;
			case "Used - Good":
				this.condition.add(Condition.USED_GOOD);
				break;
			case "Used - Acceptable":
				this.condition.add(Condition.USED_ACCEPTABLE);
				break;
			case "Collectible - Like New":
				this.condition.add(Condition.COLLECTIBLE_LIKE_NEW);
				break;
			case "Collectible - Very Good":
				this.condition.add(Condition.COLLECTIBLE_VERY_GOOD);
				break;
			case "Collectible - Good":
				this.condition.add(Condition.COLLECTIBLE_GOOD);
				break;
			case "Collectible - Acceptable":
				this.condition.add(Condition.COLLECTIBLE_ACCEPTABLE);
				break;
			}
		}
	}

	private String parseNextPage(Document doc) {
		Elements pages = doc.select(NEXT_PAGE_SELECTOR);
		for (Element e : pages) {
			if (e.text().startsWith("Next")) {
				return e.attr("abs:href");
			}
		}
		return null;
	}

	private ArrayList<Listing> getListings() {
		ArrayList<Listing> listings = new ArrayList<>();

		if (price.size() != shipping.size() && shipping.size() != seller.size()
				&& seller.size() != rating.size()
				&& rating.size() != totalRatings.size()
				&& totalRatings.size() != condition.size()) {
			throw new IllegalStateException("Unequal number of listings.");
		}

		for (int i = 0; i < price.size(); i++) {
			Listing offer = new Listing();
			offer.setPrice(price.get(i));
			offer.setShipping(shipping.get(i));
			offer.setTax(0.0); // TODO - tax rate
			offer.setTotal(offer.getPrice() + offer.getShipping()
					+ offer.getTax());
			offer.setSeller(seller.get(i));
			offer.setRating(rating.get(i));
			offer.setTotalRatings(totalRatings.get(i));
			offer.setCondition(condition.get(i));
			listings.add(offer);
		}

		Collections.sort(listings);
		return listings;
	}

	public void setURL(String url) throws IOException {
		this.url = url;
		this.doc = parseURL(url);
	}

	public String getURL() {
		return url;
	}

	public Listing getMyListing() {
		for (Listing offer : listings) {
			if (offer.getSeller().equals(Amazon.sellerName)) {
				return offer;
			}
		}
		return new Listing();
	}
}
