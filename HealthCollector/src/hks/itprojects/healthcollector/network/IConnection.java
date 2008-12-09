package hks.itprojects.healthcollector.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface IConnection
	{

		/**
		 * Writes XML to specified Uri
		 * @param xml
		 * @param uri
		 * @param contentType
		 * @return
		 */
		public abstract HttpResponse writeXML(String xml, String uri,
				String contentType) throws IOException;

		// Background information :
		// http://msdn.microsoft.com/en-us/library/cc752964.aspx
		// Accessed : 25 november 2008
		public abstract HttpResponse writeBLOB(ByteArrayInputStream blob,
				int length, String containerUri, String MIMEType, String slug,
				String contentDisposition) throws IOException;

		public abstract HttpResponse writeBLOBSmall(byte [] blob,String containerUri, String MIMEType, String slug, String contentDisposition)throws IOException;
		
		
		/**
		 * Request https/GET and returns a string (xml)
		 * @param uri
		 * @param contentType
		 * @return
		 */
		public abstract HttpResponse readXML(String uri, String contentType)
				throws IOException;
		
		public abstract HttpResponse readBLOBSmall(String UriBlob, String MIMEType) throws IOException;
			

	}