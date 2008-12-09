package hks.itprojects.healthcollector.ListRendering;
import java.io.IOException;

import com.sun.lwuit.*;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.*;

import hks.itprojects.healthcollector.PHR.*;
import hks.itprojects.healthcollector.utils.*;

/**
 *
 * @author henning
 */
public class WoundRenderer extends Container implements ListCellRenderer {

//    private Label lblSystolic = new Label("");  
//    private Label lblDiastolic = new Label("");  
//    private Label lblLocation = new Label(""); 
   private Label lblName = new Label("");
//    private Label lblDate = new Label("");
    
   private Image imgContainer = null;
   
    public WoundRenderer() {
    	setLayout(new BoxLayout(BoxLayout.Y_AXIS));
    	UtilityUI.setMediumBold(lblName);
//    	UtilityUI.setSmall(lblLocation);
//    	UtilityUI.setSmall(lblDate);
    	
    	addComponent(lblName);
    	try
			{
				imgContainer = Image.createImage("/Container.png");
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
//    	addComponent(lblLocation);
//        addComponent(lblDate);
    }
    
    public Component getListCellRendererComponent(List list, 
            Object value, int index, boolean isSelected) {      
   
       Wound wound = (Wound) value;
       if (isSelected) {
    	   lblName.setFocus(true);
    	  // lblDate.setFocus(true);
    	  // lblLocation.setFocus(true);
    	  // lblDate.getStyle().setBgTransparency(100);
       	 
       }
       else
       {
    	  // lblDate.setFocus(false);
    	   lblName.setFocus(false);
    	  // lblLocation.setFocus(false);
    	  // lblDate.getStyle().setBgTransparency(0);
       }
       
       lblName.setText(wound.getName());
       lblName.setTextPosition(Label.RIGHT);
       if (wound.hasWoundContainer())
    	   {
    		   if (imgContainer != null)
				  lblName.setIcon(imgContainer);
//		      	   
    	   }
	//	 lblLocation.setText(wound.getLocation());
//       
//       if (wound.getDate() != null)
//       {
//        String df = Utility.formatDate(wound.getDate());
//        lblDate.setText(df);
//       } else
//           lblDate.setText("Ukjent dato/tid");
//        
        return this;
     }

	
        
    
    public Component getListFocusComponent(List list) 
    {
    	return null;
    }
    
}
