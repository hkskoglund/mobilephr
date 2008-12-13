package hks.itprojects.healthcollector.UI;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParserException;

import hks.itprojects.healthcollector.ListModels.ThumbnailCloudModel;
import hks.itprojects.healthcollector.ListModels.WoundModel;
import hks.itprojects.healthcollector.ListRendering.ThumbnailRenderer;
import hks.itprojects.healthcollector.ListRendering.WoundRenderer;
import hks.itprojects.healthcollector.PHR.Wound;
import hks.itprojects.healthcollector.REST.IRESTCLOUDDB;
import hks.itprojects.healthcollector.REST.MicrosoftSDS;
import hks.itprojects.healthcollector.authorization.LoginUserSDS;
import hks.itprojects.healthcollector.backgroundTasks.SendWoundInBackground;
import hks.itprojects.healthcollector.utils.Utility;
import hks.itprojects.healthcollector.utils.UtilityUI;

import com.sun.lwuit.*;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.DataChangedListener;
import com.sun.lwuit.events.SelectionListener;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.layouts.GridLayout;
import com.sun.lwuit.list.DefaultListModel;
import com.sun.lwuit.list.ListModel;

public class FormWoundOverview extends Form implements ActionListener, SelectionListener {

	private HealthCollectorMIDlet parentMIDlet;
	
	private IRESTCLOUDDB cloudDB = null;
	
	private final int ListSizeInPixels = 100;
	
	private Thread tReadWounds;
	
	private Command 
		
		cmdMenu, 
		cmdBack, 
		cmdFullScreen, 
		cmdGetThumbnails, 
		cmdImportImages, 
		cmdSend;
	
	private Button btnGetThumbnails = null;
	
	private List 
	
    	lbWounds = null,
    	lbThumbnails = null, 
    	lbImported = null;
    
    private Container
    	
    	contImported = null,
    	contThumbnails = null;
    
    
	private Label 
	
		lblDate,
		lblDateValue,
		lblLocation,
		lblLocationValue;
	
	public FormWoundOverview(String title, HealthCollectorMIDlet parentMIDlet)
	{
		super(title);
		this.parentMIDlet = parentMIDlet;
	    this.setLayout(new BoxLayout(BoxLayout.Y_AXIS));   
	    
	    LoginUserSDS user = HealthCollectorMIDlet.getLoginUser();
		
	    cloudDB  =  new MicrosoftSDS(HealthCollectorMIDlet.getIMEI(),
				HealthCollectorMIDlet.getAuthorityID(),
				user.getUserName(),
				user.getPassword());
		
		setupMasterOverview();
		setupDetails();
		
		// Read wounds on separate thread
		
		tReadWounds = new Thread(new Runnable() {

			public void run()
				{
					queryWounds(); // Fetch data from net
				    		
				}});
	        
	     tReadWounds.setPriority(Thread.NORM_PRIORITY);
	     tReadWounds.start();
	      
	     Dimension d = setupThumbnails();
		  
		  setupImportedImages(d);
		    
	      show();
	}

	private void setupImportedImages(Dimension d)
		{
			lbImported = new List();
			  lbImported.setModel(new DefaultListModel());
			  lbImported.setOrientation(List.HORIZONTAL);
			  lbImported.setListCellRenderer(new ThumbnailRenderer());
			  lbImported.setIsScrollVisible(true);
			  lbImported.setFixedSelection(List.FIXED_LEAD);
			
			  lbImported.setPreferredSize(d);
			  
			  contImported = new Container();
			  contImported.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
			  Label lblImported = new Label("Importerte");
			  lblImported.setAlignment(Label.CENTER);
			  UtilityUI.setSmallBold(lblImported);
			  contImported.addComponent(lblImported);
			  contImported.addComponent(lbImported);
			  contImported.setVisible(false);
			  
			  addComponent(contImported);
		}

	private Dimension setupThumbnails()
		{
			contThumbnails = new Container();
			 contThumbnails.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
			 
			 Label lblThumbnails = new Label("Bilder");
			 lblThumbnails.setAlignment(Label.CENTER);
			 UtilityUI.setSmallBold(lblThumbnails);
			 contThumbnails.addComponent(lblThumbnails);
			 
			 lbThumbnails = new List();
			 lbThumbnails.setModel(new ThumbnailCloudModel());

			 lbThumbnails.setOrientation(List.HORIZONTAL);
			 lbThumbnails.setListCellRenderer(new ThumbnailRenderer());
			 lbThumbnails.setIsScrollVisible(true);
			 lbThumbnails.setFixedSelection(List.FIXED_LEAD);

			  Dimension d = new Dimension();
			  Dimension dForm = this.getPreferredSize();
			  d.setHeight(55);
			  d.setWidth(dForm.getWidth());
			  lbThumbnails.setPreferredSize(d);
			    
			  contThumbnails.addComponent(lbThumbnails);
			  contThumbnails.setVisible(false);
			  
			  addComponent(contThumbnails);
			return d;
		} 
	
	public void setImportedListModel(ListModel model)
		{
			lbImported.setModel(model);
			if (lbImported.getModel().getSize() > 0)
				{
				  contImported.setVisible(true);
				//  lbImported.setVisible(true);
				}
		}
	
	/**
     * Sets up the master list of blood pressures
     */
    private void setupMasterOverview()
    {
   	 
        lbWounds = new List();
      //  BloodPressureModel bpModel = new BloodPressureModel();
        WoundModel woundModel = new WoundModel();
        lbWounds.setModel(woundModel);
        //woundModel.addSelectionListener(this);
        lbWounds.setListCellRenderer(new WoundRenderer());
        lbWounds.setFixedSelection(List.FIXED_LEAD);
        lbWounds.addSelectionListener(this);
        
        // Handle the case when the list is first updated to also synchronize details information without
        // user intervention, fake list selection
        
        lbWounds.getModel().addDataChangedListener(new DataChangedListener() {

			public void dataChanged(int type, int index)
				{
					
					if (index == 1 && type == DataChangedListener.ADDED)
						selectionChanged(-1,0);
					
				}});
        
//     Activating this inteferes with navigation in form...disabled
//     this.addGameKeyListener(Display.GAME_FIRE, new ActionListener() {
//
//			public void actionPerformed(ActionEvent arg0)
//				{
//					loadThumbnails();
//					
//				}});

        
        // Limit list screen size
        Dimension d = this.getPreferredSize();
        d.setHeight(ListSizeInPixels);
        lbWounds.setPreferredSize(d);
        
       
        this.addComponent(lbWounds);
        
        
        setupCommands();
      
    }

	private void setupCommands()
		{
			// Commands
			
			cmdMenu = new Command("HovedMeny");
			cmdBack = new Command("Tilbake");
			cmdFullScreen = new Command("Fullskjerm");
			cmdGetThumbnails = new Command("Hent");
			cmdImportImages = new Command("Importer");
			cmdSend = new Command("Send");
			
			addCommand(cmdMenu);
			addCommand(cmdBack);
			addCommand(cmdFullScreen);
			addCommand(cmdGetThumbnails);
			addCommand(cmdImportImages);
			addCommand(cmdSend);
			
			setBackCommand(cmdBack);
     
			setCommandListener(this);
		}
    
    private void setupDetails()
    	{
    		Container contDetails = new Container();
    		contDetails.setLayout(new GridLayout(2,2));
    	    lblDate = new Label("Dato");
    	    lblDate.getStyle().setBgTransparency(128);
    	    UtilityUI.setSmallBold(lblDate);
		    lblDateValue = new Label("");
		    lblDateValue.getStyle().setBgTransparency(128);
    	    
    		lblLocation = new Label("Lokalisering");
    		lblLocation.getStyle().setBgTransparency(128);
    	    
    		UtilityUI.setSmallBold(lblLocation);
		    lblLocationValue = new Label("");
		    lblLocationValue.getStyle().setBgTransparency(128);
    	    
		    contDetails.addComponent(lblDate);
    		contDetails.addComponent(lblDateValue);
    		
    		contDetails.addComponent(lblLocation);
    		contDetails.addComponent(lblLocationValue);
    		
    		Dimension d = this.getPreferredSize();
            d.setHeight(50);
    		contDetails.setPreferredSize(d);
    		
    		addComponent(contDetails);
    	}
    
    /**
	 * Fetches wound data from net
	 */
	private void queryWounds() {
	
		   try {
		       cloudDB.queryWounds("descending",lbWounds.getModel());
		} catch (IOException ioe) {
			HealthCollectorMIDlet.showErrorMessage("FEIL","Feil ved nettverksforbindelse til databasen; "+ioe.getMessage());
		    return;
		} catch (XmlPullParserException e)
			{
				HealthCollectorMIDlet.showErrorMessage("FEIL","Klarer ikke å tolke XML teksten fra server;"+e.getMessage());
			    return;
			}
		
		   if (lbWounds.getModel().getSize()==0)
		   {
			Dialog.show("INFORMASJON", "Fant ingen sår registrert", null, Dialog.TYPE_INFO, null, 4000);
		   } 
	
	}
	
	
	/**
	 * Fetches wound data from net
	 */
	private void queryThumbnailReferences(String woundContainer) {
	
		   try {
		       cloudDB.queryThumbnailReferences("descending",lbThumbnails.getModel(),woundContainer);
		} catch (IOException ioe) {
			HealthCollectorMIDlet.showErrorMessage("FEIL","Feil ved nettverksforbindelse til databasen; "+ioe.getMessage());
		    return;
		} catch (XmlPullParserException e)
			{
				HealthCollectorMIDlet.showErrorMessage("FEIL","Klarer ikke å tolke XML teksten fra server;"+e.getMessage());
			    return;
			}
		
		   if (lbThumbnails.getModel().getSize()==0)
		  	Dialog.show("INGEN BILDER", "Fant ingen bilder registrert", null, Dialog.TYPE_INFO, null, 4000);
		  
	}
	
	public void actionPerformed(ActionEvent ae)
		{
			Command c = ae.getCommand();
			Object source = ae.getSource();
		    
			if (btnGetThumbnails == source) {
				loadThumbnails();
								
				
			} else
			
		     if (c == cmdMenu) {
		    	
		    	 // Wait for background wound reader to finish
			      
		    	 try
					{
					 if (tReadWounds.isAlive())
						tReadWounds.join();
					} catch (InterruptedException e1)
					{
					}
					
					FormMainMenu menuScr = new FormMainMenu("Hoved Meny",parentMIDlet);
		    	    menuScr.show();
		     } else
		    	 
		    	 if (c == cmdFullScreen && lbThumbnails.getModel().getSize() > 0) {
		    		 	   FormImageShow fImage = new FormImageShow((ThumbnailCloudModel)lbThumbnails.getModel(),this);
			    		   fImage.show();
			    	 } 
			
		    	 else if (c == cmdGetThumbnails) {
		    		contThumbnails.setVisible(true);
		    		 loadThumbnails();
		 			
		    	 }
		    		 
		    	 else if (c == cmdImportImages) {
		    		 FormImageImport fImport = new FormImageImport("Importer bilder",this);
		    		 fImport.show();
		    	 }
		    	 else if (c == cmdSend) 
		    		 sendWound(lbImported.getModel());
			
		}

	private void loadThumbnails()
		{
			final Wound wound = (Wound)lbWounds.getSelectedItem();
			
			if (wound == null)
				{
				HealthCollectorMIDlet.showErrorMessage("IKKE VALGT SÅR", "Velg et sår for å vise bildene");
				return;
				}
			
			try
				{
					if (cloudDB.containerExist(wound.getId())) {

						lbThumbnails.setModel(new ThumbnailCloudModel()); // Create new thumbnail model

						Thread tReadThumbnailReferences = new Thread(new Runnable() {

							public void run()
								{
									queryThumbnailReferences(wound.getId());
									
								}
							
						});
						tReadThumbnailReferences.start();

					} else
						HealthCollectorMIDlet.showErrorMessage("INGEN BILDER", "Sår katalogen med bilder eksisterer ikke");
						
				} catch (IOException e)
				{
				   HealthCollectorMIDlet.showErrorMessage("INGEN BILDER","Klarte ikke å aksessere sår katalog med bilder; "+e.getMessage());
				}
		}
	
	private void sendWound(ListModel thumbnailModel)
		{
			// Validate
			  if (lbWounds == null)  // Guard 1
				  return;
			
			  Object selItem =  lbWounds.getSelectedItem();
				if (selItem == null ||  !(selItem instanceof Wound)) // Guard 2
					{
						HealthCollectorMIDlet.showErrorMessage("VELG SÅR", "Vennligst velg et sår");
						return;
					}
					
				
			if (lbImported == null || lbImported.getModel().getSize() == 0) // Guard 3
				{
					HealthCollectorMIDlet.showErrorMessage("INGEN BILDER", "Fant ingen bilder å sende");
					return;
				}

			// Initialization
			Vector vThumbnails = new Vector();
			int size=thumbnailModel.getSize();
			for (int i=0;i<size;i++) {
				Object o =  thumbnailModel.getItemAt(i);
			    vThumbnails.addElement(o);
//			    thumbnailModel.removeItem(i);
//			    lbWounds.getModel().addItem(o); // Update wounds images with imported image
			  
			}
			
			Wound wound = (Wound) selItem;
			wound.setThumbnails(vThumbnails);

			// Run sending on separate thread please, starts threading automatically

			SendWoundInBackground woundHandler = new SendWoundInBackground(wound,true);

			
		}

	public void selectionChanged(int oldIndex, int newIndex)
		{
			  if (lbWounds == null)  // Guard 1
				  return;
			
			  Object selItem =  lbWounds.getSelectedItem();
				if (selItem == null ||  !(selItem instanceof Wound)) // Guard 2
					return;
				
				// Fetch wound from selected item
				Wound wound  = (Wound) selItem;

				Date date = wound.getDate();
				if (date == null)
					lblDateValue.setText("Ukjent dato/tid");
				else
					lblDateValue.setText(Utility.formatDate(wound.getDate()));
				
				String location = wound.getLocation();
				if (location == null || location.length()==0)
					lblLocationValue.setText("Ukjent plassering");
			    else
				    lblLocationValue.setText(wound.getLocation());
			
				lbThumbnails.setModel(new ThumbnailCloudModel()); // Delete old model
		}
    
}
