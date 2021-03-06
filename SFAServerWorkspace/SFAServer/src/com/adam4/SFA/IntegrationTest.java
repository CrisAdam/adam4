package com.adam4.SFA;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IntegrationTest 
{
	public static void main(String args[] ) throws Exception
	{
		new Thread(new ServerRunner()).start();
		TestClient.main(new String[]{"Localhost"});
		final Path watchedFile = Paths.get(SFAServer.runFilePath).toAbsolutePath();
		Files.deleteIfExists(watchedFile);
	}
	

	static class ServerRunner implements Runnable
	{
		ServerRunner()
		{
			
		}

		@Override
		public void run() 
		{
			try {
				SFAServer.main(new String[]{"-c"});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
