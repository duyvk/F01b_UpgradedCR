package datageek.entity;

import java.io.PrintWriter;
import java.util.HashMap;

/**User Entity 
 * @author hugo
 *
 */
public class User {
	/**
	 * User ID
	 */
	private int id;
	
	/**
	 * User Age
	 */
	private int age;
	
	/**
	 * User gender
	 */
	
	private String gender;
	
	/**
	 * User occupation
	 */
	private String occupation;
	
	/**
	 * User's Location zip code
	 */
	private String zipCode;

	/**
	 * Coupon choose list with coupon score 
	 */
	private HashMap<Item, Double> couponChooseList;
	
	/**
	 * Friend list with friend score
	 */
	private HashMap<User, Double> friendList;
	
	/**
	 * Likehood friend list
	 */
	private HashMap<User, Double> likehoodFriendList;
	
	/**
	 * Category Choose List  with category score
	 */
	private HashMap<Category,Double> categoryChooseList;

	
	/**
	 * Constructor without parameter
	 */
	public User(){
		couponChooseList = new HashMap<Item, Double>();
		friendList = new HashMap<User, Double>();
		categoryChooseList = new HashMap<Category, Double>();
		likehoodFriendList = new HashMap<User, Double>();
	}
	
	/**
	 * Constructor with id
	 * @param _id
	 */
	public User(int _id) {
		this.id = _id;
		couponChooseList = new HashMap<Item, Double>();
		friendList = new HashMap<User, Double>();
		categoryChooseList = new HashMap<Category, Double>();
		likehoodFriendList = new HashMap<User, Double>();
	}
	
	/**
	 * Constructowr with all fields
	 * @param _id
	 * @param _age
	 * @param _gender
	 * @param _occupation
	 * @param _zipcode
	 */
	public User(int _id, int _age, String _gender, String _occupation, String _zipcode) {
		this.id = _id;
		this.age = _age;
		this.gender = _gender;
		this.occupation = _occupation;
		this.zipCode = _zipcode;
		couponChooseList = new HashMap<Item, Double>();
		friendList = new HashMap<User, Double>();
		categoryChooseList = new HashMap<Category, Double>();
		likehoodFriendList = new HashMap<User, Double>();
	}// End Constructor
	
	public HashMap<User, Double> getLikehoodFriendList() {
		return likehoodFriendList;
	}

	public void setLikehoodFriendList(HashMap<User, Double> likehoodFriendList) {
		this.likehoodFriendList = likehoodFriendList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	public HashMap<Item, Double> getCouponChooseList() {
		return couponChooseList;
	}

	public void setCouponChooseList(HashMap<Item, Double> couponChooseList) {
		this.couponChooseList = couponChooseList;
	}

	public HashMap<User, Double> getFriendList() {
		return friendList;
	}

	public void setFriendList(HashMap<User, Double> friendList) {
		this.friendList = friendList;
	}

	public HashMap<Category, Double> getCategoryChooseList() {
		return categoryChooseList;
	}

	public void setCategoryChooseList(HashMap<Category, Double> categoryChooseList) {
		this.categoryChooseList = categoryChooseList;
	}
	
	/**
	 * Print friend list
	 */
	public void printFriends() {
		for(User u : getFriendList().keySet()) {
			System.out.println("Likehood ID: " + u.getId());
		}
	}
	
	/**
	 * 
	 */
	
	
	/**
	 * Print likehood friend list
	 */
	public void printLikeHoodFriends(PrintWriter out) {
		for(User u : likehoodFriendList.keySet()) {
			out.println("Likehood ID: " + u.getId());
		}
	}
	
	
	/**
	 * Print friend list
	 */
	public void printFriendList(PrintWriter out) {
		for(User u : friendList.keySet()) {
			out.println("Friend ID: " + u.getId());
		}
	}
	
	/**
	 * Print categogry
	 */
	public void printCategoryList(PrintWriter out) {
		for(Category c : categoryChooseList.keySet()) {
			out.println("Category ID: " + c.getId() + "   f(u,c) = " + categoryChooseList.get(c));
			
		}
	}
	
	/**
	 * Print Coupon list
	 */
	public void printCouponList(PrintWriter out) {
		//out.println("size of: " + couponChooseList.size());
		for(Item i : couponChooseList.keySet()) {
			out.println("CouponID: " + i.getDeal().get("id"));
		}
	}
	
	/**
	 * Compare two users 
	 * @param anotherUser
	 * @return true if two ids are equals. Otherwise, return false
	 */
	public boolean equals(User anotherUser) {
		if(this.getId() ==  anotherUser.getId()) {
			return true;
		}
		return false;
	}
}
