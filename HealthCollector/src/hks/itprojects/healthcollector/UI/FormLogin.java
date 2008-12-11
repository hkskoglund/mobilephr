package hks.itprojects.healthcollector.UI;

import java.io.IOException;

import hks.itprojects.healthcollector.REST.IRESTCLOUDDB;
import hks.itprojects.healthcollector.REST.MicrosoftSDS;
import hks.itprojects.healthcollector.authorization.LoginUser;
import hks.itprojects.healthcollector.network.HttpResponse;

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
public class FormLogin extends Form implements ActionListener
	{

		// Fields
		private TextArea tfPassword;
		private TextArea tfUsername;
		private TextArea tfLoginStatus;

		// Commands
		private Command cmdExit;
		private Command cmdLogin;

		
		// RMS
		private RecordStore rs = null;
		private LoginUser user = null;

		private HealthCollectorMIDlet parentMIDlet = null;
		
		private IRESTCLOUDDB cloudDB = null;
		private final String authorityID = "hks";
		
		public FormLogin(HealthCollectorMIDlet parentMIDlet)
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
				tfLoginStatus = new TextArea(null,2,20,TextArea.UNEDITABLE);
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

		private void getLoginAuthorization()
			{
					openAuthorizationDB();
				
			    try
					{
						int numrec = rs.getNumRecords();
						if (numrec == 1) {
				            // Retrive user from DB
							user = new LoginUser();
							user.fromByteArray(rs.getRecord(1));
							// Update UI
							tfUsername.setText(user.getUserName());
							tfPassword.setText(user.getPassword());
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
					
					closeAuthorizationDB();
			}

		private void closeAuthorizationDB()
			{
				try
					{
						rs.closeRecordStore();
					} catch (RecordStoreNotOpenException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RecordStoreException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		
		private void openAuthorizationDB()
			{
				try {
					rs = RecordStore.openRecordStore("Authorization", true, RecordStore.AUTHMODE_PRIVATE, true);
				} catch (RecordStoreException rse) {
					HealthCollectorMIDlet.showErrorMessage("FEIL","Problemer med tilgang til autorisasjons informasjon i RMS; "+rse.getMessage());
				}
			}
		
		private void saveLoginUser(String username, String password)
			{
				boolean update  = false;
				
				openAuthorizationDB();
				
				try {
		               // Case 1 : User already stored

					if (user != null)
					{
						if (!user.getUserName().equals(username)) {
							user.setUserName(username);
							update = true;
						}
						
						if (!user.getPassword().equals(password)) {
							user.setPassword(password);
							update = true;
						}
						
						if (update) {
							byte [] userdata = user.toByteArray();
									rs.setRecord(0, userdata, 0, userdata.length);
						}
					} else

			// Case 2 : User not previously stored
				if (user == null && rs.getNumRecords()==0) {
			       user = new LoginUser();
			       user.setPassword(password);
			       user.setUserName(username);
					byte [] userdata = user.toByteArray();
						 int recId =	rs.addRecord(userdata, 0, userdata.length);
						}
				
				closeAuthorizationDB();
				
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
			}
		
		public void actionPerformed(ActionEvent ae)
			{
				Command c = ae.getCommand();
                StringBuffer loginStatus = new StringBuffer();
                boolean proceedWithLogin = false;
				
				if (c == cmdLogin) {
					   saveLoginUser(tfUsername.getText(),tfPassword.getText());
					   cloudDB = new MicrosoftSDS(HealthCollectorMIDlet.getIMEI(),this.authorityID,user.getUserName(),user.getPassword());
					   loginStatus.append(cloudDB.getServiceName()+"\n");
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
				if (c == cmdExit)
					parentMIDlet.stopApplication();

			}

		// Code based on example from Chap. 12 Protecting Network Data -
		// Jonathan Knutsen
		private boolean authenticateUser()
			{
				// String user = tfUsername.getText();
				// String password = tfPassword.getText();
				//    	
				// byte[] passwordBytes = password.getBytes();
				// // Create the message digest.
				// Digest digest = new SHA1Digest();
				// // Calculate the digest value.
				// digest.update(passwordBytes, 0, passwordBytes.length);
				// byte[] digestValue = new byte[digest.getDigestSize()];
				// digest.doFinal(digestValue, 0);
				return true;
			}

	}
