package hks.itprojects.healthcollector.UI;

import java.io.IOException;

import hks.itprojects.healthcollector.REST.IRESTCLOUDDB;
import hks.itprojects.healthcollector.REST.MicrosoftSDS;
import hks.itprojects.healthcollector.authorization.LoginUserSDS;
import hks.itprojects.healthcollector.network.HttpResponse;
import hks.itprojects.healthcollector.utils.UtilityUI;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.animations.*;
import com.sun.lwuit.layouts.*;

import javax.microedition.io.HttpsConnection;
import javax.microedition.rms.*;

/**
 * 
 * @author henning
 */
public class FormLoginSDS extends Form implements ActionListener
	{

		// Fields
		private TextArea tfPassword;
		private TextArea tfUsername;
		private TextArea tfLoginStatus;
		private TextArea tfAuthorityID;

		// Commands
		private Command cmdExit;
		private Command cmdLogin;

		private HealthCollectorMIDlet parentMIDlet = null;
		
		private IRESTCLOUDDB cloudDB = null;
	    
		private final String AUTHORIZATIONSTORE = "Authorization";
		
		public FormLoginSDS(HealthCollectorMIDlet parentMIDlet)
			{
				super("Innlogging");
				this.parentMIDlet = parentMIDlet;
				this.setTransitionOutAnimator(CommonTransitions
						.createFade(1000));

				BoxLayout boxLayout = new BoxLayout(BoxLayout.Y_AXIS);
				this.setLayout(boxLayout);
				
					
				// Heading
				Image imgLock = HealthCollectorMIDlet.loadImage("/Lock.png");
				imgLock.scaled(100, 100);
				Label lblLock = new Label(imgLock);
				lblLock.setAlignment(CENTER);
				addComponent(lblLock);

				// SDS AuthorityID
				Label lblAuthorityID = new Label("AutoritetsID");
				addComponent(lblAuthorityID);
				tfAuthorityID = new TextArea();
				addComponent(tfAuthorityID);
				
				// Username
				Label lblUserName = new Label("Brukernavn");
				addComponent(lblUserName);
				tfUsername = new TextArea();
				addComponent(tfUsername);

				// Password
				Label lblPassword = new Label("Passord");
				addComponent(lblPassword);
				tfPassword = new TextArea(null, 1, 1, TextArea.PASSWORD);
				addComponent(tfPassword);
				
				// Status
				Label lblStatus = new Label("Status");
				addComponent(lblStatus);
				tfLoginStatus = new TextArea(null,3,20,TextArea.UNEDITABLE);
				UtilityUI.setSmall(tfLoginStatus);
				addComponent(tfLoginStatus);

				// Commands

				cmdExit = new Command("Avslutt");
				cmdLogin = new Command("LoggInn");
				addCommand(cmdLogin);
				addCommand(cmdExit);
				setBackCommand(cmdExit);
				setCommandListener(this);
				
				getLoginAuthorization();

			}

		/**
		 * Retrieves user login information from RMS authorization database
		 */
		private void getLoginAuthorization()
			{
				LoginUserSDS user = null;
				
			RecordStore rs = openAuthorizationDB();
				
			    try
					{
						int numrec = rs.getNumRecords();
						if (numrec == 1) {
						
				            // Retrive user from DB
							user = new LoginUserSDS();
							user.fromByteArray(rs.getRecord(1));
							HealthCollectorMIDlet.setLoginUser(user);
							// Update UI
							tfUsername.setText(user.getUserName());
							tfPassword.setText(user.getPassword());
							tfAuthorityID.setText(user.getAuthorityID());
						}
						
					} 
			    
			    catch (RecordStoreNotOpenException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidRecordIDException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RecordStoreException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					closeAuthorizationDB(rs);
			}

		private void closeAuthorizationDB(RecordStore rs)
			{
				try
					{
						rs.closeRecordStore();
					} catch (RecordStoreNotOpenException e)
					{
						HealthCollectorMIDlet.showErrorMessage("FEIL","Autorisasjons database er ikke åpen; "+e.getMessage());
						
					} catch (RecordStoreException e)
					{
						HealthCollectorMIDlet.showErrorMessage("FEIL","Autorisasjons database feil ved aksess; "+e.getMessage());
					}
			}
		
		private RecordStore openAuthorizationDB()
			{
				RecordStore rs = null;
		
				try {
					rs = RecordStore.openRecordStore("Authorization", true, RecordStore.AUTHMODE_PRIVATE, true);
				    
				} catch (RecordStoreException rse) {
					HealthCollectorMIDlet.showErrorMessage("FEIL","Problemer med tilgang til autorisasjons informasjon i RMS; "+rse.getMessage());
				}
				
				return rs;
			}
		
		private void deleteAuthorizationDB()
			{
				try
					{
						RecordStore.deleteRecordStore("Authorization");
					} catch (RecordStoreNotFoundException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RecordStoreException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		
		private int saveLoginUser(String username, String password, String authorityID)
			{
				boolean update  = false;
				LoginUserSDS userStored = HealthCollectorMIDlet.getLoginUser();
			
				// Validate
				if (username == null || password == null)
					{
						HealthCollectorMIDlet.showErrorMessage("BRUKERNAVN/PASSORD","Brukernavn eller passord ikke definert");
						return -1;
						
					}
				
				if (username.length()==0 || password.length()==0)
					{
						HealthCollectorMIDlet.showErrorMessage("BRUKERNAVN/PASSORD","Brukernavn eller passord ikke angitt");
					    return -1;		
					}
				
			   if (authorityID == null || authorityID.length() == 0)
				   {
						HealthCollectorMIDlet.showErrorMessage("AUTORITESTID","Authoritets id. som er start på DNS for databasen er ikke definert");
					    return -1;		
					}
				
				   
				RecordStore rs = openAuthorizationDB();
				
				try {
		               // Case 1 : User already stored - record nr. 1

					if (userStored != null)
					{
							
						if (!userStored.getUserName().equals(username)) {
							userStored.setUserName(username);
							update = true;
						}
						
						if (!userStored.getPassword().equals(password)) {
							userStored.setPassword(password);
							update = true;
						}
					
						String aId = userStored.getAuthorityID();
						if (aId == null || (!aId.equals(authorityID)))
							{
								userStored.setAuthorityID(authorityID);
								update = true;
							} 
						
						if (update) {
							byte [] userdata = userStored.toByteArray();
									rs.setRecord(1, userdata, 0, userdata.length);
						}
					} else

			// Case 2 : User not previously stored
				if (userStored == null && rs.getNumRecords()==0) {
			       userStored = new LoginUserSDS();
			       HealthCollectorMIDlet.setLoginUser(userStored);
			       userStored.setPassword(password);
			       userStored.setUserName(username);
			       userStored.setAuthorityID(authorityID);
					byte [] userdata = userStored.toByteArray();
						 int recId =	rs.addRecord(userdata, 0, userdata.length);
						}
				
				closeAuthorizationDB(rs);
				
				} catch (IOException ioe)
					{
						HealthCollectorMIDlet.showErrorMessage("FEIL","Problemer med aksess til autorisasjons database; "+ioe.getMessage());
					}
				catch (RecordStoreNotOpenException e)
					{
						HealthCollectorMIDlet.showErrorMessage("FEIL","Autorisasjons database er ikke åpen; "+e.getMessage());
					} catch (RecordStoreFullException e)
					{
						HealthCollectorMIDlet.showErrorMessage("FEIL","Autorisasjons database er full; "+e.getMessage());
					} catch (RecordStoreException e)
					{
						HealthCollectorMIDlet.showErrorMessage("FEIL","Autorisasjons database feil ved aksess; "+e.getMessage());
					}
					
			return 0;
			}
		
		public void actionPerformed(ActionEvent ae)
			{
				Command c = ae.getCommand();
           	
				if (c == cmdLogin) {
					   performLogin();
					   
				} else
				if (c == cmdExit)
					parentMIDlet.stopApplication();

			}

		private void performLogin()
			{
			     StringBuffer loginStatus = new StringBuffer();
	             boolean proceedWithLogin = false;
	             
	             String username = tfUsername.getText();
	             String password = tfPassword.getText();
				 String authorityID = tfAuthorityID.getText();
				
			int status = saveLoginUser(username,password, authorityID);
			if (status == -1)
				return;
			
			LoginUserSDS user = HealthCollectorMIDlet.getLoginUser();
			
				   cloudDB = new MicrosoftSDS(
						   				HealthCollectorMIDlet.getIMEI(),
						   				user.getAuthorityID(),
						   				user.getUserName(),
						   				user.getPassword());
				   
				   loginStatus.append(cloudDB.getServiceName()+"\n");
				   loginStatus.append(cloudDB.getServiceAddress()+"\n");
			
				   HttpResponse hResponse = null;
				   try
					{
					    hResponse = cloudDB.checkAccess();
					} catch (IOException e)
					{
						HealthCollectorMIDlet.showErrorMessage("FEIL", "Klarte ikke å sjekke aksess til database-tjenesten; "+e.getMessage());
					}
				   
					if (hResponse != null)
						{
							if (hResponse.getCode() == HttpsConnection.HTTP_OK) {
									{ 
										loginStatus.append("Velykket innlogging!");
										proceedWithLogin = true;					
									}
							} else 
								loginStatus.append("Kan ikke logge inn; "+hResponse.getMessage());
						} else

							HealthCollectorMIDlet.showErrorMessage("FEIL", "Udefinert respons fra database-tjenesten");
					
					// Update UI
					tfLoginStatus.setText(loginStatus.toString());
					
					if (proceedWithLogin) {
						try
							{
								Thread.sleep(500); // Allow some time to show login succeeded!
							} catch (InterruptedException e)
							{
								// TODO Auto-generated catch block
								//e.printStackTrace();
							}
						FormMainMenu menuScr = new FormMainMenu("Hoved Meny",parentMIDlet);
					    menuScr.show();	
					}
			}

		

	}
