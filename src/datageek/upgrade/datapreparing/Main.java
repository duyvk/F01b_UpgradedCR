package datageek.upgrade.datapreparing;

import java.util.List;

import datageek.entity.Item;

public class Main {

	public static final String dealsFile = "data/upgrade/deals.txt";
	public static final String categoriesFile = "data/upgrade/categories.txt";

	public static void main(String[] args) {
		DealsJsonParser parser = new DealsJsonParser(categoriesFile);
		List<Item> items = parser.getAllDeals(dealsFile);
		System.out.println("Total Items Count :: " + items.size());
		for (Item item : items) {
			System.out.println("deal is: " + item.deal);
			System.out.println("XXX: " + item.deal.get("categoryID"));
			//System.out.println("merchant is: " + item.merchant);
		}
		System.out.println("YYY: " + (int) (0.13*6));
	}

}
