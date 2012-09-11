package datageek.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Date {
	public static String getCurrentDate() {
		String sDate;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar date = Calendar.getInstance(); // the current date and time
		
		sDate = df.format(date.getTime());
		return sDate;
	}
}
