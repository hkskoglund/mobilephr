
package hks.itprojects.healthcollector.UI;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.layouts.*;
import com.sun.lwuit.util.*;

/**
 *
 * @author henning
 */
public class FormMainMenu extends Form implements ActionListener {

    
    private Button btnBloodPressure;
    private Button btnBPOverview;
    private Button btnWoundOverview;
    
    private Button btnTest;
    
    private Button btnRegisterWound;
    
    private TextArea taInfo;
    
    private Command cmdLogout;
    private Command cmdHelp;
    
    private HealthCollectorMIDlet parentMIDlet;
    
    public FormMainMenu(String title, HealthCollectorMIDlet parentMIDlet)
    {
        super(title);
        this.parentMIDlet = parentMIDlet;
        this.setLayout(new BorderLayout());
        Container cont = new Container();
        
        // Bloodpressure registration button
        Image imgHeart = HealthCollectorMIDlet.loadImage("/Heart.png");
        imgHeart = imgHeart.scaled(20,20);
        Image imgHeartEffect = Effects.reflectionImage(imgHeart);
        btnBloodPressure = new Button("Blodtrykk",imgHeartEffect);
        btnBloodPressure.setTextPosition(BOTTOM);
        btnBloodPressure.addActionListener(this);
        btnBloodPressure.setRolloverIcon(imgHeart.scaled(25, 25));
        btnBloodPressure.setPressedIcon(imgHeart.scaled(15,15));
        btnBloodPressure.getStyle().setBorder(null);
        
        cont.addComponent(btnBloodPressure);
        
        // Bloodpressure overview button
        Image imgBPOverview = HealthCollectorMIDlet.loadImage("/Overview.png");
        imgBPOverview = imgBPOverview.scaled(20, 20);
        Image imgBPOverviewEffect = Effects.reflectionImage(imgBPOverview);
        btnBPOverview = new Button("Oversikt BT",imgBPOverviewEffect);
        btnBPOverview.setTextPosition(BOTTOM);
        btnBPOverview.addActionListener(this);
        btnBPOverview.setRolloverIcon(imgBPOverview.scaled(25, 25));
        btnBPOverview.setPressedIcon(imgBPOverview.scaled(15,15));
        btnBPOverview.getStyle().setBorder(null);
      
        cont.addComponent(btnBPOverview);
        
        btnTest = new Button("Test");
        btnTest.addActionListener(this);
        btnTest.getStyle().setBorder(null);
         
       // cont.addComponent(btnTest);
        
        
        
        
        
        
     // Wound button
        Image imgWound = HealthCollectorMIDlet.loadImage("/Wound.png");
        imgWound = imgWound.scaled(20,20);
        Image imgWoundEffect = Effects.reflectionImage(imgWound);
        btnRegisterWound = new Button("Sår",imgWoundEffect);
        btnRegisterWound.setTextPosition(BOTTOM);
        btnRegisterWound.addActionListener(this);
        btnRegisterWound.setRolloverIcon(imgWound.scaled(25, 25));
        btnRegisterWound.setPressedIcon(imgWound.scaled(15,15));
        btnRegisterWound.getStyle().setBorder(null);
      
        cont.addComponent(btnRegisterWound);
      
        
     // Wound overview button
        Image imgWoundOverview = HealthCollectorMIDlet.loadImage("/Overview.png");
        imgWoundOverview = imgWoundOverview.scaled(20, 20);
        Image imgWoundOverviewEffect = Effects.reflectionImage(imgWoundOverview);
        btnWoundOverview = new Button("Oversikt sår",imgWoundOverviewEffect);
        btnWoundOverview.setTextPosition(BOTTOM);
        btnWoundOverview.addActionListener(this);
        btnWoundOverview.setRolloverIcon(imgWoundOverview.scaled(25, 25));
        btnWoundOverview.setPressedIcon(imgWoundOverview.scaled(15,15));
        btnWoundOverview.getStyle().setBorder(null);
      
        cont.addComponent(btnWoundOverview);
        
        
        
        // Commands
        cmdLogout = new Command("LoggUt");
        addCommand(cmdLogout);
        setBackCommand(cmdLogout);
//        cmdHelp = new Command("Hjelp");
//        addCommand(cmdHelp);
//        
        this.setCommandListener(this);
        
        
        taInfo = new TextArea();
        taInfo.setEditable(false);
        //taInfo.setColumns(20);
        taInfo.setRows(3);
        //taInfo.getStyle().setFont(new Font())
        taInfo.setText("Du kan lagre en blodtrykks-måling ved å trykke på registrer, trykk oversikt om du vil vise dine registrerte målinger");
        taInfo.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, 
        		Font.STYLE_PLAIN, Font.SIZE_SMALL));
        taInfo.getStyle().setBgTransparency(128);
        
        // Set width to parent form, and limit height to 100 pixels
        Dimension d = this.getPreferredSize();
        d.setHeight(120);
        cont.setPreferredSize(d);
        
        addComponent(BorderLayout.NORTH,cont);
       // addComponent(BorderLayout.SOUTH,taInfo);
        
    }
    
    public void actionPerformed(ActionEvent ae) {
        Command c = ae.getCommand();
        Object btnSource =  ae.getSource();
      
        // Observation - button will be in state STATE_ROLLOVER after clicked
        
        // Buttons
        
        if (btnBloodPressure == btnSource) {
        	   FormBloodPressure bloodPressureScr = new FormBloodPressure("Registrer måling",parentMIDlet);
          	  bloodPressureScr.show();
        }
        else
        if (btnBPOverview == btnSource) {
        	FormBPOverview overviewBPScr = new FormBPOverview("Blodtrykksoversikt",parentMIDlet);
        }
            
            else
        if (btnWoundOverview == btnSource)
        	{
        		FormWoundOverview woundOverviewForm = new FormWoundOverview("Oversikt sår",parentMIDlet);
        		
        	}
        else        
        if (btnRegisterWound == btnSource)
        {
        	FormWound woundForm = new FormWound("Registrer sår",this.parentMIDlet);
        	woundForm.show();
        }
        
        // Commands
        
        if (c == cmdLogout) {
        	FormLogin loginScr = 
        			new FormLogin("Innlogging", parentMIDlet);
        	loginScr.show();
        }
       
    }
    
   
}
