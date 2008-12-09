package hks.itprojects.healthcollector.UI;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.animations.*;
import com.sun.lwuit.layouts.*;

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

			}

		public void actionPerformed(ActionEvent ae)
			{
				Command c = ae.getCommand();

				if (c == cmdLogin)
					if (authenticateUser())
						parentMIDlet.menuScr.show();

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
