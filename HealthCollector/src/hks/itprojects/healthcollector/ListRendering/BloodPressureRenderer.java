

package hks.itprojects.healthcollector.ListRendering;

import com.sun.lwuit.*;
import com.sun.lwuit.list.*;

import hks.itprojects.healthcollector.PHR.*;
import hks.itprojects.healthcollector.utils.*;

/**
 *
 * @author henning
 */
public class BloodPressureRenderer extends Container implements ListCellRenderer {

//    private Label lblSystolic = new Label("");  
//    private Label lblDiastolic = new Label("");  
//    private Label lblHR = new Label(""); 
    private Label lblBP = new Label("");
    private Label lblDate = new Label("");
    
    public BloodPressureRenderer() {
        addComponent(lblDate);
        addComponent(lblBP);
    }
    
    public Component getListCellRendererComponent(List list, 
            Object value, int index, boolean isSelected) {      
   
       BloodPressure bp = (BloodPressure) value;
       if (isSelected) {
    	   lblDate.setFocus(true);
    	   lblBP.setFocus(true);
    	   lblDate.getStyle().setBgTransparency(100);
       	   lblBP.getStyle().setBgTransparency(100);
       	 
       }
       else
       {
    	   lblDate.setFocus(false);
    	   lblBP.setFocus(false);
    	   lblBP.getStyle().setBgTransparency(0);
    	   lblDate.getStyle().setBgTransparency(0);
       }
       
       if (bp.getDate() != null)
       {
        String df = Utility.formatDate(bp.getDate());
        lblDate.setText(df);
       } else
           lblDate.setText("Ukjent dato/tid");
        
       String bpString = null;
       
        if (bp.getSystolic() != -1)
            bpString = Utility.addTwoSpacesIfNeccessary(bp.getSystolic());
        else
            bpString = "---";
       
        bpString += "/";
        
        if (bp.getDiastolic() != -1)
            bpString += Utility.addTwoSpacesIfNeccessary(bp.getDiastolic());
        else
            bpString += "---";
       
        bpString+=" ";
        
       if (bp.getHeartRate() != -1)
            bpString += Utility.addTwoSpacesIfNeccessary(bp.getHeartRate());
        else
            bpString += "---";
        
        lblBP.setText(bpString);
        
        return this;
     }

	
        
    
    public Component getListFocusComponent(List list) 
    {
        return null;
    }
    
}
