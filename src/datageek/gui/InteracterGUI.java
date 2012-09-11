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
import datageek.upgrade.datapreparing.UserInforGenertor_v2;

/**
 * The interface implementation for interact with GUI
 * @author hugo
 *
 */
public class InteracterGUI {
	
	final double RECOMMEND_THRESHOLD = 0.2;
	
	final  String fNewCoupon = "data/upgrade/new-coupons.txt";
	final  String fOldCoupon = "data/upgrade/old-coupons.txt";
	
	final  String fUser = "data/upgrade/user";
	final  String fCoupon = "data/upgrade/deals.txt";
	
	final  String fUserCoupon = "data/upgrade/user-coupon.txt";
	final  String fUserFriend = "data/upgrade/user-friend.txt";
	final String fUserCate = "data/upgrade/user-cate.txt";
	final String fCategory = "data/upgrade/categories.txt";
	final String fCouponCategory = "data/upgrade/coupon-category.txt";
	
	//final  int userID = 1; //user for testing
	ArrayList<Item> newCoupons = new ArrayList<Item>();
	ArrayList<Item> oldCoupons = new ArrayList<Item>();
	ArrayList<Item> allCoupons = new ArrayList<Item>();
	ArrayList<User> userList = new ArrayList<User>();
	ArrayList<Category> categoryList = new ArrayList<Category>();
	
	User testUser;
	
	public InteracterGUI() throws IOException, JSONException {
		UserInforGenertor_v2 uig = new UserInforGenertor_v2();
		userList = uig.loadUser(new File(fUser));
		allCoupons = uig.loadCoupon(new File(fCoupon));
		categoryList = uig.loadCategory(new File(fCategory));
		oldCoupons = uig.loadCoupon(new File(fOldCoupon));
		
		initData();
	}
	
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
								testUser.getCouponChooseList().put(i, 1.0);	
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
		UserInforGenertor_v2 uig = new UserInforGenertor_v2();
		newCoupons = uig.loadCoupon(new File(fNewCoupon));
	}
	
	// Return true if user has history
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
					for(Item j : c.getCouponList()) {
						if(i.getDeal().get("id").compareTo(j.getDeal().get("id"))==0) {
							total++;
						}
					}
				}
				
				interestCategoryOfFriend += u.getCategoryChooseList().get(c) * total;
			}	
			
			scores.put(c, interestCategoryOfFriend);
			
			//Map newscoupon into category list
			ArrayList<Item> tmp = new ArrayList<Item>();
			for(Item i : newCoupons) {
				for(Item j:c.getCouponList()) {
					if(i.getDeal().get("id").compareTo(j.getDeal().get("id"))==0) {
						tmp.add(i);
					}
				}
				
			}
			newCouponInCategory.put(c, tmp);
		}
		
		//Sort here
		
		//Return
		
		//String[]={“2”, “titlecoupon”, “coupon_expiration-2012-08-22”, “receivingDate-2012-08-19” }
		for(Category c : scores.keySet()) {
			if(scores.get(c) >= RECOMMEND_THRESHOLD) {
				for(Item i : newCouponInCategory.get(c)) {
					String [] toAdd = {i.getDeal().get("id"), 
										i.getDeal().get("title"),
										i.deal.get("coupon_expiration"), 
										i.deal.get("receivingDate")};
					couponsForRecommend.add(toAdd);
				}
				
			}
		}
		
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
			int genNum = UserInforGenertor_v2.genIntegerInRange(userList.size()-1);
			
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
