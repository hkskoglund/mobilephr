package hks.itprojects.healthcollector.ListRendering;

import hks.itprojects.healthcollector.ListModels.Thumbnail;

import java.io.IOException;
import com.sun.lwuit.*;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.*;

public class ThumbnailRenderer extends Container implements ListCellRenderer
	{

		private Label lblFilename = new Label("");
		private Label lblLastModified = new Label("");

		public ThumbnailRenderer()
			{
				// this.parentMIDlet = parentMIDlet;
				this.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
				lblFilename.setAlignment(Label.CENTER);
				lblFilename.setVerticalAlignment(CENTER);
				addComponent(lblFilename);

			}

		public Component getListCellRendererComponent(List list, Object value,
				int index, boolean isSelected)
			{

				Thumbnail t = (Thumbnail) value;

				if (isSelected)
					{
						lblFilename.setFocus(true);
						lblFilename.getStyle().setBgTransparency(200);

					} else
					{
						lblFilename.setFocus(false);
						lblFilename.getStyle().setBgTransparency(0);
					}

				try
					{
						// lblFilename.setText(t.getFileName());
						lblFilename.setTextPosition(Label.BOTTOM);
						// lblFilename.setText(String.valueOf(Runtime.getRuntime().freeMemory()));
						// Date d = new Date();
						// d.setTime(t.getLastModified());
						// lblFilename.setText(d.toString());
						Dimension dList = list.getPreferredSize(); // Scale to list
						lblFilename.setIcon(t.getThumbnailImage().scaledHeight(dList.getHeight()-3));
					} catch (IOException e)
					{
						try
							{
								lblFilename.setIcon(Image
										.createImage("/Error.png")); // Local
																		// resource
							} catch (IOException e1)
							{
								// TODO Auto-generated catch block
								// e1.printStackTrace();
							}
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}

				return this;
			}

		public Component getListFocusComponent(List list)
			{
				// TODO Auto-generated method stub
				return null;
			}

	}
