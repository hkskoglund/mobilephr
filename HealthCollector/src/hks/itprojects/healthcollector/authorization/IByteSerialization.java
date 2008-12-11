package hks.itprojects.healthcollector.authorization;

import java.io.IOException;

public interface IByteSerialization
	{

		public abstract void fromByteArray(byte[] data) throws IOException;

		public abstract byte[] toByteArray() throws IOException;

	}