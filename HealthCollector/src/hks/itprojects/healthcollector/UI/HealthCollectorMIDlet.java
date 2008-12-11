package hks.itprojects.healthcollector.UI;

//Copyright (c) 2008 Henning Knut Skoglund
//
//Permission is hereby granted, free of charge, to any person
//obtaining a copy of this software and associated documentation
//files (the "Software"), to deal in the Software without
//restriction, including without limitation the rights to use,
//copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the
//Software is furnished to do so, subject to the following
//conditions:
//
//The above copyright notice and this permission notice shall be
//included in all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
//EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
//OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
//NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
//HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
//WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
//FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
//OTHER DEALINGS IN THE SOFTWARE.

// November 08: Started out with "a simple netclient" provided by Sun 2000-2001
// and expanded/rewrote it to generate a simple PHR service using Microsoft SQL
// data service

import javax.microedition.midlet.*;
import java.io.*;

import com.sun.lwuit.*;
import com.sun.lwuit.util.*;
import com.sun.lwuit.plaf.*;

public class HealthCollectorMIDlet extends MIDlet
	{

		private static String IMEI = null;
		private String platform = System.getProperty("microedition.platform");

		public static String getIMEI()
			{
				return IMEI;
			}

		public HealthCollectorMIDlet()
			{
				Display.init(this);

				getPhoneIMEI();

				initializeUITheme();

				login();

			}

		private void login()
			{
				FormLogin loginScr;
				// Login

				loginScr = new FormLogin("Innlogging", this);

				loginScr.show();
			}

		private void initializeUITheme()
			{
				try
					{

						Resources r = Resources.open("/businessTheme.res");
						UIManager.getInstance().setThemeProps(
								r.getTheme("businessTheme"));

					} catch (IOException ioe)
					{
						HealthCollectorMIDlet.showErrorMessage("FEIL","Klarte ikke å laste inn tema for brukergrensesnitt; "+ioe.getMessage());
					    stopApplication();
					}
			}

		private void getPhoneIMEI()
			{
				// The IMEI comined with date is used to uniquely identify a
				// health measurements entitites like blood pressure, wounds etc.

				try
					{
//						if (platform.startsWith("Nokia"))
//							IMEI = System.getProperty("com.nokia.mid.imei");
						if (platform.startsWith("SonyEricsson"))
							IMEI = System.getProperty("com.sonyericsson.imei");
//						if (platform.startsWith("Motorola"))
//							IMEI = System.getProperty("com.motorola.IMEI");

					} catch (Exception e)
					{
						showIMEInotFoundErrorAndStop(e.getMessage());
					}

				if (IMEI == null)
					showIMEInotFoundErrorAndStop("Udefinert IMEI for denne telefonen");
			}

		protected void startApp()
			{
			}

		private void showIMEInotFoundErrorAndStop(String msg)
			{
				showErrorMessage("FEIL",
						"Kan ikke nå tak i IMEI nummeret for platformen"
								+ platform+"; melding = "+msg);
			stopApplication();
			}

		protected void pauseApp()
			{
			}

		protected void destroyApp(boolean unconditional)
			{
			}

		public void stopApplication()
			{
				destroyApp(false);
				notifyDestroyed();
			}

		public static Image loadImage(String imageResource)
			{
				try
					{
						return Image.createImage(imageResource);
					} catch (IOException ioe)
					{
						showErrorMessage("FEIL",
								"Klarte ikke å åpne ressursen " + imageResource
										+ " feilmelding; " + ioe.getMessage());
						return null;
					}

			}

		public static void showErrorMessage(final String title,
				final String message)
			{

				final Image imgError = loadImage("/Error.png");

				final Command[] cmds = new Command[1];
				cmds[0] = new Command("OK");

				if (Display.getInstance().isEdt())
					Dialog.show(title, message, cmds, Dialog.TYPE_ERROR,
							imgError, 2000);
				else
					{
						Display.getInstance().callSerially(new Runnable()
							{

								public void run()
									{

										Dialog.show(title, message, cmds,
												Dialog.TYPE_ERROR, imgError,
												2000);

									}

							});
					}

			}

		public static void entitySaved(String msg)
			{
				Image imgSaved = loadImage("/Saved.png");
				Command[] cmds = new Command[1];
				cmds[0] = new Command("OK");

				Command cmdResult = Dialog.show("OK", msg, cmds,
						Dialog.TYPE_INFO, imgSaved, 2000);
			}

	}
