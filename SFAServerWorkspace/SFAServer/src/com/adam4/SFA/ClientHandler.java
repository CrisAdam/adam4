package com.adam4.SFA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.adam4.common.Common;
import com.adam4.irc.IRC;
import com.adam4.irc.ParsedMessage;
import com.adam4.mylogger.MyLogger;
import com.adam4.mylogger.MyLogger.LogLevel;

public class ClientHandler implements Runnable
{
	Socket clientSocket;
	int currentGame = 0;  // 0 means not in game
	int clientID = 0;  // 0 = not logged in
	String password, user, nick;
	PrintWriter output;
	
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
		Common.log.logMessage("client handler created and ran " + clientSocket.getInetAddress(), MyLogger.LogLevel.INFO);
	
		try
		{
			input = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream(),
					StandardCharsets.UTF_8.newDecoder()));
			output = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
		}
		catch (IOException e)
		{
			Common.log.logMessage(e, MyLogger.LogLevel.ERROR);
		}
	//	while (!clientSocket.isClosed())
		{
			try
			{
				while((unparsedMessage = input.readLine()) != null)
				{
					System.out.println("ClientHandler Recieved: " + unparsedMessage);
					handleMessage(IRC.parseLine(unparsedMessage));
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
			
			
			
		}
		Common.log.logMessage("client handler closing: " + clientSocket.getInetAddress(), MyLogger.LogLevel.INFO);
	}

	private void handleMessage(ParsedMessage message)
	{
		
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
			case "QUIT":
			{
				quit(message.trailing);
				break;
			}
			


			default:
			{
				Common.log.logMessage("unsupported message type" + message.toString(), MyLogger.LogLevel.ERROR);
				break;
			}
		}
	}


	private void ping(ParsedMessage message) 
	{
		sendMessage(new ParsedMessage("PONG", message.args,message.trailing));
	}

	private void pong(ParsedMessage message) 
	{
		// TODO Auto-generated method stub
		
	}

	private void register(ParsedMessage message)
	{
		// REGISTER email requestedNick password
		if (message.args.length < 3)
		{
			sendError("not enough parameters recieved, expecting: email, nickname, password");
		}
		else if(!Common.isValidEmailString(message.args[0]))
		{
			sendError("invalid email syntax: " + message.args[0]);
		}
		else if(!Common.isGoodUserName(message.args[1]))
		{
			sendError("invalid username: " + message.args[1]);
		}
		else if(message.args[2].length() < 3)
		{
			sendError("error: password length is too short: " + message.args[2].length() + " needs to be atleast 3 or greater");
		}
		else if (SFAServer.clientDatabasePool == null)
		{
			sendError("unable to accept registrations at this time");
		}
		else if(!SFAServer.clientDatabasePool.isConnected())
		{
			sendError("unable to process registrations at this time");
		}
		else
		{	
			String usernameCheckSQL = "SELECT `UsersTable`.`userID` FROM `SFASchema`.`UsersTable` where (email  = ? or nickname = ?)";
			String insertSQL = "INSERT INTO `SFASchema`.`UsersTable` (email, nickname, hashedpassword, salt) VALUES (?, ?, ?, ?)";
			String hashedpw = Common.hashPassword(password);
			Connection con = null;
			java.sql.PreparedStatement prep = null;
			try 
			{
				// check for username/nickname already in use
				
				con = SFAServer.clientDatabasePool.getConnection();
				prep = con.prepareStatement(usernameCheckSQL);
				prep.setString(1, message.args[0]);
				prep.setString(2, nick);
				ResultSet rs = prep.executeQuery();
				if (rs.first()) 
				{
					// username/nick is taken available
					sendError("Error: username or nick is unavailable");
					return;
				}
				prep.close();
				
				prep = con.prepareStatement(insertSQL);
				prep.setString(1, message.args[0]);
				prep.setString(2, message.args[1]);
				prep.setString(3, hashedpw);
				prep.setString(4, Common.generateRandomString(10));
				if (prep.executeUpdate() == 1)
				{
					
				}
				try 
				{
					// TODO: create unique hash, send email with hash to verify account
					Runtime.getRuntime().exec("ls");
				} 
				catch (IOException e) 
				{
					sendError("account created, however a validation error has occured");
					Common.log.logMessage(e, LogLevel.ERROR);
				}
			} 
			catch (SQLException e) 
			{
				sendError("an error has occured");
				Common.log.logMessage(e, LogLevel.ERROR);
				
			}
			finally 
			{
				if (prep != null) 
				{
					try 
					{
						prep.close();
					} 
					catch (SQLException e) 
					{
						Common.log.logMessage(e, LogLevel.ERROR);
					}
				}
				if (con != null) 
				{
					SFAServer.clientDatabasePool.returnConnection(con);
				}
			}
		}
		
	}
	
	private void user(ParsedMessage message) 
	{
		// USER email 
		// USER email nickname password salt (send to e-mail, used for verification)
		if (clientID != 0)
		{
			sendError("Error: already logged on");
		}
		else if (password == "")
		{
			sendError("no password recieved");
		}
		else if (!Common.isGoodUserName(nick))
		{
			sendError("bad nickname");
		}
		else if (user == "")
		{
			sendError("no user recieved");
		}
		else if (SFAServer.clientDatabasePool != null && SFAServer.clientDatabasePool.isConnected())
		{
			// TODO finish login checks
			String loginCheckSQL = "SELECT `UsersTable`.`userID` FROM `SFASchema`.`UsersTable` where (email  = ? and nickname = ?)";
		//	String hashedpw = Common.hashPassword(password);
			Connection con = null;
			java.sql.PreparedStatement prep = null;
			
			// TODO: check that email/user don't already exist
			
			try 
			{
				con = SFAServer.clientDatabasePool.getConnection();
				prep = con.prepareStatement(loginCheckSQL);
				prep.setString(1, message.args[0]);
				prep.setString(2, message.args[1]);
			//	prep.setString(3, hashedpw);
				ResultSet rs = prep.executeQuery();
				if (!rs.first() ) 
				{
					// unable to login, account not found
					sendError("Error: invalid login credentials");
				}
				else
				{
					String meta = "";
					for (int i = rs.getMetaData().getColumnCount(); i > 0; --i)
					{
						meta += rs.getMetaData().getColumnName(i) + " " + rs.getMetaData().getColumnType(i) + '\n';
					}
					clientID = rs.getInt(1);
					Common.log.logMessage(meta, LogLevel.INFO);
					//clientID = rs.getInt(0);
					motd();
					// TODO: update last login date to current
					rs.close();
				}
				

			} 
			catch (SQLException e) 
			{
				sendError("a login error has occured");
				Common.log.logMessage(e, LogLevel.ERROR);
				
			}
			finally 
			{
				if (prep != null) 
				{
					try 
					{
						prep.close();
					} 
					catch (SQLException e) 
					{
						Common.log.logMessage(e, LogLevel.ERROR);
					}
				}
				if (con != null) 
				{
					SFAServer.clientDatabasePool.returnConnection(con);
				}
			}
		}
		else
		{
			Common.log.logMessage("unable to validate login", LogLevel.ERROR);
			clientID = 1;
			motd();
		}
		SFAServer.connectedClients.add(this);
		
		
	}
	
	private void ready(ParsedMessage message) 
	{
		if (currentGame == 0)
		{
			sendError("unable to process ready state, client is not in game");
		}
		else
		{
			// TODO: finish
		}
		
	}
	
	private void motd() 
	{
		
		if (clientID != 0)
		{
			sendMessage(new ParsedMessage("MOTD", "Welcome " + clientID + " to SFAServer!"));
		}
		else
		{
			sendError("Not logged in");
		}
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
		sendMessage(new ParsedMessage("NOTICE", notice));
	}

	
	private void sendError(String error)
    {

        if (error.isEmpty())
        {
            error = "empty error";
        }
        if (!error.substring(0, 5).equals("ERROR "))
        {
            error = "ERROR " + error;
        }
        if (error.charAt(error.length() - 1) != '\n')
        {
            error += '\n';
        }
        output.write(error);
        output.flush();
    }
    
    private void sendMessage(ParsedMessage message)
    {
    	System.out.println("Server sent: " + message);
        output.write(message + "\n");
        output.flush();
    }

}
