package datageek.upgrade.datapreparing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;


public class DataGenerator {
	static final String USER_RAW_FILENAME = "data/upgrade/user-raw"; //User before add lon/lat
	static final String USER_FILENAME = "data/upgrade/user"; //User before add lon/lat
	//1|24|M|technician|85711|lon|lat
	
	/**
	 * Generate lon, lat for each user from fUserRaw and return fUser file
	 * @param fUserRaw
	 * @param fUser
	 */
	public static void genUserLonLat(File fUserRaw, File fUser){
		try {
			PrintWriter out = new PrintWriter(fUser);
			
			Scanner in = new Scanner(fUserRaw);
			while(in.hasNext()) {
				String userLine = in.nextLine();
				String lon = Double.toString(genLon());
				String lat = Double.toString(genLat());
				userLine += "|" + lon + "|" + lat;
				out.println(userLine);
			}
			
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Generate random longtitude 
	 * @return longtitude by a double number
	 */
	private static double genLon() {
		double lon = -770000;
		int bias = UserInforGenertor.genIntegerInRange(650); 
		
		return (double)(lon-bias)/10000;
	}
	
	/**
	 * Generate random latitude
	 * @return latitude by a double number
	 */
	private static double genLat() {
		double lat = 388900;
		int bias = UserInforGenertor.genIntegerInRange(200);
	
		return (double)(lat+bias)/10000;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		genUserLonLat(new File(USER_RAW_FILENAME), new File(USER_FILENAME));
		/*System.out.println("lon: " + genLon());
		
		System.out.println("lat: " + genLat());*/
	}

}
