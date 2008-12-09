package hks.itprojects.healthcollector.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class HttpResponse {
	private int code;
	private String message = null;
	private String xml = null;
	private ByteArrayInputStream bis = null;
	private ByteArrayOutputStream bos = null; // Returned byte data from server, introduced to read thumbnails
											  // thats maximum 16 Kbytes
	
	
	// Observation: Have constructor on top always, if not error messages are
	// generated like; method undefined for...
	
public HttpResponse(){
		
	}

public ByteArrayInputStream getByteArrayInputStream() {
	
	if (bis == null)
		bis = new ByteArrayInputStream(bos.toByteArray()); 
	
	return bis;
}

public ByteArrayOutputStream getByteArrayOutputStream() {
	return this.bos;
}

public void setByteArrayOutputStream(ByteArrayOutputStream bos)
	{
		this.bos = bos;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
