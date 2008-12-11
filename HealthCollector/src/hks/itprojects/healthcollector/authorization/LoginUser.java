package hks.itprojects.healthcollector.authorization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LoginUser implements IByteSerialization
	{

		private String userName;
		private String password;
			
		public void setUserName(String userName)
			{
					this.userName = userName;
			}
		
		public String getUserName()
			{
					return userName;
			}
		
		public void setPassword(String password)
			{
					this.password = password;
			}
		
		public String getPassword()
			{
					return password;
			}
		
		
		// From http://developers.sun.com/mobility/midp/articles/databasemap/
		// Accessed : 11 december 2008
		
		/* (non-Javadoc)
		 * @see hks.itprojects.healthcollector.authorization.IByteSerialization#fromByteArray(byte[])
		 */
		public void fromByteArray( byte[] data ) throws IOException {
		    ByteArrayInputStream bin = new ByteArrayInputStream(data);
		    DataInputStream din = new DataInputStream( bin );

		    userName = din.readUTF();
		    password = din.readUTF();

		    din.close();
		}

		/* (non-Javadoc)
		 * @see hks.itprojects.healthcollector.authorization.IByteSerialization#toByteArray()
		 */
		public byte[] toByteArray() throws IOException {
		    ByteArrayOutputStream bout = new ByteArrayOutputStream();
		    DataOutputStream dout = new DataOutputStream( bout );

		    dout.writeUTF( getUserName() );
		    dout.writeUTF( getPassword() );

		    dout.close();

		    return bout.toByteArray();
		}

		
	}
