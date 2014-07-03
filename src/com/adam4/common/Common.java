package com.adam4.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.adam4.SFA.Point;
import com.adam4.mylogger.MyLogger;
import com.adam4.mylogger.MyLogger.LogLevel;

public class Common
{
	public static final String SEPARATOR = (char) 31 + "";
	public static final String ENCODING = "UTF-8";
	public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	

	
    public static Date getTime()
    {
    	return new Date(new Date().getTime() - TimeZone.getDefault().getRawOffset());
    }
    
	public static String hashPassword(String password) throws NoSuchAlgorithmException
	{
		String output = "hash failure";
		MessageDigest digest = MessageDigest.getInstance("SHA-512");
		for (int i = 0; i < 50000; i++)
		{
			digest.update(password.getBytes());
			digest.update(new String("Saltd2815980fcb9635bc8a972f1902e1f1c18be889bf79b5c72372ca37730df9ab3cab5ad983860b80501007325eba2784d4a97814bedfa95e73259a1179733").getBytes());
		}
		byte[] byteOutput = digest.digest(password.getBytes());
		BigInteger bigInt = new BigInteger(1, byteOutput);
		output = bigInt.toString(16);
		while (output.length() < 32)
		{
			output += '!';
		}

		return output;
	}

	public static Boolean isGoodUserName(String input)
	{
		if (2 > input.length() || input.length() > 20)
		{
			return false;
		}
		return !input.matches("^.*[^a-zA-Z0-9].*$");
	}
	
	static String getSystem()
	{
		return System.getProperty("os.name");
	}
	

	
}
