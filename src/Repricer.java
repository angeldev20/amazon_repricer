import java.util.ArrayList;

public class Repricer {
	private static final double VARIABLE_FEE_RATE = 0.15;
	private static final double VARIABLE_CLOSING_FEE = 1.35;
	private static final double PER_ITEM_FEE = 0.99;
	private static final double EPSILON = 0.00001;

	private Product product;
	private ArrayList<Listing> listings;

	private double priceFloor, priceCeiling;
	private int conditionFilter, ratingFilter;

	public Repricer(Product product, ArrayList<Listing> listings) {
		this.product = product;
		this.listings = listings;

		priceFloor = 0.0;
		priceCeiling = Double.MAX_VALUE;
		conditionFilter = ratingFilter = 0;
	}

	public double reprice(Listing seller) {
		if (seller == null) {
			seller = new Listing();
		}

		double reprice = priceFloor;
		for (int i = 0; i < listings.size(); i++) {
			Listing offer = listings.get(i);
			double total = offer.getTotal();

			if (total > priceCeiling) {
				break;
			} else if (total < priceFloor || offer.getRating() < ratingFilter
					|| offer.getSeller().equals(seller.getSeller())) {
				continue;
			}

			if (conditionFilter == Condition.NEW) {
				if (offer.getCondition() == Condition.NEW) {
					return offer.getTotal() - seller.getShipping();
				}
			} else if (offer.getCondition() >= conditionFilter - 1) {
				if (Math.abs(total - listings.get(0).getTotal()) < EPSILON) {
					return offer.getTotal() - seller.getShipping();
				} else {
					return offer.getTotal() - seller.getShipping() - 0.01;
				}
			}

			reprice = offer.getTotal();
		}

		return reprice - seller.getShipping();
	}

	public double calculateProfit(double price, double shipping) {
		return price + shipping - VARIABLE_FEE_RATE * price
				- VARIABLE_CLOSING_FEE - PER_ITEM_FEE
				- product.getShippingRate();
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setListings(ArrayList<Listing> listings) {
		this.listings = listings;
	}

	public void setPriceFloor(double price) {
		priceFloor = price;
	}

	public void setPriceCeiling(double price) {
		priceCeiling = price;
	}

	public void setConditionFilter(int filter) {
		conditionFilter = filter;
	}

	public void setRatingFilter(int filter) {
		ratingFilter = filter;
	}

	public Product getProduct() {
		return product;
	}

	public ArrayList<Listing> getListings() {
		return listings;
	}

	public double getPriceFloor() {
		return priceFloor;
	}

	public double getPriceCeiling() {
		return priceCeiling;
	}

	public int getConditionFilter() {
		return conditionFilter;
	}

	public int getRatingFilter() {
		return ratingFilter;
	}
}
