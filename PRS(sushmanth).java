import java.util.*;

public class RecommendationSystem {

    // A map to store user ratings. Each user has a map of item ratings.
    private static Map<String, Map<String, Double>> userRatings = new HashMap<>();
    
    // A method to add user ratings to the system
    public static void addRating(String user, String item, double rating) {
        userRatings.computeIfAbsent(user, k -> new HashMap<>()).put(item, rating);
    }
    
    // A method to calculate similarity between two users based on their ratings
    private static double calculateSimilarity(Map<String, Double> ratings1, Map<String, Double> ratings2) {
        Set<String> commonItems = new HashSet<>(ratings1.keySet());
        commonItems.retainAll(ratings2.keySet());
        
        if (commonItems.isEmpty()) return 0;
        
        double sum1 = 0, sum2 = 0, sumProduct = 0;
        for (String item : commonItems) {
            double rating1 = ratings1.get(item);
            double rating2 = ratings2.get(item);
            sum1 += rating1 * rating1;
            sum2 += rating2 * rating2;
            sumProduct += rating1 * rating2;
        }
        
        return sumProduct / (Math.sqrt(sum1) * Math.sqrt(sum2));
    }
    
    // A method to recommend items to a user
    public static Map<String, Double> recommendItems(String user) {
        Map<String, Double> recommendations = new HashMap<>();
        Map<String, Double> targetRatings = userRatings.get(user);
        
        if (targetRatings == null) return recommendations;
        
        for (String otherUser : userRatings.keySet()) {
            if (otherUser.equals(user)) continue;
            
            Map<String, Double> otherRatings = userRatings.get(otherUser);
            double similarity = calculateSimilarity(targetRatings, otherRatings);
            
            if (similarity <= 0) continue;
            
            for (Map.Entry<String, Double> entry : otherRatings.entrySet()) {
                String item = entry.getKey();
                double rating = entry.getValue();
                
                if (!targetRatings.containsKey(item)) {
                    recommendations.put(item, recommendations.getOrDefault(item, 0.0) + similarity * rating);
                }
            }
        }
        
        return sortByValue(recommendations);
    }
    
    // Helper method to sort recommendations by score
    private static Map<String, Double> sortByValue(Map<String, Double> unsortedMap) {
        List<Map.Entry<String, Double>> list = new ArrayList<>(unsortedMap.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        
        Map<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        
        return sortedMap;
    }
    
    public static void main(String[] args) {
        // Adding sample data
        addRating("Alice", "Item1", 5.0);
        addRating("Alice", "Item2", 3.0);
        addRating("Bob", "Item1", 4.0);
        addRating("Bob", "Item3", 2.0);
        addRating("Charlie", "Item2", 4.0);
        addRating("Charlie", "Item3", 5.0);
        
        // Get recommendations for korukoppula sushmanth
        Map<String, Double> recommendations = recommendItems("korukoppula sushmanth");
        
        System.out.println("Recommendations for korukoppula sushmanth:");
        for (Map.Entry<String, Double> entry : recommendations.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}