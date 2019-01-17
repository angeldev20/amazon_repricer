import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ProductParser extends Parser {
	private static final String NAME_SELECTOR = "span[id=productTitle]";
	private static final String WEIGHT_SELECTOR = "li";

	private String url;
	private Document doc;

	public ProductParser(String url) throws IOException {
		this.url = url;
		this.doc = parseURL(url);
	}

	public Product parseProduct() {
		String name = parseName(doc);
		String asin = parseASIN(doc);
		double weight = parseWeight(doc);
		return new Product(name, asin, weight);
	}

	private String parseName(Document doc) {
		return doc.select(NAME_SELECTOR).first().text();
	}

	private String parseASIN(Document doc) {
		String[] split = url.split("/");
		return split[split.length - 1];
	}

	private double parseWeight(Document doc) {
		Elements weight = doc.select(WEIGHT_SELECTOR);

		for (Element e : weight) {
			String text = e.text();
			if (text.startsWith("Shipping Weight:")) {
				String trim = text.substring(text.indexOf(":") + 2);
				if (trim.contains("pound")) {
					return Double.parseDouble(trim.substring(0,
							trim.indexOf(" ")));
				} else {
					return -Double.parseDouble(trim.substring(0,
							trim.indexOf(" ")));
				}
			}
		}

		return 0.0;
	}

	public void setURL(String url) throws IOException {
		this.url = url;
		this.doc = parseURL(url);
	}

	public String getURL() {
		return url;
	}
}
