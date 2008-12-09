package hks.itprojects.healthcollector.utils;
import java.util.*;

public class Utility {
	
	// Thanks to Amr K. Kamel 
	// http://amrkamel.wordpress.com/2008/01/18/replacing-strings-in-j2me/
	// Accessed : 18 november 08
	
	public static String replaceAll(String text, String searchString, String replacementString){
		StringBuffer sBuffer = new StringBuffer();
		int pos = 0;
		while((pos = text.indexOf(searchString)) != -1){
			// 1. Insert replacement
		sBuffer.append(text.substring(0, pos) + replacementString);
			// 2. Advance search string after replacement/tail
		text = text.substring(pos + searchString.length());
		}
		// 3. Insert tail
		sBuffer.append(text);
		return sBuffer.toString();
		}
        
    public static String addZeroIfNeccessary(int value)
   {
      if (value < 10)
          return "0"+String.valueOf(value);
      else
          return String.valueOf(value);
   }
    
    public static String addTwoSpacesIfNeccessary(int value)
   {
        
      if (value < 10)
          return "  "+String.valueOf(value);
      else if (value < 100)
          return " "+String.valueOf(value);
      return String.valueOf(value);
   }
    
    public static  String formatDate(Date date) {
		java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        
        cal.setTime(date); // Set time in UTC-timezone
        cal.setTimeZone(TimeZone.getDefault());
        
        int year = cal.get(java.util.Calendar.YEAR);
        int month = cal.get(java.util.Calendar.MONTH)+1;
        int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
        int hour = cal.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = cal.get(java.util.Calendar.MINUTE);
        
        String df = String.valueOf(year)+"-"+
                Utility.addZeroIfNeccessary(month)+"-"+
                Utility.addZeroIfNeccessary(day)+" "+
                Utility.addZeroIfNeccessary(hour)+":"+
                Utility.addZeroIfNeccessary(minute);
		return df;
	}
    
    public static String getMilliSecondDate()
    {
    	Calendar cal = Calendar.getInstance();
		Date nowDate = cal.getTime();
		long milliDate = nowDate.getTime();
		
		return String.valueOf(milliDate);
		
    }

}
