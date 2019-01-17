public class Listing implements Comparable<Listing> {
	private static final double DEFAULT_SHIPPING = 3.99;

	private String seller;
	private double price, shipping, tax, total;
	private int rating, totalRatings, condition;

	public Listing() {
		seller = Amazon.sellerName;
		shipping = DEFAULT_SHIPPING;
		total = shipping;
	}

	public Listing(String seller, double price, double shipping, double tax,
			int rating, int totalRatings, int condition) {
		this.seller = seller;
		this.price = price;
		this.shipping = shipping;
		this.tax = tax;
		total = price + shipping + tax;
		this.rating = rating;
		this.totalRatings = totalRatings;
		this.condition = condition;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setShipping(double shipping) {
		this.shipping = shipping;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public void setTotalRatings(int totalRatings) {
		this.totalRatings = totalRatings;
	}

	public void setCondition(int condition) {
		this.condition = condition;
	}

	public double getPrice() {
		return price;
	}

	public double getShipping() {
		return shipping;
	}

	public double getTax() {
		return tax;
	}

	public double getTotal() {
		return total;
	}

	public String getSeller() {
		return seller;
	}

	public int getRating() {
		return rating;
	}

	public int getTotalRatings() {
		return totalRatings;
	}

	public int getCondition() {
		return condition;
	}

	@Override
	public int compareTo(Listing o) {
		if (total == o.total) {
			if (condition == o.condition) {
				if (rating == o.rating) {
					return totalRatings > o.totalRatings ? -1 : 1;
				}
				return rating > o.rating ? -1 : 1;
			}
			return condition > o.condition ? -1 : 1;
		}
		return total < o.total ? -1 : 1;
	}

	@Override
	public String toString() {
		return String.format("%s: $%.2f + $%.2f shipping%n", seller, price,
				shipping);
	}
}
