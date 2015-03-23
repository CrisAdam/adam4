package com.adam4.SFA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.adam4.common.Common;
import com.adam4.irc.IRC;
import com.adam4.irc.ParsedMessage;
import com.adam4.mylogger.MyLogger;
import com.adam4.mylogger.MyLogger.LogLevel;

public class ClientHandler implements Runnable
{
	Socket clientSocket;
	boolean loggedIn = false;
	boolean inGame = false;
	String password;

	ClientHandler(Socket connection)
	{
		clientSocket = connection;
		
	}

	public void run()
	{

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
				if (unparsedMessage.isEmpty())
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

			String switchStr = message.command.toUpperCase();
			
			switch (switchStr)	// requires java 7 to switch on string
			{
				case "USER": // connect
				{
					connect(message);
					break;
				}
				case "d": // disconnect
				{
					disconnect("");
					break;
				}
				case "e": // enter game
				{
					if (loggedIn)
					{
						enter(message);
					}
					break;
				}
				case "i": // game input
				{
					if (loggedIn && inGame)
					{
						recieveInput(message);
					}
					break;
				}
				case "j": // join (new account)
				{
					join(message);
					break;
				}
				case "g": // get available games
				{
					getGames();
					break;
				}
				case "PASS": // connect
				{
					setPassword(message);
					break;
				}

				case "r": // toggle ready status
				{
					if (loggedIn && inGame)
					{
						ready(message);
					}
					break;
				}

				case "s": // select ship
				{
					if (loggedIn && inGame)
					{
						selectShip(message);
					}
					break;
				}
				case "u": // update info
				{
					updateInfo(message);
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

	private void setPassword(ParsedMessage message) 
	{
		password = message.args[0];
	}

	private void connect(ParsedMessage message)
	{
		//TODO: fix the [params
		String[] params = message.command.split(Common.SEPARATOR);
		// 1 = name, 2 = password, 3 = clientType, 4 = clientVersion
		String playerName = params[1];
		String password = params[2] + playerName;
		String version = params[4];
		if (!Common.isGoodUserName(playerName))
		{
			Network.sendError(clientSocket, "bad user name: " + playerName);
			Common.log
					.logMessage("bad user name: " + playerName, LogLevel.INFO);
			return;
		}
		
	//	if ()
	}

	public void disconnect(String reason)
	{
		loggedIn = false;
		inGame = false;
		Network.sendError(clientSocket, reason);
		try
		{
			clientSocket.close();
		}
		catch (IOException e)
		{
			Common.log.logMessage(e, MyLogger.LogLevel.INFO);
		}
	}

	private void enter(ParsedMessage message)
	{
		// Enter game function

	}

	private void getGames()
	{
		// TODO Auto-generated method stub

	}

	private void join(ParsedMessage message)
	{
		String[] params = message.command.split(Common.SEPARATOR);
		// 1 = name, 2 = password, 3 = clientType, 4 = clientVersion
		String playerName = params[1];
		String password = params[2] + playerName;
		String version = params[4];
		if (!Common.isGoodUserName(playerName))
		{
			System.out.println(message.args);
			System.out.println(params[0] + "  " + params[1] + "  " + params[3]);
			Network.sendError(clientSocket, "bad user name: " + playerName);
			Common.log
					.logMessage("bad user name: " + playerName, LogLevel.INFO);
			return;
		}
	}

	private void ready(ParsedMessage message)
	{
		// toggle ready status (must be in game)

	}

	private void recieveInput(ParsedMessage message)
	{
		// must be in game

	}

	private void selectShip(ParsedMessage message)
	{
		// must be in game

	}

	private void updateInfo(ParsedMessage message)
	{
		// update password or e-mail

	}

}
