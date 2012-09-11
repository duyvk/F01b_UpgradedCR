package datageek.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import datageek.entity.Category;

public class Sorter {
	
	// Sort a hash map by value
	public static HashMap<Category, Double> sortHashMap(HashMap<Category, Double> input){
	    Map<Category, Double> tempMap = new HashMap<Category, Double>();
	    for (Category wsState : input.keySet()){
	        tempMap.put(wsState,input.get(wsState));
	    }

	    List<Category> mapKeys = new ArrayList<Category>(tempMap.keySet());
	    List<Double> mapValues = new ArrayList<Double>(tempMap.values());
	    
	    HashMap<Category, Double> sortedMap = new LinkedHashMap<Category, Double>();
	    TreeSet<Double> sortedSet = new TreeSet<Double>(mapValues);
	    
	    Object[] sortedArray = sortedSet.toArray();
	    int size = sortedArray.length;
	    for (int i=0; i<size; i++){
	        sortedMap.put(mapKeys.get(mapValues.indexOf(sortedArray[i])), 
	                      (Double)sortedArray[i]);
	    }
	    return sortedMap;
	}
}
