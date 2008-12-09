package hks.itprojects.healthcollector.UI;

import java.io.IOException;
import java.util.Vector;

import hks.itprojects.healthcollector.ListModels.ImageModel;
import hks.itprojects.healthcollector.ListModels.Thumbnail;
import hks.itprojects.healthcollector.ListRendering.ImageRenderer;
import hks.itprojects.healthcollector.ListRendering.ThumbnailRenderer;
import hks.itprojects.healthcollector.filemanager.*;

import com.sun.lwuit.*;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.DefaultListCellRenderer;

public class FormImageImport extends Form implements ActionListener
	{

		private Command cmdAddImage = null;
		private Command cmdRemoveImported = null;
		private Command cmdImport = null;
		private Command cmdBack = null;

		private List lFileList = null;

		private List lImported = null;

		private FormWound woundForm = null;
 
		private FormWoundOverview woundOverviewForm = null;
		
		public FormImageImport(String title, FormWound wForm)
			{
				super(title);

				this.woundForm = wForm;
				setup();
		     }
		
		public FormImageImport(String title, FormWoundOverview woForm)
			{
				super(title);

				this.woundOverviewForm = woForm;
				setup();
		     }
		

		private void setup()
			{
				 this.setMenuCellRenderer(new DefaultListCellRenderer(false)); // Disable numbering


					BorderLayout bLayout = new BorderLayout();

					setLayout(bLayout);

					Filemanager fManager = null;

					// Read files (JPG) from internal memory/memory stick

					try
						{
							fManager = new Filemanager();
						} catch (IOException e)
						{
							HealthCollectorMIDlet.showErrorMessage("FEIL", e
									.getMessage());
						}

					lImported = new List();
					lImported.setOrientation(List.HORIZONTAL);
					lImported.setListCellRenderer(new ThumbnailRenderer());
					lImported.setIsScrollVisible(true);
					lImported.setFixedSelection(List.FIXED_LEAD);
				
					// Limit size
					Dimension d = new Dimension();
					d.setHeight(65);
					lImported.setPreferredSize(d);

					setScrollable(false);

					// Get thumbnails from filesystem
					Vector vFileList = fManager.getFileList();
					if (vFileList == null)
						{
							HealthCollectorMIDlet.showErrorMessage("FEIL",
									"Ingen filer å vise!");
							return;
						}

					// Present available thumbnails
					lFileList = new List();
					lFileList.setModel(new ImageModel());
					for (int i = 0; i < vFileList.size(); i++)
						lFileList.addItem((Thumbnail)vFileList.elementAt(i));

					
					lFileList.setListCellRenderer(new ImageRenderer());
					lFileList.setFixedSelection(List.FIXED_CENTER);
					
					// Limit size to 150 pixels
					Dimension d2 = this.getPreferredSize();
					d2.setHeight(150);
					lFileList.setPreferredSize(d2);
					
					Label lblImportHeading = new Label("Importerte bilder");

					addComponent(BorderLayout.NORTH, lFileList);
					addComponent(BorderLayout.CENTER, lblImportHeading);
					addComponent(BorderLayout.SOUTH, lImported);

					cmdAddImage = new Command("LeggTil");
					addCommand(cmdAddImage);

					cmdRemoveImported = new Command("Fjern");
					addCommand(cmdRemoveImported);

					cmdImport = new Command("Importer");
					addCommand(cmdImport);

					cmdBack = new Command("Tilbake");
					addCommand(cmdBack);
					

					setCommandListener(this);

			}
		
		public void actionPerformed(ActionEvent evt)
			{
				// TODO Auto-generated method stub
				Command c = evt.getCommand();
				Thumbnail t = (Thumbnail) lFileList.getSelectedItem();
				if (t == null)
					return;

				if (c == cmdAddImage)
					lImported.addItem(t);

				else if (c == cmdRemoveImported)
					{
						int indx = lImported.getSelectedIndex();
						if (indx != -1)
							lImported.getModel().removeItem(indx);
					} else if (c == cmdBack)
					{
						if (woundForm != null)
						  woundForm.show();
						
						if (woundOverviewForm != null)
							woundOverviewForm.show();
						
					} else if (c == cmdImport)
					{
						// Handle import for woundForm
						if (woundForm != null) {
							woundForm.setThumbnailListModel(lImported.getModel());
							woundForm.show();	
						}
						
						if (woundOverviewForm != null)
							{
								woundOverviewForm.setImportedListModel(lImported.getModel());
								woundOverviewForm.show();
							}
						
					}

			}

	}
