package hks.itprojects.healthcollector.filemanager;

import hks.itprojects.healthcollector.ListModels.Thumbnail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import java.util.Enumeration;
import java.util.Vector;

public class Filemanager {
	
	// Internal memory
	private String dirCPictures = "file:///c:/pictures/";
	private String dirCCamera = "file:///c:/camera/";
	
	// Memory stick
	private String dirEDCIM = "file:///e:/DCIM/100MSDCF/";
	private String dirEPictures = "file:///e:/pictures/";
	
	private Vector vFileList = null;
	
	public Vector getFileList() {
		return vFileList;
	}

	
	public Filemanager() throws IOException {
		
//		if (hasFileConnectionAPI()==false);
//		{  
//		showErrorMessage("FEIL","Telefonen har ikke støtte for fil-behandling (fileconnection API).");
//		 //return;
//		destroyApp(false);
//	    this.notifyDestroyed();
//		}
		
		//getRoots();
		Vector vdirCPictures = getFileNames(dirCPictures);
		Vector vdirCCamera = getFileNames(dirCCamera);
		Vector vdirEDCIM = getFileNames(dirEDCIM);
		Vector vdirEPictures = getFileNames(dirEPictures);


		Vector vdirAll = new Vector();
	    vdirAll = addToVector(vdirAll,vdirCPictures);
		vdirAll = addToVector(vdirAll,vdirCCamera);
		vdirAll = addToVector(vdirAll,vdirEDCIM);
		vdirAll = addToVector(vdirAll,vdirEPictures);
		//Vector vThumbnails = generateThumbnails(vdirAll);
		
		vFileList = vdirAll;
		
	
		
	}
	
	
	// (C) Peter Wentzell, Sony Ericsson Tips and Tricks
	/**
	 * Traverse an inputStream and return a thumbnail image if any. We build
	 * the thumbnail directly from the inputstream, thus avoiding to run out
	 * of memory on very large picture files.
	 * 
	 * Modified 25 november 2008 : -use ByteArrayOutputStream and return
	 * byte [] instead of Image Henning K. Skoglund
	 * 
	 * @param str
	 *            the stream
	 * @returns byte[] - created from thumbnail iside jpeg file.
	 */
	public static byte[] getThumbFromFile(InputStream str)
		{
			// final int THUMB_MAX_SIZE = 16284; // Max Thumbnail size

			ByteArrayOutputStream tempByteArray = new ByteArrayOutputStream();

			// byte[] tempByteArray = new byte[THUMB_MAX_SIZE]; // how big
			// can a thumb get.
			byte[] bytefileReader = { 0 }; // lazy byte reader
			byte firstByte, secondByte = 0;
			// int currentIndex = 0;

			try
				{

					str.read(bytefileReader);
					firstByte = bytefileReader[0];

					str.read(bytefileReader);
					secondByte = bytefileReader[0];

					if (isJPEG(firstByte, secondByte))
						{
							byte rByte = 0;
							do
								{

									while (rByte != -1)
										{
											str.read(bytefileReader);
											rByte = bytefileReader[0];
										}

									str.read(bytefileReader);
									rByte = bytefileReader[0];

								} while ((rByte & 0xFF) != 0xD8); // thumb
																	// starts

							// tempByteArray[currentIndex++] = -1;
							// tempByteArray[currentIndex++] = rByte;

							tempByteArray.write((byte) -1);
							tempByteArray.write(rByte);

							rByte = 0;
							do
								{
									while (rByte != -1)
										{
											str.read(bytefileReader);
											rByte = bytefileReader[0];
											// tempByteArray[currentIndex++]
											// = rByte;
											tempByteArray.write(rByte);
										}
									str.read(bytefileReader);
									rByte = bytefileReader[0];
									// tempByteArray[currentIndex++] =
									// rByte;
									tempByteArray.write(rByte);
								} while ((rByte & 0xFF) != 0xD9); // thumb
																	// ends

							// byte[] thumbBytes = new byte[currentIndex-1];
							// tempByteArray[currentIndex++] = -1;
							tempByteArray.write((byte) -1);
							tempByteArray.flush();

							// Image im = Image.createImage ( tempByteArray,
							// 0, currentIndex-1);
							// tempByteArray = null;
							// return im;
							//	       

							return tempByteArray.toByteArray();
						}
					str.close();
				} catch (Exception ex)
				{
					System.out.println(" getThumbFromFile(): "
							+ ex.toString());
				}
			return null;
		}

	// (C) Peter Wentzell, Sony Ericsson Tips and Tricks
	/**
	 * Checks if two consectutive bytes can be interpreted as a jpeg
	 * 
	 * @param b1
	 *            first byte
	 * @param b2
	 *            second byte
	 * @return true if b1 and b2 are jpeg markers
	 */
	private static boolean isJPEG(byte b1, byte b2)
		{
			return ((b1 & 0xFF) == 0xFF && (b2 & 0xFF) == 0xD8);
		}

	public static byte[] getThumbFromFile(String filename)
			throws IOException
		{
			byte[] imgSmall = null;

			FileConnection fc = (FileConnection) Connector.open(filename,
					Connector.READ);

			if (fc.exists())
				{
					imgSmall = getThumbFromFile(fc.openInputStream());
					fc.close();
					fc = null;
				}

			return imgSmall;
		}

	
	private Vector addToVector(Vector vDirectory, Vector vDirectoryToAdd)
	{
		if (vDirectoryToAdd != null)
		 for (int i=0;i<vDirectoryToAdd.size();i++)
		  vDirectory.addElement(vDirectoryToAdd.elementAt(i));
		
		return vDirectory;
	}
	
	/**
	 * Gets JPG files in specified directory, will not traverse subdirectories
	 * @param directory
	 * @return Vector of thumbnails
	 */
	private Vector getFileNames(String directory) throws IOException
	{
		Vector vFileNames = null;
	
		FileConnection fcPictureDir = (FileConnection)Connector.open(directory,Connector.READ);
		
			if (fcPictureDir.isDirectory())
			{
				vFileNames = new Vector();
			   Enumeration eFilenames = fcPictureDir.list();
			   while (eFilenames.hasMoreElements()) {
				   String filename = (String) eFilenames.nextElement();
				   if (filename.endsWith("JPG")) {
					   FileConnection fcFile = (FileConnection)Connector.open(directory+filename,Connector.READ);
					   Thumbnail t = new Thumbnail(fcFile.getURL());
					   t.setLastModified(fcFile.lastModified());
					   t.setFileSize(fcFile.fileSize());
					   fcFile.close();
					   fcFile = null;
					   vFileNames.addElement(t);
						   
				   }
				    
			   }
			   fcPictureDir.close();
			   fcPictureDir = null;
			       	}
			else
				return null; // Not a directory
		
		
		return vFileNames;
		
	}
	
	// From http://developers.sun.com/mobility/apis/articles/fileconnection/
	private void getRoots() {
	      Enumeration drives = FileSystemRegistry.listRoots();
	      System.out.println("The valid roots found are: ");
	      while(drives.hasMoreElements()) {
	         String root = (String) drives.nextElement();
	         System.out.println("\t"+root);
	      }
	   }

	
	private boolean hasFileConnectionAPI()
	{
		String version = System.getProperty("microedition.io.file.FileConnection.version");
	    if (version == null)
	    	return false;
	    else
	    	return true;
	}


}
