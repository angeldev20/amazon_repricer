import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Parser {
	public static final int INVALID_URL_STATUS_CODE = 404;

	protected Document parseURL(String url) throws IOException {
		System.setProperty("java.net.useSystemProxies", "true");

		Document doc = null;
		while (true) {
			try {
				doc = Jsoup.connect(url).get();
				break;
			} catch (HttpStatusException e) {
				if (e.getStatusCode() == INVALID_URL_STATUS_CODE) {
					throw new MalformedURLException("Invalid URL.");
				}
			} catch (SocketTimeoutException e) {
				// try connecting to URL again
			}
		}

		return doc;
	}
}
