package com.adam4.SFA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.adam4.common.Common;
import com.adam4.mylogger.MyLogger;
import com.adam4.mylogger.MyLogger.LogLevel;

public class ClientHandler implements Runnable
{
	Socket clientSocket;
	Client c;
	boolean loggedIn = false;
	boolean inGame = false;

	ClientHandler(Socket connection, Client client)
	{
		clientSocket = connection;
		c = client;
	}

	public void run()
	{

		BufferedReader input = null; // not sure if I need the buffering, but
										// having the getLine() is nice
		String message = "";

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
				message = input.readLine();
				if (message.isEmpty())
				{
					continue;
				}
			}
			catch (IOException e)
			{
				message = "error";
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

			char switchChar = message.charAt(0);
			switch (switchChar)
			{
				case 'c': // connect
				{
					connect(message);
					break;
				}
				case 'd': // disconnect
				{
					disconnect();
					break;
				}
				case 'e': // enter game
				{
					if (loggedIn)
					{
						enter(message);
					}
					break;
				}
				case 'i': // game input
				{
					if (loggedIn && inGame)
					{
						recieveInput(message);
					}
					break;
				}
				case 'j': // join (new account)
				{
					join(message);
					break;
				}
				case 'g': // get available games
				{
					getGames();
					break;
				}

				case 'r': // toggle ready status
				{
					if (loggedIn && inGame)
					{
						ready(message);
					}
					break;
				}

				case 's': // select ship
				{
					if (loggedIn && inGame)
					{
						selectShip(message);
					}
					break;
				}
				case 'u': // update info
				{
					updateInfo(message);
					break;
				}

				default:
				{
					Common.log.logMessage(
							"unsupported message type" + message.charAt(0),
							MyLogger.LogLevel.ERROR);
					break;
				}
			}
		}
	}

	private void connect(String message)
	{
		String[] params = message.split(Common.SEPARATOR);
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

	public void disconnect()
	{
		loggedIn = false;
		inGame = false;
		try
		{
			clientSocket.close();
		}
		catch (IOException e)
		{
			Common.log.logMessage(e, MyLogger.LogLevel.INFO);
		}
	}

	private void enter(String message)
	{
		// Enter game function

	}

	private void getGames()
	{
		// TODO Auto-generated method stub

	}

	private void join(String message)
	{
		String[] params = message.split(Common.SEPARATOR);
		// 1 = name, 2 = password, 3 = clientType, 4 = clientVersion
		String playerName = params[1];
		String password = params[2] + playerName;
		String version = params[4];
		if (!Common.isGoodUserName(playerName))
		{
			System.out.println(message);
			System.out.println(params[0] + "  " + params[1] + "  " + params[3]);
			Network.sendError(clientSocket, "bad user name: " + playerName);
			Common.log
					.logMessage("bad user name: " + playerName, LogLevel.INFO);
			return;
		}
	}

	private void ready(String message)
	{
		// toggle ready status (must be in game)

	}

	private void recieveInput(String message)
	{
		// must be in game

	}

	private void selectShip(String message)
	{
		// must be in game

	}

	private void updateInfo(String message)
	{
		// update password or e-mail

	}

}