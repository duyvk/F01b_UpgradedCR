package datageek.upgrade.datapreparing;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Random;
import java.util.Scanner;
import java.util.Set;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import datageek.entity.Category;
import datageek.entity.Item;
import datageek.entity.User;

public class UserInforGenertor {
	// Constant for friendship modeling
	final double A_THRESHOLD = 0.5;
	final double K_LIKEHOOD = 0.7;
	final double G_LIKEHOOD = 0.3;
	final int NUMBER_OF_FRIEND_FOR_EACH_USER = 10;
	final int NUMBER_OF_CATEGORY_FOR_EACH_USER = 3;
	final int NUMBER_OF_COUPON_FOR_EACH_CATEGORY = 5;
	
	/**
	 * All users in userList
	 */
	private ArrayList<User> userList;
	/**
	 * All coupons in couponList
	 */
	private ArrayList<Item> couponList;
	
	/**
	 * All categories in categoryList
	 */
	private ArrayList<Category> categoryList;
	
	// Setter and getter
	public ArrayList<User> getUserList() {
		return userList;
	}

	public void setUserList(ArrayList<User> userList) {
		this.userList = userList;
	}

	public ArrayList<Item> getCouponList() {
		return couponList;
	}

	public void setCouponList(ArrayList<Item> couponList) {
		this.couponList = couponList;
	}

	public ArrayList<Category> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<Category> categoryList) {
		this.categoryList = categoryList;
		
	}
	// End setters, getters
	
	/**
	 * Load all users
	 */
	public ArrayList<User> loadUser(File userFile) {
		userList = new ArrayList<User>();
		try {
			Scanner in = new Scanner(userFile);
			while(in.hasNext()) {
				String line = in.nextLine();
				if(line.startsWith("#") == true) {
					continue;
				}else {
					//1|24|M|technician|85711
					String[] userPropertises = line.split("\\|");
					
					User newUser = new User(Integer.parseInt(userPropertises[0]),
											Integer.parseInt(userPropertises[1]),
											userPropertises[2], userPropertises[3],
											userPropertises[4], 
											Double.parseDouble(userPropertises[5]), 
											Double.parseDouble(userPropertises[6]));
					/*User newUser = new User(Integer.parseInt(userPropertises[0]),
									Integer.parseInt(userPropertises[1]),
									userPropertises[2], userPropertises[3],
									userPropertises[4]);*/
							
					userList.add(newUser);
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userList;
	}// End loadUser method
	
	/**
	 * Load all categories
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public ArrayList<Category> loadCategory(File categoryFile) throws IOException, JSONException {
		categoryList = new ArrayList<Category>();
		String CATEGORY_ID = "categoryID";
		String CATEGORY = "category";
		
		StringBuffer catBuffer = new StringBuffer();
		FileInputStream cfs = new FileInputStream(new File(categoryFile.getPath()));
		DataInputStream cin = new DataInputStream(cfs);
		BufferedReader cbr = new BufferedReader(new InputStreamReader(cin));
		String s;
		while ((s = cbr.readLine()) != null) {
			catBuffer.append(s);
		}
		cin.close();
		cbr.close();

		JSONArray catArray = new JSONArray(catBuffer.toString());
		if (catArray.length() > 0) {
			for (int i = 0; i < catArray.length(); i++) {
				JSONObject obj = catArray.getJSONObject(i);
				if (obj.has(CATEGORY_ID) && obj.has(CATEGORY)) {
					Category newCategory = new Category(Integer.parseInt(obj.getString(CATEGORY_ID)), obj.getString(CATEGORY));
					categoryList.add(newCategory);
				}
			}
		}
		return categoryList;
	}// End loadCategory method
	
	/**
	 * Load all coupons
	 */
	public ArrayList<Item> loadCoupon(File couponFile) {
		couponList = new ArrayList<Item>();
		String categoriesFileName = "data/upgrade/categories.txt";
		DealsJsonParser djp = new DealsJsonParser(categoriesFileName);
		couponList.addAll(djp.getAllDeals(couponFile.getPath()));
		return couponList;
	}// End loadCoupon method
	
	////////////////////////////////////////////////////////////////////////////
	// RANDOM GENERATORS
	/**
	 * Generate a double number from 0.00 to 1.00
	 * @return a number with format 0.XX
	 */
	private double genDouble() {
		Random ran = new Random();
		return Math.round(ran.nextDouble()*100.0)/100.0;
	}// End randomGenDouble method
	
	/**
	 * Generate random integer number from 0 to maxNum-1
	 * @param maxNum is maximum number for random range
	 * @return a random integer between 0 and maxNum
	 */
	public static int genIntegerInRange(int maxNum) {
		Random ran = new Random();
		int ranNum = ran.nextInt(maxNum);
		/*if(ranNum < 1) {
			genIntegerInRange(maxNum);
		}*/
		return ranNum;
	}// End genIntegerInRange method
	
	/**
	 * Generate random integer list from 0 to maxNum-1
	 * @param maxNum
	 * @param amoutNumber
	 * @return a list of integer numbers
	 */
	private ArrayList<Integer> genIntegerList(int maxNum, int amoutNumber){
		ArrayList<Integer> integerList = new ArrayList<Integer>();
		for(int i=0; i<amoutNumber; i++) {
			integerList = genIntegerRecursion(maxNum, integerList);
		}
		return integerList;
	}// End genIntegerList method
	
	/**
	 * Private method for generate integer list
	 * @param maxNum
	 * @param list
	 * @return
	 */
	private ArrayList<Integer> genIntegerRecursion(int maxNum, ArrayList<Integer> list) {
		int ranNum = genIntegerInRange(maxNum);
		if(list.contains(ranNum) == false) {
			list.add(ranNum);
		}else {
			genIntegerRecursion(maxNum, list);
		}
		return list;
	}// End genIntegerRecursion method
	
	////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Set random interest category score for User u 
	 * @param u is user
	 * @param c is category
	 */
	public void setInterestScore(User u, Category c) {
		u.getCategoryChooseList().put(c, genDouble());
	}// End setInterest method
	
	/**
	 * Get interest score between user and category
	 * @param u
	 * @param c
	 * @return score, double type
	 */
	public double getInterestScore(User u, Category c) {
		return u.getCategoryChooseList().get(c);
	}// End getInterestScore method
	
	
	/**
	 * Generate random coupon list for each user, for each category
	 * @param u is user
	 * @param listOfItems is a list which contains all items
	 */
	public void couponListRandomForUser(User u) {
		ArrayList<Integer> iList = genIntegerList(categoryList.size(), NUMBER_OF_CATEGORY_FOR_EACH_USER);
		for(int i: iList) {
			Category c = categoryList.get(i);
			// E*F(i,n)
			//int numberOfCouponsInCategoryForUser = (int)getInterestScore(u, c) 
			//										* countCouponInCategory(c);
			int numberOfCouponsInCategoryForUser = NUMBER_OF_COUPON_FOR_EACH_CATEGORY;
			
			
			// Generate list of item index to assign for user u
			ArrayList<Integer> listOfItemIndex = new ArrayList<Integer>();
			if(numberOfCouponsInCategoryForUser > c.getCouponList().size()) {
				for(int index = 0; index<c.getCouponList().size(); index++) {
					listOfItemIndex.add(index);
				}
			}else {
				listOfItemIndex.addAll(genIntegerList(c.getCouponList().size()
											, numberOfCouponsInCategoryForUser));	
			}
			
			for(Integer iItem : listOfItemIndex) {
				u.getCouponChooseList().put(c.getCouponList().get(iItem), 0.0);
			}
		}
		
	
		
	}// End couponListRandomForUser method
	
	/**
	 * Get coupon list for each category
	 */
	public void getCouponListForCategory() {
		for(Category c : categoryList) {
			for(Item i : couponList) {
				if(c.getId() == Integer.parseInt(i.deal.get("categoryID"))) {
					c.getCouponList().add(i);
				}
			}
		}
	}// End getCouponListForCategory method
	
/*	*//**
	 * Get list of category with m elements have highest score
	 *//*
	public ArrayList<Category> getMCategory(User u, int m){
		ArrayList<Category> mCategoryList = new ArrayList<Category>();
		TreeMap<Category, Double> tm= new TreeMap<Category, Double>(u.getCategoryChooseList()); 
		int i = 0;
		for(Category c : tm.keySet()) {
			if(i<m) {
				mCategoryList.add(c);
				i++;
			}else {
				break;
			}
		}
		return mCategoryList;
	}// End getMCategory method
*/	
	/**
	 * Count total coupon in category c
	 * @param c
	 * @return number of total coupon
	 */
	public int countCouponInCategory(Category c) {
		int totalCoupon = 0;
		for(Item i:couponList) {
			if(Integer.parseInt(i.deal.get("categoryID")) == c.getId()) {
				totalCoupon++;
			}
		}
		return totalCoupon;
	}// End countCouponInCategory method
	
	/**
	 * Set friend list for a user
	 * @param u user who is assigned friends
	 */
	public void setFriendForUser(User u) {
		ArrayList<User> _userList = new ArrayList<User>();
		_userList.addAll(userList);
		_userList.remove(u);
		
		// R
		//int randomNumberFriend = genIntegerInRange(_userList.size());
		
		int randomNumberFriend = genIntegerInRange(NUMBER_OF_FRIEND_FOR_EACH_USER);
		
		if(randomNumberFriend > u.getFriendList().size()) {
			randomNumberFriend = randomNumberFriend - u.getFriendList().size();
			//System.out.println("XXX: " + randomNumberFriend);
			// Random friend list 
			ArrayList<Integer> friendListIndex = genIntegerList(_userList.size()
															  , randomNumberFriend);
			
			// Set true friend
			for(Integer i : friendListIndex) {
				if(u.getFriendList().containsKey(_userList.get(i))==false) {
					u.getFriendList().put(_userList.get(i), 1.0);
					userList.get(userList.indexOf(_userList.get(i))).getFriendList().put(u, 1.0);	
				}
				
			}
		}
		
		ArrayList<User> userInFriendList = new ArrayList<User>();
		userInFriendList.addAll(u.getFriendList().keySet());
		
		System.out.println("-----------------------------------------------");
		System.out.println("Size of friend: " + userInFriendList.size());
		
		
		for(int i = 0 ; i< userInFriendList.size(); i++) {
			for(int j = 0; j < _userList.size(); j++) {
				if(userInFriendList.get(i).equals(_userList.get(j))==true) {
					_userList.remove(j);
				}
			}
		}
		System.out.println("userList Removed: " + _userList.size());
		
		for(User uX : _userList) {
			double likehoodScore = setLikehoodFriend(u, uX);
			if(likehoodScore < A_THRESHOLD) {
				u.getLikehoodFriendList().put(uX, K_LIKEHOOD);	
			}else {
				u.getLikehoodFriendList().put(uX, G_LIKEHOOD);
			}
		}
		System.out.println("likehood list: " + u.getLikehoodFriendList().size());
	}// End setFriendForUser method
	
	/**
	 * 
	 * @param u
	 * @param anotherUser
	 * @return a double number presents relationship between user and another 
	 */
	public double setLikehoodFriend(User u, User anotherUser) {
		double relationshipScore = 0.0;
		Set<Category> uKeySet = u.getCategoryChooseList().keySet();
		Set<Category> auKeySet = anotherUser.getCategoryChooseList().keySet();
		
		for(Category uc : uKeySet) {
			for(Category auc : auKeySet) {
				if(uc == auc) {
					relationshipScore += Math.abs(
							u.getCategoryChooseList().get(uc) 
							- anotherUser.getCategoryChooseList().get(auc));
				}
			}
		}
		return relationshipScore;
	}// End setLikehoodFriend method

	/**
	 * Generate interest scores u and c 
	 */
	public void genInterestScores() {
		for(User u : userList) {
			for(Category c : categoryList) {
				setInterestScore(u, c);
			}
		}
	}// End genInterestScores method
	
	/**
	 * Generate friends for all users
	 */
	public void genFriendForUsers() {
		ArrayList<User> tmp = new ArrayList<User>();
		tmp.addAll(getUserList());
		for(User u : tmp) {
			setFriendForUser(u);
		}
	}
	
	/**
	 * Generate coupon list for all users
	 */
	public void genCouponForUsers() {
		for(User u: userList) {
			couponListRandomForUser(u);
		}
	}
	
	//GEN data based on format
	/**
	 * Format user friend list based on the format 
	 * 10 | 5, 3, 6, 12
	 * 12 | 10, 23, 32
	 * @param u
	 * @return
	 */
	public String formatUserFriend(User u) {
		String formatFriends = new String();
		formatFriends += u.getId() + "|";
		
		for(User uFriend : u.getFriendList().keySet()) {
			formatFriends += uFriend.getId()+";";
		}
		
		return formatFriends;
	}
	
	/**
	 * Format user coupon history based on the format:
	 * 10 | 3 | 2012-08-28
	 * 10 | 6 | 2012-08-28
	 * 10 | 18 | 2012-09-02
	 * @param u
	 * @return
	 */
	public String formatUserCouponHistory(User u) {
		String formatUserCoupon = new String();
		
		for(Item i : u.getCouponChooseList().keySet()) {
			formatUserCoupon += u.getId() + "|" + i.getDeal().get("id") + "|" + genDate()+"\n";
		}
		
		return formatUserCoupon.trim();
	}
	
	/**
	 * Generate date
	 * @return the date String with format YYYY-MM-DD
	 */
	public String genDate() {
		//String date = new String();
		int[] years = {2012, 2011, 2010, 2009, 2008, 2007, 2006, 2005};
		int[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
		int[] days = {1, 2, 3, 4, 5, 6, 7,8,9,10,11,
						12,13,14,15,16,17,18,19,20,21
					  , 22, 23, 24, 25, 26, 27, 28};
		
		String dateGen;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar date = Calendar.getInstance(); // the current date and time
		
		int  yearIdx = genIntegerInRange(years.length);
		int monthIdx = genIntegerInRange(months.length);
		int dayIdx = genIntegerInRange(days.length);
		
		int year = years[yearIdx];
		int month = months[monthIdx];
		int day = days[dayIdx];
		
		date.set(year, month, day);
		dateGen = df.format(date.getTime());
		
		return dateGen;
	}
	
	/**
	 * Gen coupon files based on format 
	 * coupon_id| coupon_title| coupon_details| coupon_merchant| merchant_lat|merchant_lon| coupon_category| coupon_expirationdate| coupon_receivingdate
	 */
	
	public String formatCoupon(Item i) {
		String strCoupon = new String();
		strCoupon += i.getDeal().get("id") 
					+ "|" + i.getDeal().get("title")
					+ "|" + i.getDeal().get("description")
					+ "|" + i.getMerchant().get("name")
					+ "|" + i.getMerchant().get("latitude")
					+ "|" + i.getMerchant().get("longitude")
					+ "|" + i.deal.get("categoryID")
					+ "|" + i.deal.get("coupon_expiration")
					+ "|" + i.deal.get("receivingDate");
		
		return strCoupon;
	}
	
	
	/**
	 * Runner
	 * @throws JSONException 
	 * @throws IOException 
	 */
	public static void runner() throws IOException, JSONException {
		File userFile = new File("data/upgrade/user");
		File categoryFile = new File("data/upgrade/categories.txt");
		File couponFile = new File("data/upgrade/deals.txt");
		
		
		UserInforGenertor one = new UserInforGenertor();
		
		//LOAD DATA
		one.loadUser(userFile);
		one.loadCategory(categoryFile);
		one.loadCoupon(couponFile);
		
		//GEN DATA
		one.genFriendForUsers();
		one.genInterestScores();
		one.getCouponListForCategory();
		one.genCouponForUsers();
		
		//WRITE DOWN FILE
		File fUserGraph = new File("data/upgrade/user-friend.txt");
		PrintWriter outUserFriend = new PrintWriter(fUserGraph);
		for(User u : one.getUserList()) {
			outUserFriend.println(one.formatUserFriend(u));
		}
		outUserFriend.close();
		
		
		File fUserCoupon = new File("data/upgrade/user-coupon.txt");
		PrintWriter outUserCoupon = new PrintWriter(fUserCoupon);
		for(User u : one.getUserList()) {
			outUserCoupon.println(one.formatUserCouponHistory(u));
		}
		outUserCoupon.close();
		
		
		File fOldCoupon = new File("data/upgrade/old-coupons.txt");
		PrintWriter outOldCoupon = new PrintWriter(fOldCoupon);
		for(Item i: one.getCouponList()) {
			outOldCoupon.println(one.formatCoupon(i));	
		}
		outOldCoupon.close();
		
		
		File fUserCate = new File("data/upgrade/user-cate.txt");
		PrintWriter outUserCate = new PrintWriter(fUserCate);
		
		for(User u : one.getUserList()) {
			for(Category c : u.getCategoryChooseList().keySet()) {
				outUserCate.println(u.getId() + "|" + c.getId() + "|" +u.getCategoryChooseList().get(c));
			}
		}
		
		outUserCate.close();
		
		//1|2
		//1|3
		//1|4
		File fCouponCategory = new File("data/upgrade/coupon-category.txt");
		PrintWriter outCouponCategory = new PrintWriter(fCouponCategory);
		
		for(Category c : one.getCategoryList()) {
			
			for(Item i : c.getCouponList()) {
				outCouponCategory.println(c.getId() + "|" + i.getDeal().get("id"));
			}
		}
		
		outCouponCategory.close();

	}
	
	
	/**
	 * @param args
	 * @throws JSONException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, JSONException {
		runner();
		
/*		File userFile = new File("data/upgrade/user");
		File categoryFile = new File("data/upgrade/categories.txt");
		File couponFile = new File("data/upgrade/deals.txt");
		
		
		UserInforGenertor one = new UserInforGenertor();
		one.loadUser(userFile);
		one.loadCategory(categoryFile);
		one.loadCoupon(couponFile);
		
		one.genFriendForUsers();
		one.genInterestScores();
		one.getCouponListForCategory();
		one.genCouponForUsers();
		
		
		File outFile = new File("data/upgrade/usergraph.txt");
		PrintWriter out = new PrintWriter(outFile);
		for(User u : one.getUserList()) {
			out.println("User ID:" + u.getId());
			u.printFriendList(out);
			u.printLikeHoodFriends(out);
			out.println("------------------------------------------------------");
		}
		out.close();
		

		File outFile1 = new File("data/upgrade/user-coupon-history.txt");
		PrintWriter out1 = new PrintWriter(outFile1);
		for(User u : one.getUserList()) {
			out1.println("User ID:" + u.getId());
			u.printCouponList(out1);
			out1.println("------------------------------------------------------");
		}
		out1.close();

		File fUserGraph = new File("data/upgrade/user-friend.txt");
		PrintWriter outUserFriend = new PrintWriter(fUserGraph);
		for(User u : one.getUserList()) {
			outUserFriend.println(one.formatUserFriend(u));
		}
		outUserFriend.close();
		
		
		File fUserCoupon = new File("data/upgrade/user-coupon.txt");
		PrintWriter outUserCoupon = new PrintWriter(fUserCoupon);
		for(User u : one.getUserList()) {
			outUserCoupon.println(one.formatUserCouponHistory(u));
		}
		outUserCoupon.close();
		
		File fOldCoupon = new File("data/upgrade/old-coupons.txt");
		PrintWriter outOldCoupon = new PrintWriter(fOldCoupon);
		
		//int numOldCoupon = one.getCouponList().size()/2;
		//int count = 0;
		for(Item i: one.getCouponList()) {
			//if(count < numOldCoupon) {
				outOldCoupon.println(one.formatCoupon(i));	
			//}
			
			//count++;
		}
		outOldCoupon.close();
		
		
		File fNewCoupon = new File("data/upgrade/new-coupons.txt");
		PrintWriter outNewCoupon = new PrintWriter(fNewCoupon);
		
		ArrayList<Item> newcoupon = one.loadCoupon(new File("data/upgrade/deals-new.txt"));
		for(Item i: newcoupon) {
			outNewCoupon.println(one.formatCoupon(i));	
		}
		outNewCoupon.close();
		
		
		File fUserCate = new File("data/upgrade/user-cate.txt");
		PrintWriter outUserCate = new PrintWriter(fUserCate);
		
		for(User u : one.getUserList()) {
			for(Category c : u.getCategoryChooseList().keySet()) {
				outUserCate.println(u.getId() + "|" + c.getId() + "|" +u.getCategoryChooseList().get(c));
			}
		}
		
		outUserCate.close();
		
		//1|2
		//1|3
		//1|4
		File fCouponCategory = new File("data/upgrade/coupon-category.txt");
		PrintWriter outCouponCategory = new PrintWriter(fCouponCategory);
		
		for(Category c : one.getCategoryList()) {
			
			for(Item i : c.getCouponList()) {
				outCouponCategory.println(c.getId() + "|" + i.getDeal().get("id"));
			}
		}
		
		outCouponCategory.close();
		
		
		System.out.println("User size: " + one.getUserList().size());
		System.out.println("DONE!");
		
		String x= "132;351;667;756;";
		String[] xa = x.split(";");
		System.out.println("length : " + xa.length);*/
	}
}

