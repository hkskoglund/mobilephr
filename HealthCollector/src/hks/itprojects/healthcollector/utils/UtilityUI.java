package hks.itprojects.healthcollector.utils;

import com.sun.lwuit.*;

public class UtilityUI {
	// Sets bold font on label headings
    public static void setMediumBold(Component comp)
    {
    	
        comp.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, 
        		Font.STYLE_BOLD, Font.SIZE_MEDIUM));
    }
    
    public static void setSmallBold(Component comp)
    {
        comp.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, 
        		Font.STYLE_BOLD, Font.SIZE_SMALL));
    }
    
    public static void setSmall(Component comp)
    	{
    	    comp.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, 
            		Font.STYLE_PLAIN, Font.SIZE_SMALL));
        		
    	}

    public static void setButtonDate(Button btn, java.util.Date date)
        {
            btn.setText(date.toString());
        }
    
    
}
