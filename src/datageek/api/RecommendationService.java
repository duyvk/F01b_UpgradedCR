package datageek.api;

import java.util.List;

public interface RecommendationService {
	
	public abstract void updateUserSelection(Long userId, List<Long> couponIds);

	public abstract List<Recommendation> getRecommendations(Long userId, Double lat, Double lon);
}
