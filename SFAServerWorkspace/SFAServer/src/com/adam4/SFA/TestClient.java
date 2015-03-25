package com.adam4.SFA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.adam4.SFA.Network.ClientListener;
import com.adam4.irc.ParsedMessage;

public class TestClient
{
    static Socket s;
    static OutputStream output;
    static InputStream input;
    public static void main(String[] args)
    {
    	
    	String URL = "54.172.235.102";
    	//String URL = "localhost";
        if (args.length > 0)
        {
            URL = args[0];
        }
        try
        {
            s = new Socket(URL, 9898);
            System.out.println("connected to: " + URL);
            output = s.getOutputStream();
            input = s.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8.newDecoder()));
            
            Scanner in = new Scanner(System.in);
            
            
         // send password
            output.write("PASS password".getBytes("UTF-8"));
           
         // send nick
            output.write("NICK testClient".getBytes("UTF-8"));
            
         // send user
            output.write("USER cristian@adam4.com javaTestClient v1.0.0".getBytes("UTF-8"));
            
            String consoleInput = "0";
            while (!consoleInput.equals("quit") && !s.isClosed())
            {
            	while (reader.ready())
            	{
            		System.out.println(reader.readLine());
            	}
            	consoleInput = in.nextLine();
            	output.write(consoleInput.getBytes("UTF-8"));
            	 System.out.println("sent: " + consoleInput);
            }
            System.out.println("closed");
            
            
            


            s.close();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        
    }
    
    
}