package datageek.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.json.JSONException;
import org.omg.CORBA.FREE_MEM;

import datageek.entity.Category;
import datageek.entity.Item;
import datageek.entity.User;
import datageek.upgrade.datapreparing.UserInforGenertor;
import datageek.util.Sorter;

/**
 * The interface implementation for interact with GUI
 * @author hugo
 *
 */
public class InteracterGUI {
	
	final double RECOMMEND_THRESHOLD = 0.01;
	final double LON_BIAS = 0.005;
	final double LAT_BIAS = 0.005;
	
	
	final  String fNewCoupon = "data/upgrade/deals-new.txt";
	final  String fOldCoupon = "data/upgrade/deals-old.txt";
	final  String fCoupon = "data/upgrade/deals.txt";
	
	final  String fUser = "data/upgrade/user";
	
	
	final  String fUserCoupon = "data/upgrade/user-coupon.txt";
	final  String fUserFriend = "data/upgrade/user-friend.txt";
	final String fUserCate = "data/upgrade/user-cate.txt";
	final String fCategory = "data/upgrade/categories.txt";
	final String fCouponCategory = "data/upgrade/coupon-category.txt";
	
	final  int userID; //userid in session
	
	ArrayList<Item> newCoupons = new ArrayList<Item>();
	ArrayList<Item> oldCoupons = new ArrayList<Item>();
	ArrayList<Item> allCoupons = new ArrayList<Item>();
	ArrayList<User> userList = new ArrayList<User>();
	ArrayList<Category> categoryList = new ArrayList<Category>();
	
	User testUser;
	
	/**
	 * Constructor with user id pass by log in gui
	 * @param _userID
	 * @throws IOException
	 * @throws JSONException
	 */
	public InteracterGUI(int _userID) throws IOException, JSONException {
		UserInforGenertor uig = new UserInforGenertor();
		userList = uig.loadUser(new File(fUser));
		allCoupons = uig.loadCoupon(new File(fCoupon));
		categoryList = uig.loadCategory(new File(fCategory));
		oldCoupons = uig.loadCoupon(new File(fOldCoupon));
		
		initData();
		this.userID = _userID;
		testUser = getUser(userID);
		System.out.println("lon: " + testUser.getLon());
		System.out.println("lat: " + testUser.getLat());
	}
	
	/**
	 * Read data of engine from files
	 * @throws FileNotFoundException
	 */
	public void initData() throws FileNotFoundException {
			
		for(User u : userList) {
			Scanner inUserCoupon = new Scanner(new File(fUserCoupon));
			Scanner inUserFriend = new Scanner(new File(fUserFriend));
			Scanner inUserCate = new Scanner(new File(fUserCate));
			
			int uID = u.getId();
			
			//Get choose coupon
			while(inUserCoupon.hasNext()) {
				//1|7762459|2012-05-02
				String line = inUserCoupon.nextLine();
				if(line.isEmpty() == false) {
					String[] lines = line.split("\\|");
					
					if(Integer.parseInt(lines[0]) == uID) {
						for(Item i : allCoupons) {
							if(i.getDeal().get("id").compareTo(lines[1]) == 0) {
								u.getCouponChooseList().put(i, 1.0);	
							}
							
						}
					}
				}
			}
			
			//Get friend list
			while(inUserFriend.hasNext()) {
				//1|502;101;846;225;184;842;23;404;637;776;
				String line= inUserFriend.nextLine();
				if(line.isEmpty() == false) {
					String[] lines = line.split("\\|");
					if(lines.length >= 2) {
						if(Integer.parseInt(lines[0]) == uID) {
							String[] friends = lines[1].split(";");
							for(String f : friends) {
								for(User uf : userList) {
									if(uf.getId() == Integer.parseInt(f)) {
										u.getFriendList().put(uf, 1.0);
									}
								}
								
							}
							
						}
					}
				}
			}
			
			//Get category with interest
			//1|2|0.07
			
			while(inUserCate.hasNext()) {
				String line = inUserCate.nextLine();
				if(line.isEmpty() == false) {
					String[] lines = line.split("\\|");
					if(Integer.parseInt(lines[0]) == uID) {
						for(Category c : categoryList) {
							if(c.getId() == Integer.parseInt(lines[1])) {
								u.getCategoryChooseList().put(c, Double.parseDouble(lines[2]));
							}
						}
					}
				}
			}
			inUserCate.close();
			inUserFriend.close();
			inUserCoupon.close();
		}
	}
	
	//Step 1: get user
	public User getUser(int userID) throws FileNotFoundException {
		for(User u : userList) {
			if(u.getId() == userID) {
				testUser = u;
			}
		}
		return testUser;
	}
	
	
	//Step 2: read new coupon
	public void readNewCoupon() {
		UserInforGenertor uig = new UserInforGenertor();
		newCoupons = uig.loadCoupon(new File(fNewCoupon));
	}
	
	/**
	 * Check user history
	 * @return true if user history is not empty
	 */
	public boolean checkUserHistory() {
		return testUser.getCategoryChooseList().size() > 0;
	}
	
	//Step 6 and 7
	public  ArrayList<String[]> returnCoupons() {
		//String[]={“1”, “titlecoupon”, “coupon_expiration-2012-08-22”, “receivingDate-2012-08-19” }
		ArrayList<String[]> allNewCoupon = new ArrayList<String[]>();
		for(Item i : newCoupons) {
			String [] coupon = {i.getDeal().get("id"), i.getDeal().get("title"), i.deal.get("coupon_expiration"), i.deal.get("receivingDate")};
			allNewCoupon.add(coupon);
		}
		
		return allNewCoupon;
	}
	
	// Step 8 and 9
	// Recommend coupon
	public ArrayList<String[]> couponRecommend(){
		ArrayList<String[]> couponsForRecommend = new ArrayList<String[]>();
		
		HashMap<Category, Double> scores = new HashMap<Category, Double>();
		HashMap<Category, ArrayList<Item>> newCouponInCategory = new HashMap<Category, ArrayList<Item>>();
		
		
		for(Category c : categoryList) {
			
			double interestCategoryOfFriend = 0.0;	
			for(User u : testUser.getFriendList().keySet()) {
				int total = 0;
				
				for(Item i : u.getCouponChooseList().keySet()) {
					if(Integer.parseInt(i.deal.get("categoryID")) == c.getId()){
						total++;
					}
				}
				
				//System.out.println("---------------X: " + u.getCategoryChooseList().get(c) +"*" + total);
				interestCategoryOfFriend += u.getCategoryChooseList().get(c) * total;
			}	
			
			scores.put(c, interestCategoryOfFriend);
			
			
			//Map newscoupon into category list
			//MUST DEBUG
			ArrayList<Item> tmp = new ArrayList<Item>();
			System.out.println("size of newcoupons: " + newCoupons.size());
			for(Item i : newCoupons) {
				if(Integer.parseInt(i.deal.get("categoryID")) == c.getId()) {
					tmp.add(i);
				}
			}
			newCouponInCategory.put(c, tmp);
			
		}
		
		//Sort by score
		//HashMap<Category, Double> scoresSorted = Sorter.sortHashMap(scores);
		HashMap<Category, Double> scoresSorted = scores;

		//String[]={“2”, “titlecoupon”, “coupon_expiration-2012-08-22”, “receivingDate-2012-08-19” }
		for(Category c : scoresSorted.keySet()) {
			if(scoresSorted.get(c) >= RECOMMEND_THRESHOLD) {
				System.out.println("category " + c + ":" + scoresSorted.get(c));
				//System.out.println("Mustcheckis, size of list coupons: " + newCouponInCategory.get(c).size());
				for(Item i : newCouponInCategory.get(c)) {
					double lonBias = Math.abs(Double.parseDouble(i.getMerchant().get("longitude"))-testUser.getLon());
					double latBias = Math.abs(Double.parseDouble(i.getMerchant().get("latitude")) - testUser.getLat()); 
					
					if((lonBias <= LON_BIAS)
						&&(latBias <= LAT_BIAS)) {
						
						System.out.println("Add it with lon = " + lonBias + "and lat = " + latBias);
						String [] toAdd = {i.getDeal().get("id"), 
								i.getDeal().get("title"),
								i.deal.get("coupon_expiration"), 
								i.deal.get("receivingDate"),
								i.getMerchant().get("latitude"),
								i.getMerchant().get("longitude")};
						couponsForRecommend.add(toAdd);
					}
				}
				
			}
		}
		
		//PRINT
		System.out.println("momo");
		for(String[] x : couponsForRecommend) {
			for(String s:x) {
				System.out.print(s + "|");
			}
			System.out.println();
		}
		System.out.println("end momo");
		return couponsForRecommend;
	}
	
	
	//STep 13
	/**
	 * Get a list of integers, are id of coupons when user chooses coupon
	 * and are id of users when choose friends
	 */
	
	// Input: ArrayList<String> couponSelected; in form : {“1”, “2”, “3”}
	// Output ArrayList<Integer>
	public  ArrayList<Item> getUserInput(ArrayList<String> couponSelected) {
		ArrayList<Item> iList = new ArrayList<Item>();
		for(String s : couponSelected) {
			for(Item i : allCoupons) {
				if(i.getDeal().get("id").compareTo(s) == 0) {
					iList.add(i);
				}
			}
		}
		
		return iList;
	}
	
	// Step 14
	public  void updateHistoricalData(ArrayList<Item> justChoose) {
		for(Item i : justChoose) {
			testUser.getCouponChooseList().put(i, 1.0);
			
		}
	}
	

	
	//Step 18
	//ArrayList<String> friendSelected; in form : {“1”, “2”, “3”}
	public ArrayList<User> getUserInputFriend(ArrayList<String> friendSelected){
		ArrayList<User> uList = new ArrayList<User>();
		for(String uID : friendSelected) {
			for(User u : userList) {
				if(u.getId() == Integer.parseInt(uID)) {
					uList.add(u);
				}
			}
		}
		
		return uList;
	}
	
	//Step 19
	public  void updateFriends(ArrayList<User> justChooseFriend) {
		for(User u : justChooseFriend) {
			testUser.getFriendList().put(u, 1.0);
		}
	}
	
	
	//Gen user for add
	//ArrayList<String[]> , với String[] = {"1", "23", "gender",  "occupation", "1110"}
	public ArrayList<String[]> friendToAdd(){
		ArrayList<String[]> friendAdding = new ArrayList<String[]>();
		
		int numEachAdd = 5;
		
		ArrayList<Integer> gented = new ArrayList<Integer>();
		ArrayList<Integer> beFriend = new ArrayList<Integer>();
		
		for(User u : testUser.getFriendList().keySet()) {
			gented.add(u.getId());
		}
		
		while(numEachAdd > 0) {
			int genNum = UserInforGenertor.genIntegerInRange(userList.size()-1);
			
			if((beFriend.contains(genNum) == false) && (gented.contains(genNum) == false)) {
				gented.add(genNum);
				for(User u : userList) {
					if(u.getId() == genNum) {
						String[] adding = {
											Integer.toString(u.getId()), 
											Integer.toString(u.getAge()),
											u.getGender(),
											u.getOccupation(),
											u.getZipCode()
											};
						
						friendAdding.add(adding);
					}
				}
				
				numEachAdd--;
			}
		}
		return friendAdding;
	}
	/**
	 * @param args
	 */
	public  void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
