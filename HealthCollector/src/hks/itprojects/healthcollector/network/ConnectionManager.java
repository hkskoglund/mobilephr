package hks.itprojects.healthcollector.network;

import java.io.*;
import javax.microedition.io.*;

/**
 * @author henning
 *
 */
public class ConnectionManager implements IConnection {

	private String userName = null;
	private String password = null;

	private void setCredentials(String username, String password) {
		userName = username;
		this.password = password;
	}

	public ConnectionManager(String userName, String password) {
		setCredentials(userName, password);
	}

	
	/**
	 * Sets authorization header
	 * @param connection
	 */
	private void setAuthorizationHeader(HttpsConnection connection) throws IOException {
			connection.setRequestProperty("Authorization", "Basic "
					+ BasicAuth.encode(userName, password));

	}

	
	/**
	 * Sets content length header
	 * @param connection
	 * @param length
	 */
	private void setContentLengthHeader(HttpsConnection connection, int length) throws IOException {
			connection.setRequestProperty("Content-Length", String
					.valueOf(length));
	}
	
	
	
	private void setAcceptHeader(HttpsConnection connection, String MIMEType) throws IOException {
		connection.setRequestProperty("Accept", MIMEType);
	}

	
	private HttpsConnection SetupHttpsRequest(String reqMethod, String Uri) throws IOException {
		
		HttpsConnection connection = null;

		connection = (HttpsConnection) Connector.open(Uri);

		connection.setRequestMethod(reqMethod);

		setAuthorizationHeader(connection);

		return connection;

	}

	/**
	 * Setup a https request header also with authorization
	 * @param reqMethod
	 * @param Uri
	 * @param contentType
	 * @return
	 */
	private HttpsConnection SetupHttpsRequest(String reqMethod, String Uri,
			String contentType) throws IOException {
		
        HttpsConnection connection = SetupHttpsRequest(reqMethod,Uri);
		connection.setRequestProperty("Content-Type", contentType);

		return connection;

	}

	private HttpsConnection SetupHttpsRequest(String reqMethod, String Uri,
			String contentType, int length)throws IOException {
		
		HttpsConnection connection = SetupHttpsRequest(reqMethod, Uri,
				contentType);
		
		if (reqMethod.equals(HttpsConnection.POST))
			setContentLengthHeader(connection, length);
		
		
			
		return connection;

	}

	
	/* (non-Javadoc)
	 * @see hks.itprojects.healthcollector.network.IConnection#writeXML(java.lang.String, java.lang.String, java.lang.String)
	 */
	public HttpResponse writeXML(String xml, String uri, String contentType)throws IOException {
	
		HttpsConnection connection = SetupHttpsRequest(HttpsConnection.POST,
				uri, contentType);

		writeOutputStream(connection, xml);
		HttpResponse hResponse = readInputStreamUTF8(connection);

		connection.close();
		connection = null;

		return hResponse;
	}

	// Background information :
	// http://msdn.microsoft.com/en-us/library/cc752964.aspx
	// Accessed : 25 november 2008
	/* (non-Javadoc)
	 * @see hks.itprojects.healthcollector.network.IConnection#writeBLOB(java.io.ByteArrayInputStream, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public HttpResponse writeBLOB(ByteArrayInputStream blob, int length,String containerUri, String MIMEType, String slug, String contentDisposition)throws IOException {
		
		// Setup
		HttpsConnection connection = SetupHttpsRequest(HttpsConnection.POST, containerUri, MIMEType);

		connection.setRequestProperty("Slug", slug); // SDS id-value
        connection.setRequestProperty("Content-Disposition",contentDisposition);

        // Request-response
        
		long elapsedTimeMillisecond = writeOutputStream(connection, blob,length);
		
		HttpResponse hResponse = readInputStreamUTF8(connection);

		// Cleanup
		connection.close();
		connection = null;

		return hResponse;
	}

	
	
public HttpResponse writeBLOBSmall(byte [] blob,String containerUri, String MIMEType, String slug, String contentDisposition)throws IOException {
		
		// 1. Setup
		HttpsConnection connection = SetupHttpsRequest(HttpsConnection.POST, containerUri, MIMEType);

		connection.setRequestProperty("Slug", slug); // SDS id-value
        connection.setRequestProperty("Content-Disposition",contentDisposition);
        setContentLengthHeader(connection, blob.length);
		
        // 2. Request-response
        
		long elapsedTimeMillisecond = writeOutputStream(connection, blob);
		
		HttpResponse hResponse = readInputStreamUTF8(connection);

		// 3. Cleanup
		connection.close();
		connection = null;

		return hResponse;
	}

	
	
	/**
	 * Writes payload to https connection
	 * @param connection
	 * @param payload
	 */
	private void writeOutputStream(HttpsConnection connection, String payload) throws IOException, UnsupportedEncodingException {
		OutputStream outputStream = null;

		byte[] payloadBytes = null;

		
		// Enconding UTF-8
			payloadBytes = payload.getBytes("UTF-8"); 
	
			setContentLengthHeader(connection, payloadBytes.length);
			
			outputStream = connection.openOutputStream();
			outputStream.write(payloadBytes);
			outputStream.flush();
			outputStream.close();
			outputStream = null;

		
	}
	
	/**
	 * Writes payload to https connection
	 * Idea : Filesystem stream/input -> network stream/output
	 * @param connection
	 * @param payloadBytes
	 */
	private long writeOutputStream(HttpsConnection connection, ByteArrayInputStream is, int length) throws IOException {
		
		if (length <= 0)
			return -1;
		
		
		OutputStream outputStream = null;

		setContentLengthHeader(connection, length);
			
			outputStream = connection.openOutputStream();
			
			int rbyte = -1;
		//	int bytesWritten = 0;
			
		
			
//			Calendar calStart = Calendar.getInstance();
//	        Date dStart = calStart.getTime();
//	        long startTime = dStart.getTime();
//	    
	        	
	        	while ((rbyte = is.read()) != -1) {
				outputStream.write(rbyte);
		//		bytesWritten++;
			}
			  
			outputStream.flush();
			outputStream.close();
			outputStream = null;
			
//			Calendar calEnd = Calendar.getInstance();
//			Date dEnd = calEnd.getTime();
//			long endTime = dEnd.getTime();
//			
//			if (bytesWritten != length)
//				throw new Exception("Bytes written to web-server "+String.valueOf(bytesWritten)+" not equal to content length"+String.valueOf(length));
//			
			
			return -1;
	}

	
	
private long writeOutputStream(HttpsConnection connection, byte [] blob) throws IOException {
		
		if (blob.length <= 0)
			return -1;
		
		
		DataOutputStream dos = new DataOutputStream(connection.openOutputStream());
			
	    dos.write(blob);
	    dos.close(); // Implicit flush
	    dos = null;
			
			return -1;
	}

	

    
	/**
	 * Reads an input stream character by character from an https connection and build up a string
	 * @param connection
	 * @return string of 
	 */
	private HttpResponse readInputStreamUTF8(HttpsConnection connection) throws IOException {
		StringBuffer result = null;
		InputStreamReader isr = null; // For character encoding UTF-8
        final int EndOfStream = -1; // End of Stream
	   
        
        // Read raw byte stream from server
        HttpResponse hResponse = readInputStreamRAW(connection);
        
        // Convert to UTF-8 coding
        
			isr = new InputStreamReader(hResponse.getByteArrayInputStream(), "UTF-8");
			hResponse.setByteArrayOutputStream(null); // Free some memory

			result = new StringBuffer();
	

			int ch = EndOfStream;
	
			do {

				ch = isr.read();

				if (ch != EndOfStream)
					result.append((char) ch);
			
			} while (ch != EndOfStream);

			if (result != null)
			  hResponse.setXml(result.toString());
			else
			  hResponse.setXml(null);
			
			isr.close();
			isr = null;
	    
       return hResponse;
       
	}
	
	public HttpResponse readBLOBSmall(String UriBlob, String MIMEType) throws IOException {
		
		HttpsConnection connection = SetupHttpsRequest(HttpsConnection.GET,
				UriBlob);

		setAcceptHeader(connection, MIMEType);
		
		HttpResponse hResponse = readInputStreamRAW(connection);
		return hResponse;
	}
	
	
	/**
	 * Reads a bytestream from the http-server - will work for relatively small bytestreams that does not fill phone memory
	 * the other way is to return an inputStream to higher levels (UI) and let the reading take place there...
	 * @param connection
	 * @param bos - byte stream
	 * @return
	 * @throws IOException
	 */
	private HttpResponse readInputStreamRAW(HttpsConnection connection) throws IOException {
	    InputStream is = null;
		final int EndOfStream = -1; // End of Stream
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
			is = connection.openInputStream();

			int ch = EndOfStream;
	
			do {

				ch = is.read();

				if (ch != EndOfStream)
					bos.write(ch);
			
			} while (ch != EndOfStream);

			HttpResponse hResponse = fetchResponseCodeAndMessage(connection);
		
			hResponse.setByteArrayOutputStream(bos);
			
			is.close();
			is = null;
	    
       return hResponse;
       
	}
	
	
	
	private HttpResponse fetchResponseCodeAndMessage(HttpsConnection connection) throws IOException {
		HttpResponse hResponse = new HttpResponse();
		hResponse.setCode(connection.getResponseCode());
		hResponse.setMessage(connection.getResponseMessage());
        return hResponse;
		
	}
	
	/* (non-Javadoc)
	 * @see hks.itprojects.healthcollector.network.IConnection#readXML(java.lang.String, java.lang.String)
	 */
	public HttpResponse readXML(String uri, String contentType) throws IOException {
		
		HttpsConnection connection = SetupHttpsRequest(HttpsConnection.GET,
				uri, contentType);

		HttpResponse hResponse =  readInputStreamUTF8(connection);

   	    connection.close();
   	    
   	    connection = null;

		return hResponse;
	}

}
