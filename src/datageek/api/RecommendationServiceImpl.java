package datageek.api;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import datageek.gui.InteracterGUI;
import datageek.util.Date;

public class RecommendationServiceImpl implements RecommendationService {
	final String F_USER_COUPON = "data/upgrade/user-coupon.txt";
	
	@Override
	public void updateUserSelection(Long userId, List<Long> couponIds) {
		//update your file data/upgrade/user-coupon.txt
		//1|7592197|2012-11-25
		try {
			PrintWriter out = new PrintWriter(
									new BufferedWriter(
											new FileWriter(F_USER_COUPON, true)));
			    
			for(Long couponId : couponIds) {
				String sToUpdate = Long.toString(userId) 
						+"|" + Long.toString(couponId)
						+"|" + Date.getCurrentDate();
				out.println(sToUpdate);
			}
			
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public List<Recommendation> getRecommendations(Long userId, Double lat, Double lon) {
    	List<Recommendation> coupons = new ArrayList<Recommendation>();
    	InteracterGUI interacterGUI;
		try {
			interacterGUI = new InteracterGUI(userId.intValue());
			interacterGUI.readNewCoupon();
			ArrayList<String[]> recommend = interacterGUI.couponRecommend();
			
			for(String[] coupon : recommend) {
				Recommendation r = new Recommendation(Long.parseLong(coupon[0]));
				coupons.add(r);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//call the recommendation engine
    	return coupons;
    }
}
