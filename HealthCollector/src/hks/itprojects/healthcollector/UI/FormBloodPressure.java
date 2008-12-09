
package hks.itprojects.healthcollector.UI;


import hks.itprojects.healthcollector.PHR.*;
import hks.itprojects.healthcollector.backgroundTasks.SendBloodPressureInBackground;
import hks.itprojects.healthcollector.utils.UtilityUI;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.animations.*;
import com.sun.lwuit.layouts.*;
import java.util.*;
/**
 *
 * @author henning
 */
public class FormBloodPressure extends Form implements ActionListener {
    
    // Fields
    private TextArea tfSystolic;
    private TextArea tfDiastolic;
    private TextArea tfHR;
    private Date date = null;

    // Buttons
    
    private Button btnChangeDate;
    
    // Commands
    private Command cmdSend;    
    private Command cmdMenu;
    
    
    // Parent MIDlet
    
    private HealthCollectorMIDlet parentMIDlet = null;
    
    // Forms
    
    FormDateTime dateScr = null;
   
    public FormBloodPressure(String title, HealthCollectorMIDlet parentMIDlet)
    {
       super(title);
       this.parentMIDlet = parentMIDlet;
       setTransitionOutAnimator(Transition3D.createRotation(1000, true));

       BoxLayout boxLayout = new BoxLayout(BoxLayout.Y_AXIS);
       // Similar to WPF Stackpanel Orientation=Horizontal <=> BoxLayout.X_AXIS
       
       this.setLayout(boxLayout);
     
       // Heading
       Image imgHeart = HealthCollectorMIDlet.loadImage("/Heart.png");
       Label lblHeart = new Label(imgHeart);
       lblHeart.setAlignment(CENTER);
       addComponent(lblHeart);
     
       // Systolic blood pressure
       Label lblSystolic = new Label("Systolisk");
     
       UtilityUI.setMediumBold(lblSystolic);
       addComponent(lblSystolic);
       tfSystolic = new TextArea(null,1,1,TextArea.NUMERIC);
       addComponent(tfSystolic);
        
       // Diastolic 
       Label lblDiastolic = new Label("Diastolisk");
       UtilityUI.setMediumBold(lblDiastolic);
       addComponent(lblDiastolic);
       tfDiastolic = new TextArea(null,1,1,TextArea.NUMERIC);
       addComponent(tfDiastolic);
        
       // Heart rate
       Label lblHR = new Label("Puls");
       UtilityUI.setMediumBold(lblHR);
       addComponent(lblHR);
       tfHR = new TextArea(null,1,1,TextArea.NUMERIC);
       addComponent(tfHR);
       
       
       Label lblDate = new Label("Dato");
       UtilityUI.setMediumBold(lblDate);
       addComponent(lblDate);

       // In this case I think the DateField from lcdui is more compact,
       // and easier to implement, unfortunatly lwuit does not support
       // editing time --> must write extra code to support it. Hopefully
       // this will be a feature that is added to a future release of lwuit
       // since this most likely will be of quite high importance for business applications as
       // pointed out by several others in forums on the Internet
       
      date = java.util.Calendar.getInstance().getTime();
     
       btnChangeDate = new Button();
       UtilityUI.setButtonDate(btnChangeDate,date);
       btnChangeDate.addActionListener(this);
       addComponent(btnChangeDate);
      
        cmdSend = new Command("Send");
        cmdMenu = new Command("Meny");
        addCommand(cmdSend);
        addCommand(cmdMenu);
        setCommandListener(this);
      
    }
    
    
    
    
    
    
    public void actionPerformed(ActionEvent ae) {
    
        Command c = ae.getCommand();
        Object objSource = ae.getSource();
        
        // Buttons
        if (objSource != null)
          if (btnChangeDate == objSource)
          {
            dateScr = new FormDateTime("Endre dato/tid",parentMIDlet,this,date);
            dateScr.show();
       
          }
        
        // Commands
        if (c == cmdSend) 
          sendBloodPressure();
        else    
        if (c == cmdMenu)
          parentMIDlet.menuScr.show();
        else
        // From dateForm
        if (dateScr != null && c == dateScr.getCmdDateFormOK())
        {
           date = dateScr.getDate();
          
           UtilityUI.setButtonDate(btnChangeDate,date);
           this.show();
           dateScr = null;
        }
              
 }
    
    protected int validateInt(String key,String integer)
    {
        try {
          int i = Integer.parseInt(integer);
          return i;
        } catch (java.lang.NumberFormatException nfe)
        {
//           Alert alert = new Alert(key,"Feil tall format",null,AlertType.INFO);
//           alert.setTimeout(1000);
//           parentMIDlet.showAlert(alert,this);
//     
        }
        
        return -1;
    }
    
    private int validateSystolic()
    {
        int systolic = validateInt("Systolisk blodtrykk",tfSystolic.getText());
        
        if (systolic == -1)
           HealthCollectorMIDlet.showErrorMessage("Validering","Systolisk blodtrykk forstås ikke/ikke angitt");
        
        return systolic;
    }
    
    private int validateDiastolic()
    {
        int diastolic = validateInt("Diastolisk blodtrykk",tfDiastolic.getText());
        if (diastolic == -1)
           HealthCollectorMIDlet.showErrorMessage("Validering","Diastolisk blodtrykk forstås ikke/ikke angitt");
        
        return diastolic;
    }
    
    private int validateHR()
    {
        int hr = validateInt("Puls",tfHR.getText());
        if (hr == -1)
           HealthCollectorMIDlet.showErrorMessage("Validering","Puls forstås ikke/ikke angitt");
      
        return hr;
    }
    
    private boolean validate(int systolic, int diastolic, int hr)
    {
        
        // Do not proceed if no valid data available, allows partial data like only heart rate
        if (systolic == -1 && diastolic == -1 && hr == -1)
        {
        	HealthCollectorMIDlet.showErrorMessage("FEIL","Ingen data å sende, vennligst legg inn før du forsøker å sende");
            return false;
        }
        // If some invalid data, ask before save
        
        if (systolic == -1 || diastolic == -1 || hr == -1)
        {
          Command[] cmds = new Command[2];
          Command cmdOK = new Command("OK");
          Command cmdCancel = new Command("Avbryt");
          cmds[0] = cmdOK;
          cmds[1] = cmdCancel;
          String prompt = "";
          if (systolic == -1)
        	  prompt += "Systolisk, ";
          if (diastolic == -1)
        	  prompt += "Diastolisk, ";
          if (hr == -1)
        	  prompt += "Puls, ";
          
          prompt += "er ugyldig/ikke angitt, vil du likevel lagre?";
          
          Command cmdResult = Dialog.show("FORTSETTE?", prompt, cmds, Dialog.TYPE_INFO,null,0);
          if (cmdResult != cmdOK)
              return false;
          else
        	  return true;
        }
       
        // Systolic pressure less than diastolic will not be tolerated
        if (systolic <= diastolic)
        {
        	boolean res = Dialog.show("UGYLDIG","Systolisk trykk (hjerte slår) er lavere enn diastolisk (hjerte avslappet)",null,"Avbryt");
            return false;
        }
        
        return true;
    }
    
     public void sendBloodPressure()
    {
       // Validation of data before save
    	 
         int systolic = validateSystolic();
         int diastolic = validateDiastolic();
         int hr = validateHR();    
      
    	 if (!validate(systolic,diastolic,hr))
    	   return;
    	 
       // Save on background thread (automatic start in constructor of SendBloodPressureInBackground)
       
       BloodPressure bp = new BloodPressure(systolic,diastolic,hr,date);
       
      SendBloodPressureInBackground bloodPressureHandler = new SendBloodPressureInBackground(parentMIDlet,bp);
      }

	 
    
}
