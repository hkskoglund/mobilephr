package hks.itprojects.healthcollector.backgroundTasks;

import java.io.IOException;

import javax.microedition.io.HttpsConnection;

import com.sun.lwuit.Display;

import hks.itprojects.healthcollector.PHR.BloodPressure;
import hks.itprojects.healthcollector.UI.HealthCollectorMIDlet;
import hks.itprojects.healthcollector.authorization.LoginUser;
import hks.itprojects.healthcollector.network.HttpResponse;

import hks.itprojects.healthcollector.REST.*;

// Useful information about LWUIT-threading
// http://forums.java.net/jive/thread.jspa?threadID=42722
// Accessed : 26 november 2008

public class SendBloodPressureInBackground implements Runnable {

	private BloodPressure bp = null;
	private HealthCollectorMIDlet parentMIDlet = null;
	
	
	public SendBloodPressureInBackground(HealthCollectorMIDlet parentMIDlet, BloodPressure bp) {
		this.parentMIDlet = parentMIDlet;
		this.bp = bp;
		(new Thread(this)).start();
	}
	
	public void run() {
		// Currently only support for Microsoft SQL Data Services (SDS)
		LoginUser user = HealthCollectorMIDlet.getLoginUser();
		IRESTCLOUDDB cloudDB = new MicrosoftSDS(HealthCollectorMIDlet.getIMEI(),
		  HealthCollectorMIDlet.getAuthorityID(),
		  user.getUserName(),
		  user.getPassword()
		);
	    
		HttpResponse hResponse;
	       
	       try {
	        hResponse =  cloudDB.createBloodPressure( bp);
	       } catch (IOException e)
	       
	       {
	    	   final IOException ioe = e;

	    	   // Run update on event dispatcher thread EDT
	    	   Display.getInstance().callSerially(new Runnable()
	  	     {

	  			public void run() {
	  		HealthCollectorMIDlet.showErrorMessage("FEIL","Feil ved nettverksforbindelsen; "+ioe.getMessage());
	  				    	
	  			}
	  	    		
	  	     });

	    	     return;
	       }
	       
	       if (hResponse.getCode() == HttpsConnection.HTTP_CREATED)
	    	   
	    	   Display.getInstance().callSerially(new Runnable()
		  	     {

		  			public void run() {
		  		         HealthCollectorMIDlet.entitySaved("Blodtrykket er lagret!");
		  				    	
		  			}
		  	    		
		  	     });

	    	   
	       else {

	    	   final HttpResponse hResp = hResponse;
	    	   
	          Display.getInstance().callSerially(new Runnable()
		  	     {

		  			public void run() {
		  		         HealthCollectorMIDlet.showErrorMessage("FEIL","Feil ved lagring; mottat melding -  "+hResp.getMessage()+" (kode "+String.valueOf(hResp.getCode())+").");
		  				    	
		  			}
		  	    		
		  	     });
	    	   
	       }
	     		
	}

}
