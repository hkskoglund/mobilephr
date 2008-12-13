package hks.itprojects.healthcollector.ListModels;

import hks.itprojects.healthcollector.REST.IRESTCLOUDDB;
import hks.itprojects.healthcollector.REST.MicrosoftSDS;
import hks.itprojects.healthcollector.UI.HealthCollectorMIDlet;
import hks.itprojects.healthcollector.authorization.LoginUserSDS;
import hks.itprojects.healthcollector.network.HttpResponse;

import java.io.IOException;

import com.sun.lwuit.list.DefaultListModel;

public class ThumbnailCloudModel extends DefaultListModel
	{
		LoginUserSDS user = HealthCollectorMIDlet.getLoginUser();
		private IRESTCLOUDDB cloudDB = new MicrosoftSDS(HealthCollectorMIDlet.getIMEI(),
				HealthCollectorMIDlet.getAuthorityID(),
				user.getUserName(),
				user.getPassword());
		
		public ThumbnailCloudModel() {
			
		}
		// Override - load thumbnail blob from cloud
		public Object getItemAt(int index) {
			Thumbnail t = (Thumbnail) super.getItemAt(index);
			try
				{
					byte [] data = t.getThumbnailData();
					
					if (data == null) // Don't load if already loaded....
						{
						  HttpResponse hResponse = 	cloudDB.readWoundThumbnail(t.getWoundId(), t);
						  t.setThumbnailData(hResponse.getByteArrayOutputStream().toByteArray());
						} 
			} catch (IOException e)
				{
					// Discard IO-errors
				}
			
		    return (Object)t;
		}

	}
