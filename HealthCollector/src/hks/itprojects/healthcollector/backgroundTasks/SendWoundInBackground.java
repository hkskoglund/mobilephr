package hks.itprojects.healthcollector.backgroundTasks;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.HttpsConnection;

import com.sun.lwuit.Display;

import hks.itprojects.healthcollector.ListModels.Thumbnail;
import hks.itprojects.healthcollector.PHR.Wound;
import hks.itprojects.healthcollector.REST.IRESTCLOUDDB;
import hks.itprojects.healthcollector.REST.MicrosoftSDS;
import hks.itprojects.healthcollector.UI.HealthCollectorMIDlet;
import hks.itprojects.healthcollector.network.HttpResponse;

public class SendWoundInBackground implements Runnable {

	private Wound wound  = null;
	private IRESTCLOUDDB cloudDB = null;
    private boolean sendOnlyThumbnails = false;
    
		public void setWound(Wound wound) {
		this.wound = wound;
	}
	
	public SendWoundInBackground(Wound wound, boolean sendOnlyThumbnails) {
		this.wound = wound;
		this.sendOnlyThumbnails = sendOnlyThumbnails;
		cloudDB = new MicrosoftSDS(HealthCollectorMIDlet.getIMEI());
		(new Thread(this)).start();  // Auto-start threading
	    
	}
	
	public void run() {
		
		if (wound == null)
			return;
		
		HttpResponse hResponse = null;
		if (!sendOnlyThumbnails)
	     hResponse = sendWound();
		
		
		if (wound.getThumbnails().size()>0)
	      sendWoundThumbnails();
		

		if (!sendOnlyThumbnails) {
			if (hResponse.getCode() == HttpsConnection.HTTP_CREATED)
				 Display.getInstance().callSerially(new Runnable()
		  	     {

					public void run() {
				         HealthCollectorMIDlet.entitySaved("Såret er lagret!");
									
					}
						  	   
		  	     });

		       else {
		    	 final HttpResponse hResp = hResponse;    
		        showErrorMessage("Feil ved lagring; mottat melding -  "+hResp.getMessage()+" (kode "+String.valueOf(hResp.getCode())+").");
		       }
	
		}
		 	  
		
	}
	
	private void showErrorMessage(String msg)
		{
			final String message = msg;
			
			Display.getInstance().callSerially(
					new Runnable()
						{

							public void run()
								{
									HealthCollectorMIDlet.showErrorMessage("FEIL",message);

								}

						});
		}
	
	private void sendWoundThumbnails() 
			{

				Vector vThumbnails = wound.getThumbnails();
				HttpResponse hResponse = null;

				// Check that number of thumbs greater than 0
				
				final int numberOfThumbs = vThumbnails.size();
				if (numberOfThumbs == 0)
					return;
				
				// Create wound container to hold thumbnails and later images
				
				String woundContainer = wound.getId();
				
				try
				{
					if (!cloudDB.woundContainerExist(woundContainer))
						{
							HttpResponse hRespContainer = cloudDB.createWoundContainer(woundContainer);
						}
				} catch (IOException ioe)
					{
						showErrorMessage("Feil ved aksess til sår katalogen;"+woundContainer);
						return;
					}
			
				// Store thumbnails

				for (int i = 0; i < numberOfThumbs; i++)
					{
						Thumbnail thumbnail = (Thumbnail) vThumbnails.elementAt(i);

						try
							{
								hResponse = cloudDB.createWoundThumbnail(woundContainer,thumbnail);

							} catch (IOException e)
							{
								final IOException ioe = e;

								showErrorMessage("Feil ved oppretting av thumbnail for sår; " + ioe.getMessage());
							
							}

						if (hResponse.getCode() == HttpsConnection.HTTP_CREATED) {
						
							
							
						
							Display.getInstance().callSerially(new Runnable()
								{

									public void run()
										{
//											HealthCollectorMIDlet
//													.entitySaved("Thumbnails er lagret!");
//
										}

								});

							
							// Create reference (so that we know what thumbnails are in the wound container)
							// BLOB-entities does not have metadata in SDS
							
							HttpResponse hRespReference = null;
							
							try
								{
									hRespReference = cloudDB.createWoundThumbnailReference(wound.getId(), thumbnail);
								} catch (IOException e)
								{
									showErrorMessage("Feil ved oppretting av referanse fra thumbnail til sår; "+e.getMessage());
								}
								
								if (hRespReference.getCode() != HttpsConnection.HTTP_CREATED)
									showErrorMessage("Klarte ikke å opprette referanse fra thumbnail til sår; "+hRespReference.getMessage());
						}

						else
							{
								final HttpResponse hResp = hResponse;

								showErrorMessage("Feil ved lagring; mottat melding -  "
																				+ hResp.getMessage()
																				+ " (kode "
																				+ String.valueOf(hResp.getCode())
																				+ ").");

							}
					}

			}
	
	private HttpResponse sendWound()
		{
			// Phase 1 - Send wound
			HttpResponse hResponse = null;
			
			try {
				hResponse =  cloudDB.createWound(wound);
				
			} catch (IOException e) {
				final IOException ioe = e;
				
				   Display.getInstance().callSerially(new Runnable()
			  	     {

						public void run() {
							HealthCollectorMIDlet.showErrorMessage("FEIL", "Feil ved oppretting av sår registrering; "+ioe.getMessage());
										
						}
							  	   
			  	     });
				   
				return null;
			}

			return hResponse;
					       
			
		}

}
