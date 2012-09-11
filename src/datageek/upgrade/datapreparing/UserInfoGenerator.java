package datageek.upgrade.datapreparing;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import datageek.entity.Category;
import datageek.entity.Item;
import datageek.entity.User;

public class UserInfoGenerator {
	// Constant for friendship modeling
	final double A_THRESHOLD = 0.5;
	final double K_LIKEHOOD = 0.7;
	final double G_LIKEHOOD = 0.3;
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
	public void loadUser(File userFile) {
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
											userPropertises[4]);
					userList.add(newUser);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// End loadUser method
	
	/**
	 * Load all categories
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public void loadCategory(File categoryFile) throws IOException, JSONException {
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
	}// End loadCategory method
	
	/**
	 * Load all coupons
	 */
	public void loadCoupon(File couponFile) {
		couponList = new ArrayList<Item>();
		String categoriesFileName = "data/upgrade/categories.txt";
		DealsJsonParser djp = new DealsJsonParser(categoriesFileName);
		couponList.addAll(djp.getAllDeals(couponFile.getPath()));
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
	private int genIntegerInRange(int maxNum) {
		Random ran = new Random();
		int ranNum = ran.nextInt(maxNum); 
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
		for(Category c : categoryList) {
			// E*F(i,n)
			int numberOfCouponsInCategoryForUser = (int)getInterestScore(u, c) 
													* countCouponInCategory(c);
			if(numberOfCouponsInCategoryForUser < 1) {
				numberOfCouponsInCategoryForUser = 1;	
			}
			
			// Generate list of item index to assign for user u
			ArrayList<Integer> listOfItemIndex = new ArrayList<Integer>();
			listOfItemIndex.addAll(genIntegerList(couponList.size()
									, numberOfCouponsInCategoryForUser));
							
			
			for(Integer i : listOfItemIndex) {
				u.getCouponChooseList().put(couponList.get(i), 0.0);
			}
		}
	}// End couponListRandomForUser method
	

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
		int randomNumberFriend = genIntegerInRange(_userList.size());
		
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

		//Set likehoodFriend
/*		ArrayList<Integer> likehoodFriendIndex = new ArrayList<Integer>();
		for(int i = 0; i < _userList.size(); i++) {
			for(Integer in:friendListIndex) {
				if(i!=in) {
					likehoodFriendIndex.add(i);
				}
			}
		}*/
/*		
		for(Integer i : likehoodFriendIndex) {
			double likehoodScore = setLikehoodFriend(u, _userList.get(i));
			if(likehoodScore < A_THRESHOLD) {
				u.getLikehoodFriendList().put(_userList.get(i), K_LIKEHOOD);	
			}else {
				u.getLikehoodFriendList().put(_userList.get(i), G_LIKEHOOD);
			}
		}*/
		
		
		
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
		
		/*for(ListIterator<User> userItr = userList.listIterator(); userItr.hasNext();) {
			User u = userItr.next();
			setFriendForUser(u);
		}*/
		
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
	
	
	/**
	 * @param args
	 * @throws JSONException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, JSONException {
		// TODO Auto-generated method stub
		File userFile = new File("data/upgrade/user");
		File categoryFile = new File("data/upgrade/categories.txt");
		File couponFile = new File("data/upgrade/deals.txt");
		
		
		
		UserInfoGenerator one = new UserInfoGenerator();
		one.loadUser(userFile);
		one.loadCategory(categoryFile);
		one.loadCoupon(couponFile);
		
		one.genFriendForUsers();
		one.genInterestScores();
		one.genCouponForUsers();
		/*
		File outFile = new File("data/upgrade/usergraph.txt");
		PrintWriter out = new PrintWriter(outFile);
		for(User u : one.getUserList()) {
			out.println("User ID:" + u.getId());
			u.printFriendList(out);
			u.printLikeHoodFriends(out);
			out.println("------------------------------------------------------");
		}
		out.close();
		*/

		File outFile = new File("data/upgrade/user-coupon-history.txt");
		PrintWriter out = new PrintWriter(outFile);
		for(User u : one.getUserList()) {
			out.println("User ID:" + u.getId());
			u.printCouponList(out);
			out.println("------------------------------------------------------");
		}
		out.close();
		
		
		/*
		System.out.println("----------------------------------------------------------------------");
		System.out.println("Total: " + (one.getUserList().get(500).getFriendList().size() + one.getUserList().get(500).getLikehoodFriendList().size()));
		System.out.println("of likehood: " + (one.getUserList().get(500).getLikehoodFriendList().size()));
		System.out.println("of friend: " + (one.getUserList().get(500).getFriendList().size()));
		
		System.out.println("CATEGORY LIST \n-------------------------------------------------");
		one.getUserList().get(0).printCategoryList();
		System.out.println("FRIEND LIST \n-------------------------------------------------");
		one.getUserList().get(0).printFriendList();
		System.out.println("LIKEHOOD FRIEND LIST \n-------------------------------------------------");
		one.getUserList().get(0).printLikeHoodFriends();
		System.out.println("COUPON LIST \n-------------------------------------------------");
		one.getUserList().get(0).printCouponList();
		System.out.println("-------------------------------------------------");
		*/
		System.out.println("User size: " + one.getUserList().size());
		System.out.println("DONE!");
		
		
	}
}
