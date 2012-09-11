package datageek.api;

public class Recommendation {

	private long couponId;
    private double UserRelevance;
    private double FriendsRelevance;
    
    public Recommendation(long _couponID) {
    	couponId = _couponID;
    }
    
    // add setters and getters
	public long getCouponId() {
		return couponId;
	}
	public void setCouponId(long couponId) {
		this.couponId = couponId;
	}
	public double getUserRelevance() {
		return UserRelevance;
	}
	public void setUserRelevance(double userRelevance) {
		UserRelevance = userRelevance;
	}
	public double getFriendsRelevance() {
		return FriendsRelevance;
	}
	public void setFriendsRelevance(double friendsRelevance) {
		FriendsRelevance = friendsRelevance;
	}
}
