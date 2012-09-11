package datageek.entity;

import java.util.ArrayList;

/** Category Entity
 * @author hugo 
 *
 */
public class Category {
	/** ID of category */
	private int id;
	/** Name of category */
	private String name;
	
	/** list of coupons */
	private ArrayList<Item> couponList;
	
	/**
	 * Constructor without parameter
	 */
	public Category() {}
	
	
	/**
	 * Constructor with id and name
	 * @return
	 */
	public Category(int _id, String _name) {
		this.id = _id;
		this.name = _name;
		couponList = new ArrayList<Item>();
	}// End constructor with id and name
	
	public ArrayList<Item> getCouponList() {
		return couponList;
	}

	public void setCouponList(ArrayList<Item> couponList) {
		this.couponList = couponList;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
