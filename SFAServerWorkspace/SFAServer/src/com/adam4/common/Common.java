package com.adam4.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.adam4.mylogger.MyLogger;
import com.adam4.mylogger.MyLogger.LogLevel;

public class Common
{
    public static final String SEPARATOR = (char) 31 + "";
    public static final String ENCODING = "UTF-8";
    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static MyLogger log = new MyLogger();

    public static boolean hasMajority(int votes, int max)
    {
        // assume votes and max are both >= 1
        return (((max - votes) - votes) < 0);
    }

    public static Timestamp getTime()
    {
        return new Timestamp(new Date().getTime());
    }

    public static String prefixColon(String s)
    {
        if (s.startsWith(":"))
        {
            return s;
        }
        return ':' + s;
    }
    
    private static SecureRandom random = new SecureRandom();
    public static String generateRandomString(int length)
    {
    	return new BigInteger(130, random).toString(32).substring(0, Math.min(length,  30));
    }

    public static String removeColons(String s)
    {
        return s.replaceAll(":", "");
    }

    public static String removeNewLine(String s)
    {
        return s.replaceAll("(\\r|\\n)", "");
    }

    public static String getHostName()
    {
        try
        {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
            log.logMessage(e, LogLevel.ERROR);
        }
        return "unknown host";

    }

    public static String readResourceFile(String fileName)
    {
        BufferedReader br;
        String line = null;
        StringBuilder output = new StringBuilder();
        try
        {

            br = new BufferedReader(new FileReader(System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + "resources" + FileSystems.getDefault().getSeparator() + fileName));
            while ((line = br.readLine()) != null)
            {
                output.append(line);
            }
            br.close();
        }
        catch (IOException e)
        {
            log.logMessage(e, LogLevel.WARN);
            return (fileName + " file not found");
        }
        return output.toString();
    }

    public static String replaceNewLines(String str)
    {
        return str.replaceAll("[\\t\\n\\r]", " ");
    }

    public static String hashPassword(String password)
    {
    	String output = "hash failure";
    	if (password.length() > 2)
    	{
    		MessageDigest digest;
            try
            {
                digest = MessageDigest.getInstance("SHA-512");

                for (int i = 0; i < 100000; i++) 
                {
                    digest.update(password.getBytes());
                }
                byte[] byteOutput = digest.digest(password.getBytes());
                BigInteger bigInt = new BigInteger(1, byteOutput);
                output = bigInt.toString(16);
                while (output.length() < 32)
                {
                    output += '!';
                }
            }
            catch (NoSuchAlgorithmException e)
            {
            	e.printStackTrace();
            }
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
    

    public static Boolean isValidEmailString(String input)
    {
	
    	boolean result = true;
    	   try {
    	      InternetAddress emailAddr = new InternetAddress(input);
    	      emailAddr.validate();
    	   } catch (AddressException ex) {
    	      result = false;
    	   }
    	   return result;
    }

    static String getSystem()
    {
        return System.getProperty("os.name");
    }

}
