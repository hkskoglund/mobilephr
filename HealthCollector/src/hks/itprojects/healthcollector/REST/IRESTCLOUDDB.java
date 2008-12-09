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

		public abstract HttpResponse createWoundContainer(String woundId)
				throws IOException;
		
		public abstract HttpResponse createWoundThumbnailReference(String woundId, Thumbnail thumbnail) throws IOException;

		
		public abstract boolean woundContainerExist(String woundContainer) throws IOException;


		/* (non-Javadoc)
		 * @see hks.itprojects.healthcollector.azureSDS.IREST#createBloodPressure(hks.itprojects.healthcollector.PHR.PHRManager, hks.itprojects.healthcollector.PHR.BloodPressure)
		 */
		/* (non-Javadoc)
		 * @see hks.itprojects.healthcollector.REST.IREST#createBloodPressure(hks.itprojects.healthcollector.PHR.BloodPressure)
		 */
		public abstract HttpResponse createBloodPressure(
				BloodPressure bloodPressure) throws IOException;

		/* (non-Javadoc)
		 * @see hks.itprojects.healthcollector.REST.IREST#createWound(hks.itprojects.healthcollector.PHR.Wound)
		 */
		public abstract HttpResponse createWound(Wound wound)
				throws IOException;

		/* (non-Javadoc)
		 * @see hks.itprojects.healthcollector.REST.IREST#createWoundThumbnails(hks.itprojects.healthcollector.PHR.Wound)
		 */
		public abstract HttpResponse createWoundThumbnail(String woundId,
				Thumbnail thumbnail) throws IOException;

		/* (non-Javadoc)
		 * @see hks.itprojects.healthcollector.REST.IREST#queryBloodPressures(java.lang.String)
		 */
		public abstract void queryBloodPressures(String SortDirection, ListModel bpModel)
				throws IOException, XmlPullParserException;
		
		public abstract void queryWounds(String SortDirection, ListModel woundModel)throws IOException, XmlPullParserException;

		public abstract void queryThumbnailReferences(String SortDirection, ListModel thumbnailReferenceModel, String woundContainer) throws IOException, XmlPullParserException; 

		public abstract HttpResponse readWoundThumbnail(String woundContainer, Thumbnail thumbnail) throws IOException;


	}