package hks.itprojects.healthcollector.UI;

import java.io.IOException;

import hks.itprojects.healthcollector.authorization.LoginUser;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.animations.*;
import com.sun.lwuit.layouts.*;
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

		// Commands
		private Command cmdExit;
		private Command cmdLogin;

		private HealthCollectorMIDlet parentMIDlet = null;
		
		// RMS
		private RecordStore rs = null;
		private LoginUser user = null;

		public FormLogin(String title, HealthCollectorMIDlet parentMIDlet)
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
						if (rs.getNumRecords() == 1) {
				            // Retrive user from DB
							user = new LoginUser();
							user.fromByteArray(rs.getRecord(0));
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
               // Case 1 : User already stored
				
				openAuthorizationDB();
				
				try {
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
				
				if (user == null && rs.getNumRecords()==0) {
			       user = new LoginUser();
			       user.setPassword(password);
			       user.setUserName(username);
					byte [] userdata = user.toByteArray();
							rs.addRecord(userdata, 0, userdata.length);
						}
				
				closeAuthorizationDB();
				
				} catch (IOException ioe)
					{
						// TO DO
					}
				catch (RecordStoreNotOpenException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RecordStoreFullException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RecordStoreException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
		
		public void actionPerformed(ActionEvent ae)
			{
				Command c = ae.getCommand();

				if (c == cmdLogin) {
					   saveLoginUser(tfUsername.getText(),tfPassword.getText());
					   FormMainMenu menuScr = new FormMainMenu("Hoved Meny",parentMIDlet);
				       menuScr.show();
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
