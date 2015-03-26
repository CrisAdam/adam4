package com.adam4.SFA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.adam4.irc.IRC;

public class TestClient
{
    static Socket s;
    static PrintWriter output;
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
            output = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true);
            
            input = s.getInputStream();
            
            new Thread(new Writer(s)).start();
            
            Scanner in = new Scanner(System.in);
            
            
         // send password
            output.write("PASS password \n");
           
         // send nick
            output.write("NICK testClient \n");
            
         // send user
            output.write("USER cristian@adam4.com javaTestClient v1.0.0 \n");
            
            output.flush();
            String consoleInput = "0";
            System.out.println("enter quit to exit");
            while (!consoleInput.equals("quit") && !s.isClosed())
            {
            	consoleInput = in.nextLine();
            	output.write(consoleInput + '\n');
            	output.flush();
            	 System.out.println("sent: " + consoleInput);
            }
            System.out.println("closed");
            
            
            


            s.close();
            in.close();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        
    }
    
    static class Writer implements Runnable
    {
    	String str;
    	Socket s;
    	BufferedReader input;
    	Writer(Socket s) throws IOException
    	{
    		this.s = s;
    		input = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8.newDecoder()));
    	}

		@Override
		public void run() 
		{
			System.out.println("TestClient reader running from " + s.getRemoteSocketAddress());
			try {
				while((str = input.readLine()) != null)
				{
					System.out.println("TestClient Recieved: " + str);
				}
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
    
}