package hks.itprojects.healthcollector.ListRendering;

import hks.itprojects.healthcollector.ListModels.Thumbnail;

import java.io.IOException;
import java.util.*;
import com.sun.lwuit.*;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.*;


public class ImageRenderer extends Container implements ListCellRenderer {

	 private Label lblFilename = new Label("");
	 
	public ImageRenderer()
	{
		
		//this.parentMIDlet = parentMIDlet;
		this.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
		lblFilename.setAlignment(LEFT);
		addComponent(lblFilename);
		
	}
	public Component getListCellRendererComponent(List list, Object value,
			int index, boolean isSelected) {
			
		Thumbnail t = (Thumbnail) value;
		
			if (isSelected) {
		    	   lblFilename.setFocus(true);
		    	   lblFilename.getStyle().setBgTransparency(200);
		       	 
		       }
		       else
		       {
		    	   lblFilename.setFocus(false);
		    	   lblFilename.getStyle().setBgTransparency(0);
		       }
		
			try {
				//lblFilename.setText(t.getFileName());
				lblFilename.setTextPosition(Label.BOTTOM);
			//	lblFilename.setText(String.valueOf(Runtime.getRuntime().freeMemory()));
				Date d = new Date();
				d.setTime(t.getLastModified());
				
				java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"));
				cal.setTime(d);
				lblFilename.setText(
				
						String.valueOf(cal.get(java.util.Calendar.YEAR))+"-"+
						String.valueOf(cal.get(java.util.Calendar.MONTH)+1)+"-"+
						String.valueOf(cal.get(java.util.Calendar.DAY_OF_MONTH))+" "+
						String.valueOf(cal.get(java.util.Calendar.HOUR_OF_DAY))+":"+
						String.valueOf(cal.get(java.util.Calendar.MINUTE))
				);
				lblFilename.setIcon(t.getThumbnailImage()); // Gets thumbnail from filesystem
			} catch (IOException e) {
				try {
					lblFilename.setIcon(Image.createImage("/Error.png")); // Local resource
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				}
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
            		
		return this;
	}

	public Component getListFocusComponent(List list) {
		// TODO Auto-generated method stub
		return null;
	}

}
