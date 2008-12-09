package hks.itprojects.healthcollector.PHR;

import java.util.Date;
import java.util.Vector;



public class BloodPressureAnalysis {
	
	private Vector bloodPressures = null;
	
	private int numSystolic;
	public int getNumSystolic() {
		return numSystolic;
	}

	public int getNumDiastolic() {
		return numDiastolic;
	}

	public int getNumHeartRate() {
		return numHeartRate;
	}

	private int numDiastolic;
	private int numHeartRate;
	
	private int maxSystolic = -1;
	public int getMaxSystolic() { return maxSystolic; }
	
	private int minSystolic = -1;
    public int getMinSystolic() { return minSystolic; }
    
    private int maxDiastolic = -1;
	public int getMaxDiastolic() { return maxDiastolic; }
	
	private int minDiastolic = -1;
    public int getMinDiastolic() { return minDiastolic; }
    
    private int minHeartRate = -1;
    public int getMinHeartRate() { return minHeartRate; }
    
    private int maxHeartRate = -1;
    public int getMaxHeartRate() { return maxHeartRate; }
    
	private double avgSystolic = 0.0;
	public double getAvgSystolic() { return avgSystolic; }
	
	private double avgDiastolic = 0.0;
	public double getAvgDiastolic() { return avgDiastolic; }

	private double avgHeartRate = 0.0;
	public double getAvgHeartRate() { return avgHeartRate; }

	public int getNumberOfMeasurements() {
		if (bloodPressures == null)
		  return 0;
	else
		return bloodPressures.size();
	}
	
	public Date getStartDate() {
		if (bloodPressures == null || bloodPressures.size()==0)
			return null;
		else 
			return ((BloodPressure)bloodPressures.elementAt(0)).getDate();
		
	}
	
	public Date getEndDate() {

	
		int size = bloodPressures.size();
		
		if (bloodPressures == null || size==0)
			return null;
		else 
			return ((BloodPressure)bloodPressures.elementAt(size-1)).getDate();
		
	}
	
	
	public void setData(Vector bloodPressures) {
		this.bloodPressures = bloodPressures;
	}
	
	public void performAnalysis() 
	{
	   int maxSystolic = -1;
	   int minSystolic = Integer.MAX_VALUE;
	   int maxDiastolic = -1;
	   int minDiastolic = Integer.MAX_VALUE;
	   int maxHeartRate = -1;
	   int minHeartRate = Integer.MAX_VALUE;
	   
	   double avgSystolic = 0.0;
	   double avgDiastolic = 0.0;
	   double avgHeartRate = 0.0;
	   double sumSystolic = 0.0;
	   double sumDiastolic = 0.0;
       double sumHr = 0.0;
       
	   if (bloodPressures == null || bloodPressures.size()==0) // Guard 1
	    return;
	
	   int numOfPressures = bloodPressures.size();
	   numSystolic = 0;
	   numDiastolic = 0;
	   numHeartRate = 0;
	   
	   for (int i=0;i<numOfPressures;i++)
	   {
		BloodPressure bp = (BloodPressure) bloodPressures.elementAt(i);   
		int systolic = bp.getSystolic();
		int diastolic = bp.getDiastolic();
		int hr = bp.getHeartRate();
		
		if (systolic != -1){
			numSystolic++;
			sumSystolic += systolic;
			
			if (systolic > maxSystolic)
				maxSystolic = systolic;
			
			if (systolic < minSystolic)
				minSystolic = systolic;
		}
	   
		if (diastolic != -1) {
			numDiastolic++;
			sumDiastolic += diastolic;
			
			if (diastolic > maxDiastolic)
		    	 maxDiastolic = diastolic;
		     
		     if (diastolic < minDiastolic)
		    	 minDiastolic = diastolic;
		  	
		}
		
		if (hr != -1)
		{
			numHeartRate++;
			sumHr += hr;
			
			if (hr > maxHeartRate)
				maxHeartRate = hr;
			
			if (hr < minHeartRate)
				minHeartRate = hr;
		}
	  }
	   
	   avgSystolic = sumSystolic/numOfPressures;
	   avgDiastolic = sumDiastolic/numOfPressures;
	   avgHeartRate = sumHr/numOfPressures;
	   
	   this.avgSystolic = avgSystolic;
	   this.avgDiastolic = avgDiastolic;
	   this.avgHeartRate = avgHeartRate;
	   this.maxSystolic = maxSystolic;
	   this.minSystolic = minSystolic;
	   
	   this.maxDiastolic = maxDiastolic;
	   this.minDiastolic = minDiastolic;
	 
	   this.maxHeartRate = maxHeartRate;
	   this.minHeartRate = minHeartRate;
	   
	}
	
	public double getSystolicStandardDeviation() throws Exception
	{
		throw new Exception("Not implemented!");
		// Seems like J2ME does not have support for square root
	}

}
