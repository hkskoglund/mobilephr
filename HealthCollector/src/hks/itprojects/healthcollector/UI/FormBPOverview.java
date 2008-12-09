
package hks.itprojects.healthcollector.UI;

import hks.itprojects.healthcollector.ListRendering.BloodPressureRenderer;
import hks.itprojects.healthcollector.PHR.*;
import hks.itprojects.healthcollector.REST.IRESTCLOUDDB;
import hks.itprojects.healthcollector.REST.MicrosoftSDS;
import hks.itprojects.healthcollector.utils.Utility;
import hks.itprojects.healthcollector.utils.UtilityUI;

import java.util.*;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
//import com.sun.lwuit.animations.*;
import com.sun.lwuit.layouts.*;
import com.sun.lwuit.list.DefaultListModel;
import com.sun.lwuit.geom.*;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;


/**
 *
 * @author henning
 */
public class FormBPOverview extends Form implements ActionListener, SelectionListener {

	public boolean stopReading = false; // Signal to stop the background thread reader
	
	// Screen limit of list size
	private final int ListSizeInPixels = 100;
	
    // Commands
    private Command cmdMenu;
    private Command cmdBack;
    
    // Parent MIDlet
    
    private HealthCollectorMIDlet parentMIDlet = null;

    private IRESTCLOUDDB cloudDB = null;
    
    // Master
    private  List lbBloodPressures = null; 
    
    // Details
    private Label lblDate;
    private Label lblDateValue;
    
    private Label lblSystolic;
    private Label lblSystolicValue;
    
    private Label lblDiastolic;
    private Label lblDiastolicValue;
    
    private Label lblHeartRate;
    private Label lblHeartRateValue;
    
    
    private Container contOverview;
    private Container contAnalysis;
    
    public boolean noRegisteredBloodPressures = false;
    
    private BloodPressureAnalysis bpAnalyser;
    
    private Thread tReadBloodPressures = null;
    
    
    public FormBPOverview(String title, final HealthCollectorMIDlet parentMIDlet) {
        super(title);
        this.parentMIDlet = parentMIDlet;
        cloudDB  =  new MicrosoftSDS(HealthCollectorMIDlet.getIMEI());
        
       // this.setScrollableY(false); 
        
        
        final TabbedPane tabPane = new TabbedPane();
        tabPane.getStyle().setBgTransparency(128);
        
        contOverview = new Container();
        BorderLayout bLayout = new BorderLayout();
        contOverview.setLayout(bLayout);
        
        contAnalysis = new Container();
        BorderLayout bLayout2 = new BorderLayout();
        contAnalysis.setLayout(bLayout2);
        
        tabPane.addTab("Oversikt", contOverview);
        tabPane.addTab("Analyse",contAnalysis);
        tabPane.addTabsListener(new SelectionListener() {

			public void selectionChanged(int oldSelected, int newSelected)
				{
					
					if (newSelected == 1 && tReadBloodPressures != null && !tReadBloodPressures.isAlive()) {
						// Tab analysis
				        doAnalysis();
				        setupAnalysis();
				        setupMetaAnalysis();
				        
					} else if (newSelected == 1 && tReadBloodPressures != null && tReadBloodPressures.isAlive())
						HealthCollectorMIDlet.showErrorMessage("LASTER NED", "Holder fortsatt på med nedlasting av data, vennligst vent.");
					
				}});
        
       
        // Tab overview
        setupMasterOverview();
        setupDetailsOverview();
      
        
        // Commands
       
       cmdMenu = new Command("Meny");
       cmdBack = new Command("Tilbake");
       addCommand(cmdMenu);
       addCommand(cmdBack);
       setBackCommand(cmdBack);
     
       setCommandListener(this);
      
       // Limit list screen size
       Dimension d = this.getPreferredSize();
       d.setHeight(ListSizeInPixels);
       lbBloodPressures.setPreferredSize(d);
       
       addComponent(tabPane);
       
        tReadBloodPressures = new Thread(new Runnable() {

		public void run()
			{
				queryBloodPressures(); // Fetch data from net
			    		
			}});
        
        tReadBloodPressures.setPriority(Thread.NORM_PRIORITY);
        tReadBloodPressures.start();
      
        show();
       
       
    }
    
    /**
     * Set meta information about blood pressures like
     *  - number of measurements
     *  - start date
     *  - end date
     */
    private void setupMetaAnalysis()
    {
    	Container contMetaData = new Container();
        GridLayout gLayoutTwoColumn = new GridLayout(3,2);
        contMetaData.setLayout(gLayoutTwoColumn);
        
        Label lblMeasurements = new Label("Målinger");
        UtilityUI.setSmallBold(lblMeasurements);
        Label lblMeasurementsValue = new Label(String.valueOf(bpAnalyser.getNumberOfMeasurements()));
        
        Label lblStartDate = new Label("Start");
        UtilityUI.setSmallBold(lblStartDate);
        Label lblStartDateValue = new Label(bpAnalyser.getStartDate().toString());
       
        Label lblEndDate = new Label("Slutt");
        UtilityUI.setSmallBold(lblEndDate);
        Label lblEndDateValue = new Label(bpAnalyser.getEndDate().toString());
       
        contMetaData.addComponent(lblMeasurements);
        contMetaData.addComponent(lblMeasurementsValue);
        contMetaData.addComponent(lblStartDate);
        contMetaData.addComponent(lblStartDateValue);
        contMetaData.addComponent(lblEndDate);
        contMetaData.addComponent(lblEndDateValue);
        
        contAnalysis.addComponent(BorderLayout.SOUTH,contMetaData);
       
    }
    
    /**
     * Setup analysis information like
     *  - max,min,avg pressures
     */
    private void setupAnalysis()
    {
    	// Analysis
        GridLayout gAnalysisLayout = new GridLayout(4,4);
        Container containerAnalysis = new Container(gAnalysisLayout);
        
        Label lblAvgHeader = new Label("Gj.snitt");
        Label lblMaxHeader = new Label("Max.");
        Label lblMinHeader = new Label("Min.");
        
        Label lblSystolic = new Label("Systolisk");
        UtilityUI.setSmallBold(lblSystolic);
        Label lblAvgSystolicValue = new Label(String.valueOf(bpAnalyser.getAvgSystolic()));
        Label lblMaxSystolicValue = new Label(Utility.addTwoSpacesIfNeccessary(bpAnalyser.getMaxSystolic()));
        Label lblMinSystolicValue = new Label(Utility.addTwoSpacesIfNeccessary(bpAnalyser.getMinSystolic()));
        
        Label lblDiastolic = new Label("Diastolisk");
        UtilityUI.setSmallBold(lblDiastolic);
        Label lblAvgDiastolicValue = new Label(String.valueOf(bpAnalyser.getAvgDiastolic()));
        Label lblMaxDiastolicValue = new Label(Utility.addTwoSpacesIfNeccessary(bpAnalyser.getMaxDiastolic()));
        Label lblMinDiastolicValue = new Label(Utility.addTwoSpacesIfNeccessary(bpAnalyser.getMinDiastolic()));
        
        Label lblHeartRate = new Label("Puls");
        UtilityUI.setSmallBold(lblHeartRate);
        Label lblAvgHeartRateValue = new Label(String.valueOf(bpAnalyser.getAvgHeartRate()));
        Label lblMaxHeartRateValue = new Label(Utility.addTwoSpacesIfNeccessary(bpAnalyser.getMaxHeartRate()));
        Label lblMinHeartRateValue = new Label(Utility.addTwoSpacesIfNeccessary(bpAnalyser.getMinHeartRate()));
        
        
        // Header
        containerAnalysis.addComponent(new Label(""));
        containerAnalysis.addComponent(lblAvgHeader);
        containerAnalysis.addComponent(lblMaxHeader);
        containerAnalysis.addComponent(lblMinHeader);
        
        // Systolic
        containerAnalysis.addComponent(lblSystolic);
        containerAnalysis.addComponent(lblAvgSystolicValue);
        containerAnalysis.addComponent(lblMaxSystolicValue);
        containerAnalysis.addComponent(lblMinSystolicValue);
        
        // Diastolic
        containerAnalysis.addComponent(lblDiastolic);
        containerAnalysis.addComponent(lblAvgDiastolicValue);
        containerAnalysis.addComponent(lblMaxDiastolicValue);
        containerAnalysis.addComponent(lblMinDiastolicValue);
        
        // Heart rate
        containerAnalysis.addComponent(lblHeartRate);
        containerAnalysis.addComponent(lblAvgHeartRateValue);
        containerAnalysis.addComponent(lblMaxHeartRateValue);
        containerAnalysis.addComponent(lblMinHeartRateValue);
        
        contAnalysis.addComponent(BorderLayout.NORTH, containerAnalysis);
        
    }
    
    
    /**
     * Performs analysis of bloodpressure data
     */
    private void doAnalysis()
    {
     bpAnalyser = new BloodPressureAnalysis();
     Vector bpList = new Vector();
     DefaultListModel bpModel = (DefaultListModel) lbBloodPressures.getModel();
     
     int size = bpModel.getSize();
     
     for (int i=0; i<size;i++)
    	bpList.addElement((BloodPressure)bpModel.getItemAt(i));
    	 
     bpAnalyser.setData(bpList);
    bpAnalyser.performAnalysis();
    	
    }
    
    /**
     * Sets up the master list of blood pressures
     */
    private void setupMasterOverview()
    {
   	 
        lbBloodPressures = new List();
      //  BloodPressureModel bpModel = new BloodPressureModel();
        DefaultListModel bpModel = new DefaultListModel();
        lbBloodPressures.setModel(bpModel);
        bpModel.addSelectionListener(this);
        lbBloodPressures.setListCellRenderer(new BloodPressureRenderer());
        //lbBloodPressures.addSelectionListener(this);
        lbBloodPressures.setFixedSelection(List.FIXED_LEAD);
        
        lbBloodPressures.getModel().addDataChangedListener(new DataChangedListener() {

			public void dataChanged(int type, int index)
				{
					
					if (index == 1 && type == DataChangedListener.ADDED)
						selectionChanged(-1,0);
					
				}});
        
       
        contOverview.addComponent(BorderLayout.NORTH, lbBloodPressures);
      
    }
    
    /**
     * Construct detail container with systolic, diastolic and heart rate
     */
    private void setupDetailsOverview()
    {
        
        GridLayout gLayout = new GridLayout(4,2);
     
        Container containerDetail = new Container(gLayout);
       
        lblDate = new Label("Dato");
        UtilityUI.setSmallBold(lblDate);
        lblDate.getStyle().setBgTransparency(128);
        lblDateValue = new Label();
        
        lblSystolic = new Label("Systolisk");
        UtilityUI.setSmallBold(lblSystolic);
        lblSystolic.getStyle().setBgTransparency(128);
       
        lblSystolicValue = new Label("");
        lblSystolicValue.getStyle().setBgTransparency(128);
        
        lblDiastolic = new Label("Diastolisk");
        UtilityUI.setSmallBold(lblDiastolic);
        lblDiastolic.getStyle().setBgTransparency(128);
        lblDiastolicValue = new Label("");
        lblDiastolicValue.getStyle().setBgTransparency(128);
        
        lblHeartRate = new Label("Puls");
        lblHeartRate.getStyle().setBgTransparency(128);
        UtilityUI.setSmallBold(lblHeartRate);
        lblHeartRateValue = new Label("");
        lblHeartRateValue.getStyle().setBgTransparency(128);
        
       
        containerDetail.addComponent(lblDate);
        containerDetail.addComponent(lblDateValue);
       
        containerDetail.addComponent(lblSystolic);
        containerDetail.addComponent(lblSystolicValue);
        
        containerDetail.addComponent(lblDiastolic);
        containerDetail.addComponent(lblDiastolicValue);
        
        containerDetail.addComponent(lblHeartRate);
        containerDetail.addComponent(lblHeartRateValue);
        
        containerDetail.getStyle().setBgTransparency(128);
        // At first I thought this transparency value would be inherited by children
        // of container, but that's not the case with lwuit, this must be manually set 
        // also for childrens -> which leads to more code (setters on children), the inheritance feature of 
        // Microsoft WPF seems better here at least in terms of lines of codes economy
       
        contOverview.addComponent(BorderLayout.SOUTH,containerDetail);
     
    }


	/**
	 * Fetches blood pressure data from net
	 */
	private void queryBloodPressures() {
	
		   try {
		       cloudDB.queryBloodPressures("descending",lbBloodPressures.getModel());
		} catch (IOException ioe) {
			HealthCollectorMIDlet.showErrorMessage("FEIL","Feil ved nettverksforbindelse til database-tjenesten; "+ioe.getMessage());
		    return;
		}
		catch (XmlPullParserException e) {
			HealthCollectorMIDlet.showErrorMessage("FEIL","Klarte ikke å tolke XML for blodtrykk; "+e.getMessage());
			return;
		}
		
		   if (lbBloodPressures.getModel().getSize()==0)
		   {
			Dialog.show("INFORMASJON", "Fant ingen blodtrykk registrert", null, Dialog.TYPE_INFO, null, 4000);
			noRegisteredBloodPressures = true;
			return;
		   } else noRegisteredBloodPressures = false;
	
	}
    
    
    public void actionPerformed(ActionEvent ae) {
     Command c = ae.getCommand();
     
     if (c == cmdMenu) {
    	
    	 // Wait for background blood pressure reader to finish
       if (tReadBloodPressures.isAlive())
		try
			{
				tReadBloodPressures.join();
			} catch (InterruptedException e)
			{
			}
       
         parentMIDlet.menuScr.show();
     }
     }


    // Updates detail when master selected index changes
	public void selectionChanged(int oldSelected, int newSelected) {
		if (lbBloodPressures == null)  // Guard 1
			return;
		
		Object selItem =  lbBloodPressures.getSelectedItem();
		if (selItem == null ||  !(selItem instanceof BloodPressure)) // Guard 2
			return;
		
		// Fetch blood pressure from selected item
		BloodPressure bp = (BloodPressure) selItem;

		Date date = bp.getDate();
		int systolic = bp.getSystolic();
		int diastolic = bp.getDiastolic();
		int hr = bp.getHeartRate();
		
		// Write it to labels
		
		lblDateValue.setText(Utility.formatDate(date));
		if (systolic == -1)
		 lblSystolicValue.setText("---");
		else
		 lblSystolicValue.setText(Utility.addTwoSpacesIfNeccessary(systolic));
		
		if (diastolic == -1)
		   lblDiastolicValue.setText("---");
		else
		  lblDiastolicValue.setText(Utility.addTwoSpacesIfNeccessary(diastolic));
		
		if (hr == -1)
			lblHeartRateValue.setText("---");
		else
			lblHeartRateValue.setText(Utility.addTwoSpacesIfNeccessary(hr));
		
	}

}
