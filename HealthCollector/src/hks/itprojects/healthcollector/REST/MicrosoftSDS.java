package hks.itprojects.healthcollector.REST;

import hks.itprojects.healthcollector.ListModels.Thumbnail;
import hks.itprojects.healthcollector.PHR.BloodPressure;
import hks.itprojects.healthcollector.PHR.Wound;
import hks.itprojects.healthcollector.network.*;
import hks.itprojects.healthcollector.utils.*;

import java.util.*;
import java.io.*;

import javax.microedition.io.HttpsConnection;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.*;

import com.sun.lwuit.list.ListModel;

/**
 * 
 * @author henning
 */

public class MicrosoftSDS implements IRESTCLOUDDB
	{

		// Change these settings to your own Microsoft SQL DataService-settings

		final public static String DefaultUserName = "hkscloudtest";
		final public static String DefaultPassword = "PASSWORD";
		final public static String DefaultAuthorityID = "hks";

		// Credentials
		private String UserName;
		private String Password;

		private String AuthorityId;

		private String AuthorityURI; // DNS to SDS

		private String getAuthorityURI()
			{
				return "https://" + AuthorityId
						+ ".data.database.windows.net/v1/";

			}

		private final String sdsContentType = "application/x-ssds+xml";

		private final String phrContainer = "PHR";

		private String uniqueUserId;

		// SDS REST API requires https functionality provided by a connection
		// manager
		private IConnection netManager = null;

		public MicrosoftSDS(String IMEI)
			{
				this.AuthorityId = MicrosoftSDS.DefaultAuthorityID;
				this.UserName = MicrosoftSDS.DefaultUserName;
				this.Password = MicrosoftSDS.DefaultPassword;
				this.uniqueUserId = IMEI;

				setupNetManager(UserName, Password);
			}

		public MicrosoftSDS(String IMEI, String AuthorityId, String username,
				String password)
			{
				this.AuthorityId = AuthorityId;
				UserName = username;
				Password = password;
				AuthorityURI = getAuthorityURI();
				this.uniqueUserId = IMEI;

				setupNetManager(UserName, Password);
			}

		private void setupNetManager(String userName, String Password)
			{
				netManager = new ConnectionManager(userName, Password); // Credentials
																		// (BASIC
																		// Authentication)
			}

		public HttpResponse createWoundContainer(String woundContainer)
				throws IOException
			{
				return CreateContainer(woundContainer);
			}

		public boolean woundContainerExist(String woundContainer)
				throws IOException
			{
				HttpResponse hResponse = netManager.readXML(this
						.makeUriCompatible(AuthorityURI + woundContainer),
						sdsContentType);
				int code = hResponse.getCode();
				if (code == HttpsConnection.HTTP_NOT_FOUND)
					return false;
				else if (code == HttpsConnection.HTTP_OK)
					return true;
				else
					return false;
			}

		protected HttpResponse CreateContainer(String container)
				throws IOException
			{
				String cont = "<s:Container xmlns:s='http://schemas.microsoft.com/sitka/2008/03/'>"
						+ "<s:Id>" + container + "</s:Id></s:Container>";
				HttpResponse hResponse = netManager.writeXML(cont,
						getAuthorityURI(), sdsContentType);

				return hResponse;
			}

		protected HttpResponse CreateEntity(String container, String xmlEntity)
				throws IOException
			{
				HttpResponse hResponse = netManager.writeXML(xmlEntity, this
						.makeUriCompatible(getAuthorityURI() + container),
						sdsContentType);
				return hResponse;
			}

		protected HttpResponse CreateBLOB(byte[] thumbnailData,
				String container, String MIMEType, String slug,
				String contentDisposition) throws IOException
			{
				// ByteArrayInputStream bis = new
				// ByteArrayInputStream(thumbnailData);
				HttpResponse hResponse = netManager.writeBLOBSmall(
						thumbnailData, makeUriCompatible(AuthorityURI
								+ container), MIMEType, slug,
						contentDisposition);
				return hResponse;
			}

		/**
		 * Replace special characters to make URI compatible
		 * 
		 * @param query
		 * @return
		 */
		private String makeUriCompatible(String query)
			{
				String newQuery = Utility.replaceAll(query, " ", "%20"); // Removes
																			// space
				newQuery = Utility.replaceAll(newQuery, "&&", "%26%26"); // Removes
																			// &&
				newQuery = Utility.replaceAll(newQuery, "==", "%3D%3D"); // Removes
																			// ==
				return newQuery;
			}

		protected HttpResponse query(String container, String query)
				throws IOException
			{
				// String fixedQuery = makeUriCompatible(query);
				HttpResponse hResponse = netManager.readXML(
						makeUriCompatible(getAuthorityURI() + container
								+ "?q='" + query + "'"), sdsContentType);
				return hResponse;
			}

		protected String getXmlDocumentHeader()
			{
				return "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

			}

		protected String getDecimalProperty(String property, int value)
			{
				String propertyDecimal = "<" + property
						+ " xsi:type=\"x:decimal\">" + String.valueOf(value)
						+ "</" + property + ">";

				return propertyDecimal;
			}

		protected String getStringProperty(String property, String value)
			{

				String propertyDecimal = "<" + property
						+ " xsi:type=\"x:string\">" + value + "</" + property
						+ ">";

				return propertyDecimal;
			}

		/**
		 * 
		 * Format date according to xml DateTime-format
		 * YYYY-MM-DDTHH:MM:SS.???????Z
		 * 
		 * @param property
		 * @param date
		 * @return
		 */
		protected String getDateTimeProperty(String property, Date date)
			{

				Calendar cal = Calendar.getInstance();
				cal.setTime(date);

				TimeZone tz = TimeZone.getTimeZone("UTC");
				cal.setTimeZone(tz);

				// Calendar.MONTH is zero-based --> add 1
				// String dateTime = String.valueOf(cal.get(Calendar.YEAR))+"-"+
				// Utility.addZeroIfNeccessary(cal.get(Calendar.MONTH)+1) +"-" +
				// Utility.addZeroIfNeccessary(cal.get(Calendar.DAY_OF_MONTH))
				// +"T"+
				// Utility.addZeroIfNeccessary(cal.get(Calendar.HOUR_OF_DAY))+":"+
				// Utility.addZeroIfNeccessary(cal.get(Calendar.MINUTE))+":"+
				// Utility.addZeroIfNeccessary(cal.get(Calendar.SECOND))+"."+
				// String.valueOf(cal.get(Calendar.MILLISECOND))+"0000Z";
				//   

				String dateTime = IsoDate.dateToString(date, IsoDate.DATE_TIME);

				String propertyDateTime = "<" + property
						+ " xsi:type=\"x:dateTime\">" + dateTime + "</"
						+ property + ">";

				return propertyDateTime;
			}

		public HttpResponse createBloodPressure(BloodPressure bloodPressure)
				throws IOException
			{
				// <Entity xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				// xmlns:x="http://www.w3.org/2001/XMLSchema"
				// xmlns:s="http://schemas.microsoft.com/sitka/2008/03/">
				// <s:Id></s:Id>
				// <numProp xsi:type="x:decimal">0</numProp>
				// <numProp1 xsi:type="x:decimal">1</numProp1>
				// <numProp2 xsi:type="x:decimal">2</numProp2>
				// </Entity>

				String propertySystolic;

				if (bloodPressure.getSystolic() == -1)
					propertySystolic = "";
				else
					propertySystolic = getDecimalProperty("Systolic",
							bloodPressure.getSystolic());

				String propertyDiastolic;

				if (bloodPressure.getDiastolic() == -1)
					propertyDiastolic = "";
				else
					propertyDiastolic = getDecimalProperty("Diastolic",
							bloodPressure.getDiastolic());

				String propertyHR;

				if (bloodPressure.getHeartRate() == -1)
					propertyHR = "";
				else
					propertyHR = getDecimalProperty("HR", bloodPressure
							.getHeartRate());

				String propertyDate = getDateTimeProperty("Date", bloodPressure
						.getDate());

				String propertyPhoneId = getStringProperty("PhoneId",
						uniqueUserId);

				String entityStart = "<BloodPressure xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:x=\"http://www.w3.org/2001/XMLSchema\" xmlns:s=\"http://schemas.microsoft.com/sitka/2008/03/\">";

				String entityId = "<s:Id>BP" + uniqueUserId
						+ Utility.getMilliSecondDate() + "</s:Id>";

				String entityEnd = "</BloodPressure>";

				String xmlBloodpressure = getXmlDocumentHeader() + "\n"
						+ entityStart + "\n" + entityId + "\n"
						+ propertyPhoneId + "\n" + propertySystolic + "\n"
						+ propertyDiastolic + "\n" + propertyHR + "\n"
						+ propertyDate + "\n" + entityEnd;

				HttpResponse hResponse = CreateEntity(phrContainer,
						xmlBloodpressure);

				return hResponse;
			}

		public HttpResponse createWound(Wound wound) throws IOException
			{

				String id = "W" + uniqueUserId + Utility.getMilliSecondDate(); // Will
																				// millisecond...
				wound.setId(id);

				String entityStart = "<Wound xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:x=\"http://www.w3.org/2001/XMLSchema\" xmlns:s=\"http://schemas.microsoft.com/sitka/2008/03/\">";

				String entityId = "<s:Id>" + id + "</s:Id>";

				String entityEnd = "</Wound>";

				String propertyPhoneId = getStringProperty("PhoneId",
						uniqueUserId);

				String propertyName = getStringProperty("Name", wound.getName());
				String propertyLocation = getStringProperty("Location", wound
						.getLocation());
				String propertyDate = getDateTimeProperty("Date", wound
						.getDate());

				String xmlWound = getXmlDocumentHeader() + "\n" + entityStart
						+ "\n" + entityId + "\n" + propertyPhoneId + "\n"
						+ propertyName + "\n" + propertyLocation + "\n"
						+ propertyDate + "\n" + entityEnd;

				// Store wound entity
				HttpResponse hResponse = CreateEntity(phrContainer, xmlWound);

				return hResponse;

			}

		public HttpResponse createWoundThumbnail(String woundContainer,
				Thumbnail thumbnail) throws IOException
			{
				String jpegMIMEType = "image/jpeg";

				// Give thumbnail an Id
				String id = "THUMBNAIL" + Utility.getMilliSecondDate();
				thumbnail.setId(id);

				HttpResponse hResponse = CreateBLOB(thumbnail
						.getThumbnailData(), woundContainer, jpegMIMEType,
						thumbnail.getId(), thumbnail.getFileName());

				return hResponse;

			}

		public HttpResponse readWoundThumbnail(String woundContainer,
				Thumbnail thumbnail) throws IOException
			{
				String jpegMIMEType = "image/jpeg";

				HttpResponse hResponse = netManager.readBLOBSmall(
						makeUriCompatible(this.AuthorityURI + woundContainer
								+ "/" + thumbnail.getThumbnailId()),
						jpegMIMEType);

				return hResponse;
			}

		public HttpResponse createWoundThumbnailReference(
				String woundContainer, Thumbnail thumbnail) throws IOException
			{
				String id = "TRef" + uniqueUserId
						+ Utility.getMilliSecondDate();

				String entityStart = "<TReference xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:x=\"http://www.w3.org/2001/XMLSchema\" xmlns:s=\"http://schemas.microsoft.com/sitka/2008/03/\">";

				String entityId = "<s:Id>" + id + "</s:Id>";

				String entityEnd = "</TReference>";

				String propertyPhoneId = getStringProperty("PhoneId",
						uniqueUserId);

				String propertyWoundId = getStringProperty("WoundId",
						woundContainer);

				String propertyThumbnailId = getStringProperty("ThumbnailId",
						thumbnail.getId());

				Date dLastModified = new Date();
				dLastModified.setTime(thumbnail.getLastModified());

				String propertyDate = this.getDateTimeProperty("Date",
						dLastModified);

				String xmlReference = getXmlDocumentHeader() + "\n"
						+ entityStart + "\n" + entityId + "\n"
						+ propertyPhoneId + "\n" + propertyWoundId + "\n"
						+ propertyThumbnailId + "\n" + propertyDate + "\n"
						+ entityEnd;

				// Store wound entity
				HttpResponse hResponse = CreateEntity(woundContainer,
						xmlReference);

				return hResponse;

			}

		public void queryBloodPressures(String SortDirection, ListModel bpModel)
				throws IOException, XmlPullParserException
			{

				// from e in entities where e["PhoneId"]==
				// "IMEI 00460101-501594-5-00"
				// && e.Kind=="BloodPressure" orderby e["Date"] descending
				// select e
				StringBuffer query = new StringBuffer();
				query.append("from e in entities ");
				query.append("where e[\"PhoneId\"]==\"" + uniqueUserId
						+ "\" && e.Kind==\"BloodPressure\" ");
				query.append("orderby e[\"Date\"] " + SortDirection
						+ " select e");

				// Forward to REST-api which then forwards to NET-api and gives
				// XML
				// result
				HttpResponse hResponse = query(phrContainer, query.toString());

				String result = hResponse.getXml();
				if (result != null)
					{
						// Create a mutal exlusive region on bpModel -> bpModel
						// will get locked
						synchronized (bpModel)
							{
								parseBloodPressureEntities(result, bpModel);

							}
					}
			}

		public void queryWounds(String SortDirection, ListModel woundModel)
				throws IOException, XmlPullParserException
			{

				// from e in entities where e["PhoneId"]==
				// "IMEI 00460101-501594-5-00"
				// && e.Kind=="BloodPressure" orderby e["Date"] descending
				// select e
				StringBuffer query = new StringBuffer();
				query.append("from e in entities ");
				query.append("where e[\"PhoneId\"]==\"" + uniqueUserId
						+ "\" && e.Kind==\"Wound\" ");
				query.append("orderby e[\"Date\"] " + SortDirection
						+ " select e");

				// Forward to REST-api which then forwards to NET-api and gives
				// XML
				// result
				HttpResponse hResponse = query(phrContainer, query.toString());

				String result = hResponse.getXml();
				if (result != null)
					{
						synchronized (woundModel)
							{
								parseWoundEntities(result, woundModel);

							}
					}
			}

		public void queryThumbnailReferences(String SortDirection,
				ListModel thumbnailReferenceModel, String woundContainer)
				throws IOException, XmlPullParserException
			{

				// from e in entities where e["PhoneId"]==
				// "IMEI 00460101-501594-5-00"
				// && e.Kind=="BloodPressure" orderby e["Date"] descending
				// select e
				StringBuffer query = new StringBuffer();
				query.append("from e in entities ");
				query.append("where e[\"PhoneId\"]==\"" + uniqueUserId
						+ "\" && e.Kind==\"TReference\" ");
				query.append("orderby e[\"Date\"] " + SortDirection
						+ " select e");

				// Forward to REST-api which then forwards to NET-api and gives
				// XML
				// result
				HttpResponse hResponse = query(woundContainer, query.toString());

				String result = hResponse.getXml();
				if (result != null)
					{
						synchronized (thumbnailReferenceModel)
							{
								parseThumbnailReferenceEntities(result,
										thumbnailReferenceModel);

							}
					}
			}

		// KXML-library copyright notice

		// Copyright (c) 2002-2007 Stefan Haustein, Oberhausen, Rhld., Germany
		//
		// Permission is hereby granted, free of charge, to any person obtaining
		// a
		// copy
		// of this software and associated documentation files (the "Software"),
		// to
		// deal
		// in the Software without restriction, including without limitation the
		// rights
		// to use, copy, modify, merge, publish, distribute, sublicense, and/or
		// sell copies of the Software, and to permit persons to whom the
		// Software
		// is
		// furnished to do so, subject to the following conditions:
		//
		// The above copyright notice and this permission notice shall be
		// included
		// in
		// all copies or substantial portions of the Software.
		//
		// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
		// EXPRESS
		// OR
		// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
		// MERCHANTABILITY,
		// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
		// SHALL
		// THE
		// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
		// OTHER
		// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
		// ARISING
		// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
		// DEALINGS
		// IN THE SOFTWARE.

		// My below XML parsing code is based on KXmlRssParser

		/***
		 * 
		 * Copyright (C) 2008 Alessandro La Rosa
		 * 
		 * This library is free software; you can redistribute it and/or modify
		 * it under the terms of the GNU Lesser General Public License as
		 * published by the Free Software Foundation; either version 2 of the
		 * License, or (at your option) any later version.
		 * 
		 * This library is distributed in the hope that it will be useful, but
		 * WITHOUT ANY WARRANTY; without even the implied warranty of
		 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
		 * Lesser General Public License for more details.
		 * 
		 * You should have received a copy of the GNU Lesser General Public
		 * License along with this library; if not, write to the Free Software
		 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
		 * USA
		 * 
		 * Contact: alessandro.larosa@gmail.com
		 * 
		 * Author: Alessandro La Rosa
		 */

		/**
		 * Parses an entityset of blood pressures (XML) from SDS database query
		 * 
		 * @param xml
		 * @return Vector of blood pressures
		 */
		private void parseBloodPressureEntities(String xml, ListModel bpModel)
				throws IOException, XmlPullParserException
			{

				// Vector bloodPressureItems = new Vector();

				// <s:EntitySet
				// xmlns:s="http://schemas.microsoft.com/sitka/2008/03/"
				// xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				// xmlns:x="http://www.w3.org/2001/XMLSchema">
				// <BloodPressure>
				// <s:Id>IMEI 35170701-107007-0-411226977644431</s:Id>
				// <s:Version>403262</s:Version>
				// <PhoneId xsi:type="x:string">IMEI
				// 35170701-107007-0-41</PhoneId>
				// <Systolic xsi:type="x:decimal">180</Systolic>
				// <Diastolic xsi:type="x:decimal">120</Diastolic>
				// <HR xsi:type="x:decimal">77</HR>
				// <Date xsi:type="x:dateTime">2008-11-18T03:07:08.662</Date>
				// </BloodPressure>
				// </s:EntitySet>

				InputStream bloodPressureStream = null;
				// http://johankanngard.net/2006/02/09/string-to-inputstream/
				// Accessed : 18 november
				try
					{
						bloodPressureStream = new ByteArrayInputStream(xml
								.getBytes("UTF-8"));
					} catch (java.io.UnsupportedEncodingException uex)
					{
					}

				if (bloodPressureStream == null)
					return;

				InputStreamReader isr = new InputStreamReader(
						bloodPressureStream, "UTF-8");

				KXmlParser parser = new KXmlParser();

				parser.setInput(isr);
				parser.setFeature(KXmlParser.FEATURE_PROCESS_NAMESPACES, true);

				parser.nextTag();
				parser.nextTag(); // Skips EntitySet

				while (parser.getEventType() != XmlPullParser.END_TAG)
					{
						String nodeName = parser.getName();

						if (nodeName.compareTo("BloodPressure") == 0)
							{
								BloodPressure bp = parseBloodPressureEntity(parser);
								bpModel.addItem(bp);
							} else
							{
								parser.skipSubTree();
							}
						parser.nextTag();
					}

				isr.close();
				isr = null;
				bloodPressureStream.close();
				bloodPressureStream = null;

				// int itemCount = bloodPressureItems.size();
				// this.bloodPressures = bloodPressureItems;
				// return bloodPressureItems;

			}

		private void parseWoundEntities(String xml, ListModel woundModel)
				throws XmlPullParserException, IOException
			{

				// Vector bloodPressureItems = new Vector();

				// <s:EntitySet
				// xmlns:s="http://schemas.microsoft.com/sitka/2008/03/"
				// xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				// xmlns:x="http://www.w3.org/2001/XMLSchema">
				// <BloodPressure>
				// <s:Id>IMEI 35170701-107007-0-411226977644431</s:Id>
				// <s:Version>403262</s:Version>
				// <PhoneId xsi:type="x:string">IMEI
				// 35170701-107007-0-41</PhoneId>
				// <Systolic xsi:type="x:decimal">180</Systolic>
				// <Diastolic xsi:type="x:decimal">120</Diastolic>
				// <HR xsi:type="x:decimal">77</HR>
				// <Date xsi:type="x:dateTime">2008-11-18T03:07:08.662</Date>
				// </BloodPressure>
				// </s:EntitySet>

				InputStream woundStream = null;
				// http://johankanngard.net/2006/02/09/string-to-inputstream/
				// Accessed : 18 november
				try
					{
						woundStream = new ByteArrayInputStream(xml
								.getBytes("UTF-8"));
					} catch (java.io.UnsupportedEncodingException uex)
					{
					}

				if (woundStream == null)
					return;

				InputStreamReader isr = new InputStreamReader(woundStream,
						"UTF-8");

				KXmlParser parser = new KXmlParser();

				parser.setInput(isr);
				parser.setFeature(KXmlParser.FEATURE_PROCESS_NAMESPACES, true);

				parser.nextTag();
				parser.nextTag(); // Skips EntitySet

				while (parser.getEventType() != XmlPullParser.END_TAG)
					{
						String nodeName = parser.getName();

						if (nodeName.compareTo("Wound") == 0)
							{
								Wound wound = parseWoundEntity(parser);
								woundModel.addItem(wound);
							} else
							{
								parser.skipSubTree();
							}
						parser.nextTag();
					}

				isr.close();
				isr = null;
				woundStream.close();
				woundStream = null;

			}

		private void parseThumbnailReferenceEntities(String xml,
				ListModel tReferenceModel) throws IOException,
				XmlPullParserException
			{

				// Vector bloodPressureItems = new Vector();

				// <s:EntitySet
				// xmlns:s="http://schemas.microsoft.com/sitka/2008/03/"
				// xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				// xmlns:x="http://www.w3.org/2001/XMLSchema">
				// <BloodPressure>
				// <s:Id>IMEI 35170701-107007-0-411226977644431</s:Id>
				// <s:Version>403262</s:Version>
				// <PhoneId xsi:type="x:string">IMEI
				// 35170701-107007-0-41</PhoneId>
				// <Systolic xsi:type="x:decimal">180</Systolic>
				// <Diastolic xsi:type="x:decimal">120</Diastolic>
				// <HR xsi:type="x:decimal">77</HR>
				// <Date xsi:type="x:dateTime">2008-11-18T03:07:08.662</Date>
				// </BloodPressure>
				// </s:EntitySet>

				InputStream tReferenceStream = null;
				// http://johankanngard.net/2006/02/09/string-to-inputstream/
				// Accessed : 18 november
				try
					{
						tReferenceStream = new ByteArrayInputStream(xml
								.getBytes("UTF-8"));
					} catch (java.io.UnsupportedEncodingException uex)
					{
					}

				if (tReferenceStream == null)
					return;

				InputStreamReader isr = new InputStreamReader(tReferenceStream,
						"UTF-8");

				KXmlParser parser = new KXmlParser();

				parser.setInput(isr);
				parser.setFeature(KXmlParser.FEATURE_PROCESS_NAMESPACES, true);

				parser.nextTag();
				parser.nextTag(); // Skips EntitySet

				while (parser.getEventType() != XmlPullParser.END_TAG)
					{
						String nodeName = parser.getName();

						if (nodeName.compareTo("TReference") == 0)
							{
								Thumbnail t = parseThumbnailReferenceEntity(parser);
								tReferenceModel.addItem(t);
							} else
							{
								parser.skipSubTree();
							}
						parser.nextTag();
					}

				isr.close();
				isr = null;
				tReferenceStream.close();
				tReferenceStream = null;

				// int itemCount = bloodPressureItems.size();
				// this.bloodPressures = bloodPressureItems;
				// return bloodPressureItems;

			}

		/**
		 * Parses an BloodPressure entity (XML) from the SDS database
		 * 
		 * @param parser
		 * @return BloodPressure
		 */
		private BloodPressure parseBloodPressureEntity(KXmlParser parser)
			{
				BloodPressure item = new BloodPressure();

				try
					{
						parser.nextTag();

						while (parser.getEventType() != XmlPullParser.END_TAG)
							{
								String nodeName = parser.getName();

								if (nodeName.compareTo("Systolic") == 0)
									{
										item.setSystolic(Integer
												.parseInt(parser.nextText()));
									} else if (nodeName.compareTo("Diastolic") == 0)
									{
										item.setDiastolic(Integer
												.parseInt(parser.nextText()));
									} else if (nodeName.compareTo("HR") == 0)
									{
										item.setHeartRate(Integer
												.parseInt(parser.nextText()));
									} else if (nodeName.compareTo("Date") == 0)
									{
										item.setDate(parseSDSDate(parser
												.nextText()));
									} else
									{
										parser.skipSubTree();
									}
								parser.nextTag();
							}
					} catch (XmlPullParserException ex)
					{

					} catch (IOException ioe)
					{
					}
				return item;
			}

		private Thumbnail parseThumbnailReferenceEntity(KXmlParser parser)
			{
				// <TReference>
				// <s:Id>TRefIMEI 35170701-107007-0-411228253453853</s:Id>
				// <s:Version>508877</s:Version>
				// <PhoneId xsi:type="x:string">IMEI
				// 35170701-107007-0-41</PhoneId>
				// <WoundId xsi:type="x:string">WIMEI
				// 35170701-107007-0-411228253435606</WoundId>
				// <ThumbnailId
				// xsi:type="x:string">THUMBNAIL1228253450697</ThumbnailId>
				// <Date xsi:type="x:dateTime">2008-11-22T13:34:10</Date>
				// </TReference>

				Thumbnail item = new Thumbnail();

				try
					{
						parser.nextTag();

						while (parser.getEventType() != XmlPullParser.END_TAG)
							{
								String nodeName = parser.getName();

								if (nodeName.compareTo("ThumbnailId") == 0)
									{ // Entity Id to thumbnail BLOB
										item.setThumbnailId(parser.nextText());
									} else if (nodeName.compareTo("WoundId") == 0) // Wound
																					// container
									item.setWoundId(parser.nextText());
								else if (nodeName.compareTo("Date") == 0)
									{
										item.setLastModified(parseSDSDate(
												parser.nextText()).getTime());
									} else
									{
										parser.skipSubTree();
									}
								parser.nextTag();
							}

					} catch (XmlPullParserException ex)
					{

					} catch (IOException ioe)
					{
					}
				return item;
			}

		private Wound parseWoundEntity(KXmlParser parser)
				throws XmlPullParserException, IOException
			{
				// <Wound>
				// <s:Id>WIMEI 35170701-107007-0-411227976445778</s:Id>
				// <s:Version>203412</s:Version>
				// <PhoneId xsi:type="x:string">IMEI
				// 35170701-107007-0-41</PhoneId>
				// <Name xsi:type="x:string">Psoriasis</Name>
				// <Location xsi:type="x:string">Ve albue</Location>
				// </Wound>
				//

				Wound item = new Wound();

				parser.nextTag();

				while (parser.getEventType() != XmlPullParser.END_TAG)
					{
						String nodeName = parser.getName();

						if (nodeName.compareTo("Name") == 0)
							{
								item.setName(parser.nextText());
							} else if (nodeName.compareTo("Location") == 0)
							{
								item.setLocation(parser.nextText());
							} else if (nodeName.compareTo("Date") == 0)
							{
								item.setDate(parseSDSDate(parser.nextText()));
							} else if (nodeName.compareTo("Id") == 0)
							{
								item.setId(parser.nextText());
							} else
							{
								parser.skipSubTree();
							}
						parser.nextTag();
					}

				return item;
			}

		/**
		 * Parses a DateTime xml-entry from SDS into J2ME Date object Nov. 25 -
		 * Use IsoDate-parsing in utility-package
		 * 
		 * @param date
		 * @return
		 */
		private Date parseSDSDate(String date)
			{
				// YYYY-MM-DDTHH:MM:SS.???????Z UTC

				int year = Integer.parseInt(date.substring(0, 4));
				int month = Integer.parseInt(date.substring(5, 7));
				int day = Integer.parseInt(date.substring(8, 10));
				int hour = Integer.parseInt(date.substring(11, 13));
				int minute = Integer.parseInt(date.substring(14, 16));
				int second = Integer.parseInt(date.substring(17, 19));
				// int millisecond = Integer.parseInt(date.substring(20, 23));

				Calendar cal = Calendar
						.getInstance(TimeZone.getTimeZone("UTC"));

				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.MONTH, month - 1); // Zero-index based thus - 1
				cal.set(Calendar.DAY_OF_MONTH, day);
				cal.set(Calendar.HOUR_OF_DAY, hour);
				cal.set(Calendar.MINUTE, minute);
				cal.set(Calendar.SECOND, second);
				// cal.set(Calendar.MILLISECOND,millisecond);

				Date d = cal.getTime();
				return d;
			}

	}
