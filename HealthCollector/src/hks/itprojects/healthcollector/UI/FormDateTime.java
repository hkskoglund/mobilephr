package hks.itprojects.healthcollector.UI;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.geom.*;

import java.util.*;

/**
 *
 * @author henning
 */
public class FormDateTime extends Form implements SelectionListener  {

    public java.util.Date getDate() {
    	java.util.Calendar calDate = java.util.Calendar.getInstance();
        calDate.setTime(calendarLWUIT.getDate()); // 1. Set calendar widget date
        calDate.set(java.util.Calendar.HOUR_OF_DAY, cal.get(java.util.Calendar.HOUR_OF_DAY)); // 2. Set time
        calDate.set(java.util.Calendar.MINUTE, cal.get(java.util.Calendar.MINUTE));
    	return calDate.getTime();
    }
    
    private com.sun.lwuit.Calendar calendarLWUIT;
    private java.util.Calendar cal;

    private ComboBox cbHours = null;
    private ComboBox cbMinutes = null;
    
    public Command getCmdDateFormOK() {
        return cmdDateFormOK;
    }
    
    private Command cmdDateFormOK;
    
    public FormDateTime(String title, ActionListener actionListener, Date date) {
        super(title);
        calendarLWUIT = new com.sun.lwuit.Calendar(date.getTime());
       
        cal = java.util.Calendar.getInstance(); 
       cal.setTime(date);
       
       cbHours = new ComboBox();
       Dimension dimHours = cbHours.getPreferredSize();
       dimHours.setHeight(30);
       dimHours.setWidth(50);
       cbHours.setPreferredSize(dimHours);
       cbHours.setFixedSelection(List.FIXED_LEAD);
        for (int hour=0;hour<24;hour++)
         cbHours.addItem((Object)String.valueOf(hour));
        
       cbMinutes = new ComboBox();
       Dimension dimMinutes = cbHours.getPreferredSize();
       dimMinutes.setHeight(30);
       dimMinutes.setWidth(50);
       cbMinutes.setPreferredSize(dimMinutes);
      
       cbMinutes.setFixedSelection(List.FIXED_LEAD);
       for (int minute=0;minute<60;minute++)
    	 cbMinutes.addItem((Object)String.valueOf(minute));
       
       cbHours.setSelectedIndex(cal.get(java.util.Calendar.HOUR_OF_DAY));  
       cbMinutes.setSelectedIndex(cal.get(java.util.Calendar.MINUTE)); 
       cbHours.addSelectionListener(this);
       cbMinutes.addSelectionListener(new SelectionListener() {
    		  
    	   public void selectionChanged(int oldSelected, int newSelected) {
    		
    			if (cbMinutes == null)  // Guard 1
    				return;
    			
    			Object selItem =  cbMinutes.getSelectedItem();
    			if (selItem == null ||  !(selItem instanceof String)) // Guard 2
    				return;
    			
    			// Fetch blood pressure from selected item
    			String minuteString = (String) selItem;
    	        int minute = Integer.parseInt(minuteString);
    	         cal.set(java.util.Calendar.MINUTE, minute);		
    	   }});
       
       
       addComponent(calendarLWUIT);
       
       Container contTime = new Container();
       contTime.addComponent(new Label("Tid"));
       contTime.addComponent(cbHours);
       contTime.addComponent(cbMinutes);
       
       addComponent(contTime);
       
        // Commands
        cmdDateFormOK = new Command("OK");
       
        addCommand(cmdDateFormOK);
       setCommandListener(actionListener);
    }
    
    public void selectionChanged(int oldSelected, int newSelected) {
		if (cbHours == null)  // Guard 1
			return;
		
		Object selItem =  cbHours.getSelectedItem();
		if (selItem == null ||  !(selItem instanceof String)) // Guard 2
			return;
		
		// Fetch blood pressure from selected item
		String hourString = (String) selItem;
        int hour = Integer.parseInt(hourString);
         cal.set(java.util.Calendar.HOUR_OF_DAY, hour);		
}	   


    
}
