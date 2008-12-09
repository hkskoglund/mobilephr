package hks.itprojects.healthcollector.ListModels;

import hks.itprojects.healthcollector.filemanager.Filemanager;
import java.io.IOException;

import com.sun.lwuit.*;

public class Thumbnail {
	
	public Thumbnail() {
		
	}
	
	public Thumbnail(String filename) 
	{
	  this.fileName = filename;
		
	}
	
	public String toString()
		{
			return this.id;
		}
	
	public void loadFromFile() throws IOException
	{
		thumbnailData = Filemanager.getThumbFromFile(this.fileName); 
				
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	

	public byte [] getThumbnailData()  {
		return thumbnailData;
	}
	
	public Image getThumbnailImage() throws IOException {
		Image imgThumb = Image.createImage(thumbnailData, 0, thumbnailData.length);
		return imgThumb;
	}

	public void setThumbnailData(byte[] thumbnailData) {
		this.thumbnailData = thumbnailData;
	}
	
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public long getFileSize() {
		return fileSize;
	}


	public void setId(String id)
		{
				this.id = id;
		}

	public String getId()
		{
				return id;
		}


	public void setThumbnailId(String thumbnailId)
		{
				this.thumbnailId = thumbnailId;
		}

	public String getThumbnailId()
		{
				return thumbnailId;
		}


	public void setWoundId(String woundId)
		{
				this.woundId = woundId;
		}

	public String getWoundId()
		{
				return woundId;
		}


	private byte [] thumbnailData;

	private String fileName;

	private long lastModified;
	
	
	private long fileSize;
	
	private String id;
	
	private String thumbnailId; // SDS entity BLOB id
	
	private String woundId; // SDS container or wound blobs
	
	

}
