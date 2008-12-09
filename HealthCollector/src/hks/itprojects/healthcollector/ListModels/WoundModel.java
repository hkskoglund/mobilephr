package hks.itprojects.healthcollector.ListModels;

import hks.itprojects.healthcollector.PHR.Wound;
import com.sun.lwuit.list.DefaultListModel;

public class WoundModel extends DefaultListModel
	{
	//	private IRESTCLOUDDB cloudDB = new MicrosoftSDS(HealthCollectorMIDlet.getIMEI());
		
		public WoundModel() {
			
		}
		// Override - find out if this wound has any images associated
		public Object getItemAt(int index) {
			Wound w = (Wound) super.getItemAt(index);
//			try
//				{
//					if (!w.hasWoundContainer())
//						  w.setHasWoundContainer(cloudDB.woundContainerExist(w.getId()));
//			} catch (IOException e)
//				{
//					// Discard IO-errors
//				}
//			
		    return (Object)w;
		}

	}

