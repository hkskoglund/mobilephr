package hks.itprojects.healthcollector.UI;

import java.util.Vector;

import com.sun.lwuit.*;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.*;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.DefaultListCellRenderer;
import com.sun.lwuit.list.ListModel;

import hks.itprojects.healthcollector.ListRendering.ThumbnailRenderer;
import hks.itprojects.healthcollector.PHR.*;
import hks.itprojects.healthcollector.backgroundTasks.SendWoundInBackground;
import hks.itprojects.healthcollector.utils.UtilityUI;

public class FormWound extends Form implements ActionListener
	{

		private Label lblName = new Label("Navn");
		private TextArea taName = new TextArea();

		private Label lblLocation = new Label("Lokalisering");
		private TextArea taLocation = new TextArea();
		
		private Button btnChangeDate = null;
		private java.util.Date date = null;

		private Command cmdMenu = new Command("Hovedmeny");
		private Command cmdSend = new Command("Send");
		private Command cmdAddImage = new Command("Importer bilder");

		private HealthCollectorMIDlet parentMIDlet;

		private List lThumbnails = null;
		
		FormDateTime dateScr = null;
	   

		public void setThumbnailListModel(ListModel model)
			{
				lThumbnails.setModel(model);

			}

		public FormWound(String title, HealthCollectorMIDlet parentMIDlet)
			{
				super(title);

				this.parentMIDlet = parentMIDlet;
				
  			    this.setMenuCellRenderer(new DefaultListCellRenderer(false)); // Disable numbering


				BoxLayout boxLayout = new BoxLayout(BoxLayout.Y_AXIS);
				setLayout(boxLayout);

				UtilityUI.setMediumBold(lblName);
				UtilityUI.setMediumBold(lblLocation);

				lThumbnails = new List();

				lThumbnails.setOrientation(List.HORIZONTAL);
				lThumbnails.setListCellRenderer(new ThumbnailRenderer());
				lThumbnails.setIsScrollVisible(true);
				lThumbnails.setFixedSelection(List.FIXED_LEAD);
				Dimension d = new Dimension();
				d.setHeight(65);
				lThumbnails.setPreferredSize(d);

				addComponent(lblName);
				addComponent(taName);

				addComponent(lblLocation);
				addComponent(taLocation);
				
				Label lblDate = new Label("Dato");
			       UtilityUI.setMediumBold(lblDate);
			       addComponent(lblDate);

			      date = java.util.Calendar.getInstance().getTime();
			     
			       btnChangeDate = new Button();
			       UtilityUI.setButtonDate(btnChangeDate,date);
			       btnChangeDate.addActionListener(this);
			       addComponent(btnChangeDate);
			      

				Label lblThumbnailHeading = new Label("Bilder");
				UtilityUI.setMediumBold(lblThumbnailHeading);

				addComponent(lblThumbnailHeading);
				addComponent(lThumbnails);

				// To DO : date

				addCommand(cmdSend);
				addCommand(cmdMenu);
				addCommand(cmdAddImage);

				setCommandListener(this);
				
				this.setMenuTransitions(
						CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, false, 400, false), null);
			}

		public void actionPerformed(ActionEvent ae)
			{
				Command c = ae.getCommand();
				
				Object objSource = ae.getSource();
		        
		        // Buttons
		        if (objSource != null)
		          if (btnChangeDate == objSource)
		          {
		            dateScr = new FormDateTime("Endre dato/tid",this,date);
		            dateScr.show();
		       
		          }
		        

				if (c == cmdMenu) {
					FormMainMenu menuScr = new FormMainMenu("Hoved Meny",parentMIDlet);
					menuScr.show();

				}
					
				if (c == cmdSend)
					sendWound(lThumbnails.getModel());

				if (c == cmdAddImage)
					{
						FormImageImport frmImgOverview = new FormImageImport(
								"Importer bilder", this);

						frmImgOverview.show();
					}
				
				// From dateForm
		        if (dateScr != null && c == dateScr.getCmdDateFormOK())
		        {
		           date = dateScr.getDate();
		          
		           UtilityUI.setButtonDate(btnChangeDate,date);
		           this.show();
		           dateScr = null;
		        }

			}

		private void sendWound(ListModel thumbnailModel)
			{
				// Validate

				String name = taName.getText();
				if (name.length() == 0)
					{
						HealthCollectorMIDlet.showErrorMessage("VALIDERING",
								"Du må angi et navn for såret");
						return;
					}

				String location = taLocation.getText();
				
				

				// Initialization
				Vector vThumbnails = new Vector();
				int size=thumbnailModel.getSize();
				for (int i=0;i<size;i++)
				    vThumbnails.addElement(thumbnailModel.getItemAt(i));

				Wound wound = new Wound(name, location, date, vThumbnails );

				// Run sending on separate thread please, starts threading automatically

				SendWoundInBackground woundHandler = new SendWoundInBackground(wound,false);

				
			}

	
	}
