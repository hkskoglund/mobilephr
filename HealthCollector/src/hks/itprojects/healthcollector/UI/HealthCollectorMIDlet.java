package hks.itprojects.healthcollector.UI;

// Based on a simple netclient provided by Sun 2000-2001

//import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import java.io.*;


import com.sun.lwuit.*;
import com.sun.lwuit.util.*;
import com.sun.lwuit.plaf.*;

public class HealthCollectorMIDlet extends MIDlet
{

    // MIDlet forms
    public FormBloodPressure bloodPressureScr = null;
    public FormLogin loginScr = null;
    public FormMainMenu menuScr = null;
    public FormBPOverview overviewBPScr = null;
    
    private static String IMEI = null;
    private String platform = System.getProperty("microedition.platform");
    
    public static String getIMEI() {
		return IMEI;
	}

	public HealthCollectorMIDlet() {
            
      //  display = Display.getDisplay(this);
       
		Display.init(this);
        
        
	      // The IMEI comined with date is used to uniquely identify a bloodpressure
	     	// entity
	     	try {
	     		if (platform.startsWith("Nokia"))
	     			IMEI = System.getProperty("com.nokia.mid.imei");
	     		if (platform.startsWith("SonyEricsson"))
	             IMEI = System.getProperty("com.sonyericsson.imei");
	     		if (platform.startsWith("Motorola")) 
	     		  IMEI = System.getProperty("com.motorola.IMEI");
	         				
	     	} catch (Exception e)
	     	{
	     		showIMEInotFoundErrorAndStop();
	     	}
	         
	     	if (IMEI == null)
	     	  showIMEInotFoundErrorAndStop();
	     	
	         
	         try {
	        	 
	        	  Resources r = Resources.open("/businessTheme.res");
	        	  UIManager.getInstance().setThemeProps(r.getTheme("businessTheme"));
	        	 
	        	} catch (IOException ioe) {
	        	  // Do something here.
	        	}

	         
	        // Setup forms
	        
	        bloodPressureScr = new FormBloodPressure("Blodtrykk",this);
	        loginScr = new FormLogin("Innlogging",this);
	        menuScr = new FormMainMenu("Hoved Meny",this);
	       
	        loginScr.show();
	       

    }

     protected void startApp() {
         
             
    }

	private void showIMEInotFoundErrorAndStop() {
		showErrorMessage("FEIL", "Kan ikke nå tak i IMEI nummeret for platformen"+platform);
		this.destroyApp(false);
		this.notifyDestroyed();
	}

     protected void pauseApp() {}

     protected void destroyApp(boolean unconditional) {}
     
     
     
     public void stopApplication()
     {
         destroyApp(false);
         notifyDestroyed();
     }
     
    public static Image loadImage(String imageResource)
    {
        try {
          return Image.createImage(imageResource);
        } catch (IOException ioe) {
            showErrorMessage("FEIL","Klarte ikke å åpne ressursen "+imageResource+" feilmelding; "+ioe.getMessage());
            return null;
        }
    
    }
    
    public static void showErrorMessage(final String title, final String message)
    {
    	
         final Image imgError = loadImage("/Error.png");
        
         final Command[] cmds = new Command[1];
         cmds[0] = new Command("OK");
          
         if (Display.getInstance().isEdt())
            Dialog.show(title,message, cmds, Dialog.TYPE_ERROR,imgError,2000);
         else
        	 {
        		 Display.getInstance().callSerially(new Runnable() {

					public void run()
						{
		
						   Dialog.show(title,message, cmds, Dialog.TYPE_ERROR,imgError,2000);
						   
						}
        			 
        		 });}
        	 
    }
    
    public static void entitySaved(String msg) {
		Image imgSaved = loadImage("/Saved.png");
           Command[] cmds = new Command[1];
           cmds[0] = new Command("OK");
           
            Command cmdResult = Dialog.show("OK", msg, cmds, Dialog.TYPE_INFO,imgSaved,2000);
	}    
    
    
}

