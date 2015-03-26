package com.adam4.SFA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import com.adam4.common.Common;
import com.adam4.irc.IRC;
import com.adam4.irc.ParsedMessage;
import com.adam4.mylogger.MyLogger;

public class ClientHandler implements Runnable
{
	Socket clientSocket;
	int currentGame = 0;  // 0 means not in game
	int clientID = 0;  // 0 = not logged in
	String password, user, nick;

	ClientHandler(Socket connection)
	{
		clientSocket = connection;
		
	}

	public void run()
	{
		Thread.currentThread().setName("Client Thread " + clientSocket.getLocalAddress());
		BufferedReader input = null; // not sure if I need the buffering, but
										// having the getLine() is nice
		String unparsedMessage ="";
		ParsedMessage message = new ParsedMessage("Error", "unsupported message recieved");

		try
		{
			input = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream(),
					StandardCharsets.UTF_8.newDecoder()));
		}
		catch (IOException e)
		{
			Common.log.logMessage(e, MyLogger.LogLevel.ERROR);
		}
		while (!clientSocket.isClosed())
		{
			try
			{
				unparsedMessage = input.readLine();
				if (unparsedMessage == null || unparsedMessage.isEmpty())
				{
					continue;
				}
				else
				{
					message = IRC.parseLine(unparsedMessage);
				}
			}
			catch (IOException e)
			{
				try
				{
					clientSocket.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
				Common.log.logMessage(e, MyLogger.LogLevel.ERROR);
			}

			System.out.println("recieved: " + message.toString());
			
			String switchStr = message.command.toUpperCase();
			
			switch (switchStr)	// requires java 7 to switch on string
			{
				case "PING":
				{
					ping(message);
					break;
				}	
				case "PONG":
				{
					pong(message);
					break;
				}
				case "REGISTER": // join (new account)
				{
					register(message);
					break;
				}
				case "PASS": 
				{
					password = message.args[0];
					break;
				}
				case "NICK": // set nickname
				{
					nick = message.args[0];
					break;
				}
				case "USER": // connect
				{
					user(message);
					break;
				}
				case "READY":
				{
					ready(message);
					break;
				}
				case "MOTD":
				{
					motd();
					break;
				}
				


				default:
				{
					Common.log.logMessage(
							"unsupported message type" + message.toString(),
							MyLogger.LogLevel.ERROR);
					break;
				}
			}
		}
	}







	private void ping(ParsedMessage message) 
	{
		Network.sendMessage(clientSocket, new ParsedMessage("PONG", message.trailing));
	}

	private void pong(ParsedMessage message) 
	{
		// TODO Auto-generated method stub
		
	}

	private void register(ParsedMessage message)
	{
		//TODO: finish this
	}
	
	private void user(ParsedMessage message) 
	{
		if (password != "")
		{
			Network.sendError(clientSocket, "no password recieved");
		}
		else if (!Common.isGoodUserName(nick))
		{
			Network.sendError(clientSocket, "bad nickname");
		}
		else if (user != "")
		{
			Network.sendError(clientSocket, "no user recieved");
		}
		else
		{
			Network.sendMOTD(clientSocket);
		}
		if (SFAServer.clientDatabasePool.isConnected())
		{
			// TODO finish login checks
			try 
			{
				SFAServer.clientDatabasePool.getConnection().prepareStatement("sql");
			}
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		SFAServer.connectedClients.add(this);
		
		
	}
	
	private void ready(ParsedMessage message) 
	{
		if (currentGame == 0)
		{
			Network.sendError(clientSocket, "unable to process ready state, client is not in game");
		}
		else
		{
			// TODO: finish
		}
		
	}
	
	private void motd() 
	{
		Network.sendMOTD(clientSocket);
	}

	public void quit(String reason)
	{
		clientID = 0;
		currentGame= 0;
		
		for (Game g : SFAServer.games)
		{
			if (g.gameID == currentGame)
			{
				for (ClientHandler c : g.clients)
				{
					//  send message to self in case of server-caused reason
					c.notice(nick + " has quit: " + reason);
				}
			}
		}
		
		SFAServer.connectedClients.remove(this);
		
		while (!clientSocket.isClosed())
		{
			try
			{
				clientSocket.close();
			}
			catch (IOException e)
			{
				Common.log.logMessage(e, MyLogger.LogLevel.INFO);
			}
		}
	}

	private void notice(String notice) 
	{
		Network.sendMessage(clientSocket, new ParsedMessage("NOTICE", notice));
	}


}
