package hks.itprojects.healthcollector.PHR;

/**
 *
 * @author henning
 */
import java.util.*;

public class BloodPressure {
	

    private int _systolic;
    public int getSystolic() { return _systolic; }
    public void setSystolic(int systolic) { this._systolic = systolic; }
    
    private int _diastolic;
    public int getDiastolic() { return _diastolic; }
    public void setDiastolic(int diastolic) { this._diastolic = diastolic; }
    
    private int _hr;
    public int getHeartRate() { return _hr; }
    public void setHeartRate(int hr) { this._hr = hr; }
    
    private Date _date;
    public Date getDate() { return _date; }
    public void setDate(Date date) { this._date = date; }
    
    private String _comment;
    public String getComment() { return _comment; }
    public void setComment(String comment) { this._comment = comment; }
    
    public String toString() // This method is called by default when lwuit-renders a List
    {
        return _date.toString();
    }
    
    public BloodPressure()
    {
        _systolic = -1;  // -1 is the udefined value that allows partial information like only systolic pressure to be stored
        _diastolic = -1;
        _hr = -1;
        _date = null;
        _comment = null;
    }
    
    public BloodPressure(int systolic, int diastolic, int hr, Date date)
    {
        _systolic = systolic;
        _diastolic = diastolic;
        _hr = hr;
        _date = date;
    }
    
 
}
