package hks.itprojects.healthcollector.ListModels;


import java.io.IOException;
import com.sun.lwuit.list.DefaultListModel;

public class ImageModel extends DefaultListModel {

	// Override - load thumbnail from file
	public Object getItemAt(int index) {
		Thumbnail t = (Thumbnail) super.getItemAt(index);
	    try {
			t.loadFromFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
	    return (Object)t;
	}

	

}
