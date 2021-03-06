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

import com.adam4.common.Common;
import com.adam4.irc.IRC;
import com.adam4.irc.ParsedMessage;
import com.adam4.mylogger.ConsoleLogWriter;
import com.adam4.mylogger.MyLogger;

public class AI
{

	static Socket s;
	static PrintWriter output;
	static BufferedReader input;
	static int state = -1;
	
	public static void main(String[] args)
	{

		
		Common.log.addLogWriter(new ConsoleLogWriter());
		String URL = "54.172.235.102";
		// String URL = "localhost";
		if (args.length > 0)
		{
			URL = args[0];
		}
		try
		{
			s = new Socket(URL, 9898);
			System.out.println("connected to: " + URL);
			output = new PrintWriter(new OutputStreamWriter(
					s.getOutputStream(), StandardCharsets.UTF_8), true);

			input = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8.newDecoder()));
			output.write("PASS mypass \n");
			output.write("NICK TESTCLIENT \n");
			output.write("USER cristian@adam4.com AITestClient v1.0.0 \n");
			output.flush();
			String str;
			
			while (!s.isClosed())
			{
				while(input.ready())  //check for chars in buffer, assuming that they will end with an endline
				{
					handleMessage(IRC.parseLine(input.readLine()));
				}
				output.flush();
			}
			s.close();
			System.out.println("closed");
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private static void handleMessage(ParsedMessage msg)
	{
		System.out.println("Ai Recieved: " + msg.toString());
		String switchStr = msg.command.toUpperCase();
		
		switch (switchStr)	// requires java 7 to switch on string
		{
		case "PING":
		{
			ping(msg);
			break;
		}	
		case "PONG":
		{
			pong(msg);
			break;
		}
		case "ERROR":
		{
			error(msg);
			break;
		}
		case "LOBBY":
		{
			lobby(msg);
			break;
		}
		case "MOTD":
		{
			motd();
			break;
		}
		case "QUIT":
		{
			quit(msg);
			break;
		}
		case "GAME":
		{
			game(msg);
			break;
		}


		default:
		{
			Common.log.logMessage("unsupported message type" + msg.toString(), MyLogger.LogLevel.ERROR);
			break;
		}
		}
		
	}

	private static void game(ParsedMessage msg)
	{
		// TODO Auto-generated method stub
		
	}

	private static void quit(ParsedMessage msg)
	{
		// TODO Auto-generated method stub
		
	}

	private static void motd()
	{
		if (state == -1)
		{
			state = 0;
		}
		if (state == 0)
		{
			sendMessage(new ParsedMessage("LOBBY", new String[]{"JOIN"}));
		}
		
	}

	private static void lobby(ParsedMessage msg)
	{
		state = Integer.parseInt(msg.args[0]);
		sendMessage(new ParsedMessage("LOBBY", new String[]{"READY"}));
	}

	private static void error(ParsedMessage msg)
	{
		Common.log.logMessage(msg.toString(), MyLogger.LogLevel.ERROR);
	}
 
	private static void pong(ParsedMessage msg)
	{
		// TODO Auto-generated method stub
		
	}

	private static void ping(ParsedMessage msg)
	{
		sendMessage(new ParsedMessage("PONG", msg.args,msg.trailing));
	}

	private static void sendMessage(ParsedMessage message)
    {
        output.write(message + "\n");
        output.flush();
    }
	
}
