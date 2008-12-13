package hks.itprojects.healthcollector.REST;

import hks.itprojects.healthcollector.ListModels.Thumbnail;
import hks.itprojects.healthcollector.PHR.BloodPressure;
import hks.itprojects.healthcollector.PHR.Wound;
import hks.itprojects.healthcollector.network.HttpResponse;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

import com.sun.lwuit.list.ListModel;

public interface IRESTCLOUDDB
	{
		/**
		 * Check access to DB should return HTTP_OK
		 * @return HttpResponse
		 * @throws IOException
		 */
		public abstract HttpResponse checkAccess()
				throws IOException;

		public abstract String getServiceAddress();
		
		public abstract HttpResponse createContainer(String container)
				throws IOException;
		
		public abstract HttpResponse createWoundThumbnailReference(String woundId, Thumbnail thumbnail) throws IOException;

		
		public abstract boolean containerExist(String container) throws IOException;

		public abstract HttpResponse createBloodPressure(
				BloodPressure bloodPressure) throws IOException;

		public abstract HttpResponse createWound(Wound wound)
				throws IOException;

		public abstract HttpResponse createWoundThumbnail(String woundId,
				Thumbnail thumbnail) throws IOException;

		public abstract void queryBloodPressures(String SortDirection, ListModel bpModel)
				throws IOException, XmlPullParserException;
		
		public abstract void queryWounds(String SortDirection, ListModel woundModel)throws IOException, XmlPullParserException;

		public abstract void queryThumbnailReferences(String SortDirection, ListModel thumbnailReferenceModel, String woundContainer) throws IOException, XmlPullParserException; 

		public abstract HttpResponse readWoundThumbnail(String woundContainer, Thumbnail thumbnail) throws IOException;

		public abstract String getServiceName();

	}