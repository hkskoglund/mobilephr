package hks.itprojects.healthcollector.UI;



import hks.itprojects.healthcollector.ListModels.Thumbnail;
import hks.itprojects.healthcollector.ListModels.ThumbnailCloudModel;

import com.sun.lwuit.Command;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;

public class FormImageShow extends Form implements ActionListener
	{
	
		private FormWoundOverview parentForm;
		
		private Command cmdBack;
		private int index;
		private int maxSize;
		private ThumbnailCloudModel tModel;
		private Label lblImage;
		
	public FormImageShow(ThumbnailCloudModel tModel, FormWoundOverview parentForm)
		{
			this.parentForm = parentForm;
			
			maxSize = tModel.getSize();
			if (tModel == null || maxSize ==0) { // Guard against invalid data
				parentForm.show();
				return;
			}
			
		index = tModel.getSelectedIndex();
		if (index <0)
			index = 0;
	   
		this.tModel = tModel;
        
		lblImage = new Label("");
		lblImage.getStyle().setMargin(0, 0, 0, 0);
		index = tModel.getSelectedIndex();
		showImage();
		
		addComponent(lblImage);
		
			cmdBack = new Command("Tilbake");
			addCommand(cmdBack);
			setCommandListener(this);
			
			// Image browsing back and forward handling
			this.addGameKeyListener(Display.GAME_RIGHT, new ActionListener() {

				public void actionPerformed(ActionEvent evt)
					{
						index++;
						if (index >= maxSize)
							index = 0;
						showImage();
						
					}});
			
			this.addGameKeyListener(Display.GAME_LEFT, new ActionListener() {

				public void actionPerformed(ActionEvent evt)
					{
						index--;
						if (index < 0)
							index = maxSize-1; // Start at end again
						showImage();
						
					}});
		}
	
	private void showImage()
		{
			Thumbnail t = (Thumbnail)tModel.getItemAt(index);
			Image img = Image.createImage(t.getThumbnailData(), 0, t.getThumbnailData().length);
			img = img.rotate(90); // Landscape mode
			img = img.scaledHeight(Display.getInstance().getDisplayHeight()-40);
			lblImage.setIcon(img);
				
		}

	public void actionPerformed(ActionEvent ae)
		{
           Command c = ae.getCommand();
           Object o = ae.getSource();
         
           if (c == cmdBack)
        	   parentForm.show();
		}

	}
