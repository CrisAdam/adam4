package com.adam4.SFA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.net.ssl.SSLSocket;

import com.adam4.common.Common;
import com.adam4.irc.IRC;
import com.adam4.irc.ParsedMessage;
import com.adam4.mylogger.MyLogger;
import com.adam4.mylogger.MyLogger.LogLevel;

public class ClientHandler implements Runnable
{
	Socket clientSocket;
	SSLSocket  clientSSLSocket;
	int currentGame = 0;  // 0 means not in game
	int clientID = 0;  // 0 = not logged in
	String password, user, nick;
	PrintWriter output;
	private final boolean ssl;
	
	ClientHandler(Socket connection)
	{
		clientSocket = connection;
		ssl = false;
	}
	
	ClientHandler(SSLSocket  connection)
	{
		clientSSLSocket = connection;
		ssl = true;
	}

	public void run()
	{
		Thread.currentThread().setName("Client Thread " + clientSocket.getLocalAddress());
		BufferedReader input = null; // not sure if I need the buffering, but
										// having the getLine() is nice
		
		String unparsedMessage ="";
		Common.log.logMessage("client handler created and ran " + clientSocket.getInetAddress(), MyLogger.LogLevel.INFO);
	
		try
		{
			if (ssl)
			{
				input = new BufferedReader(new InputStreamReader(
						clientSSLSocket.getInputStream(),
						StandardCharsets.UTF_8.newDecoder()));
				output = new PrintWriter(new OutputStreamWriter(clientSSLSocket.getOutputStream(), StandardCharsets.UTF_8), true);
			}
			else
			{
				input = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream(),
						StandardCharsets.UTF_8.newDecoder()));
				output = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
			}
			
		}
		catch (IOException e)
		{
			Common.log.logMessage(e, MyLogger.LogLevel.ERROR);
		}
		SFAServer.connectedClients.add(this);
		try
		{
			while((unparsedMessage = input.readLine()) != null)
			{
				handleMessage(IRC.parseLine(unparsedMessage));
			}
		}
		catch (IOException e)
		{
			try
			{
				if (ssl)
				{
					clientSSLSocket.close();
				}
				else
				{
					clientSocket.close();
				}
				
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			// will occur if client closes connection before issuing "quit"
			//Common.log.logMessage(e, MyLogger.LogLevel.INFO);
		}
		if (ssl)
		{
			Common.log.logMessage("client handler closing: " + clientSSLSocket.getInetAddress(), MyLogger.LogLevel.INFO);
		}
		else
		{
			Common.log.logMessage("client handler closing: " + clientSocket.getInetAddress(), MyLogger.LogLevel.INFO);
		}
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
				password(message);
				break;
			}
			case "NICK": // set nickname
			{
				
				nick(message);
				break;
			}
			case "USER": // connect
			{
				user(message);
				break;
			}
			case "LOBBY":
			{
				lobby(message);
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


	private void nick(ParsedMessage message) 
	{
		if (clientID == 0)
		{
			nick = message.args[0];
		}
		else
		{
			sendError("Already logged in");
		}
		
	}

	private void password(ParsedMessage message) 
	{
		if (clientID == 0)
		{
			password = message.args[0];
		}
		else
		{
			sendError("Already logged in");
		}
		
		
	}

	private void ping(ParsedMessage message) 
	{
		sendMessage(new ParsedMessage("PONG", message.args,message.trailing));
	}

	private void pong(ParsedMessage message) 
	{
		
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
			Connection con = null;
			java.sql.PreparedStatement prep = null;
			try 
			{
				// check for username/nickname already in use
				
				con = SFAServer.clientDatabasePool.getConnection();
				prep = con.prepareStatement(usernameCheckSQL);
				prep.setString(1, message.args[0]);
				prep.setString(2, message.args[1]);
				ResultSet rs = prep.executeQuery();
				if (rs.first()) 
				{
					// username/nick is taken
					sendError("Error: username or nick is unavailable");
					return;
				}
				prep.close();
				String salt = Common.generateRandomString(10);
				String hashedpw = Common.hashPassword(password + salt + SFAServer.getPepper());
				prep = con.prepareStatement(insertSQL);
				prep.setString(1, message.args[0]);
				prep.setString(2, message.args[1]);
				prep.setString(3, hashedpw);
				prep.setString(4, salt);
				if (prep.executeUpdate() == 1)
				{
						try 
						{
				//			String text = "Hello " + message.args[1] + ", \n Thank you for registering with SFA \n To validate this e-mail account, please copy the following into the verification box: \n \n  " + salt + " \n \n If you believe that you recieved this message in error, simply ignore it.";
				//			String[] ex = {"/bin/sh", "-c", 
								"echo \"" + text + "\" | mail -s \"SFA Registration\" " + message.args[0]};

							Common.log.logMessage("$" + ex + "$", LogLevel.DEBUG);
							
							Process p = Runtime.getRuntime().exec(ex);
							
							String email;
							BufferedReader stdInput = new BufferedReader(new
					                 InputStreamReader(p.getInputStream()));
					 
					            BufferedReader stdError = new BufferedReader(new
					                 InputStreamReader(p.getErrorStream()));
					 
					            // read the output from the command
					            while ((email = stdInput.readLine()) != null) 
					            {
					                System.out.println(email);
					            }
					            
					            while ((email = stdError.readLine()) != null) 
					            {
					                System.out.println(email);
					            }
						}
						catch (IOException e) 
						{
							Common.log.logMessage(e, LogLevel.ERROR);
						}
				}
				else
				{
					sendError("an account validation error has occured");
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
		// USER email clientType clientVersion 
		// USER email clientType clientVersion verification
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

			if (message.args.length == 3 || message.args.length == 4 )
			{
				Connection con = null;
				ResultSet rs = null;
				java.sql.PreparedStatement prep = null;
				
				try
				{
					con = SFAServer.clientDatabasePool.getConnection();
					
					String loginCheckSQL = "SELECT `UsersTable`.`userID`, `UsersTable`.`hashedpassword`, `UsersTable`.`salt` FROM `SFASchema`.`UsersTable` WHERE nickname=\"" + nick + "\" OR email=\"" + message.args[0] + "\"";
					prep = con.prepareStatement(loginCheckSQL);
					rs = prep.executeQuery();
					if (!rs.first()) 
					{
						sendError("Error: unable to find account");
						return;
					}
					String DBhashedPassword = rs.getString(2);
					String salt = rs.getString(3);
					if (message.args.length == 3)
					{

						// BEGIN NORMAL USER LOGIN
						
						if (salt.length() > 12)  // check that user has validated
						{
							if (java.security.MessageDigest.isEqual(Common.hashPassword(password + salt + SFAServer.getPepper()).getBytes(), DBhashedPassword.getBytes()))
							{
								clientID = rs.getInt(1);
								motd();
								// Successful login, given password matches expected password
								
								String updateSQL = "UPDATE `SFASchema`.`UsersTable` SET lastSuccessfulLogin=\"" + Common.getTime() +  "\" WHERE userID=\"" + clientID + "\"";
								prep.close();
								prep = con.prepareStatement(updateSQL);
								if (prep.executeUpdate() != 1)
								{
									Common.log.logMessage("unable to update last login date: " + message.args[0] + " " + nick, LogLevel.ERROR);
								}
							}
							else
							{
								sendError("Error: unsuccessful login attempt");
								// TODO: log unsuccessful login attempt, restrict retries
							}
						}
						else
						{
							sendError("Error: account needs to be validated first");
						}
						
					}
					else
					{
						// BEGIN VALIDATE USER
						if (salt.length() > 12)
						{
							sendError("Error: account has already been validated");
							return;
						}
						if (!salt.equals(message.args[3]))
						{
							sendError("Error: invalid verifier");
							return;
							// TODO: prevent repeated guesses at verifier
						}
						if (java.security.MessageDigest.isEqual(Common.hashPassword(password + salt + SFAServer.getPepper()).getBytes(), DBhashedPassword.getBytes()))
						{
							clientID = rs.getInt(1);
							motd();
							salt = Common.generateRandomString(20);
							String updateSQL = "UPDATE `SFASchema`.`UsersTable` SET hashedpassword=\"" + Common.hashPassword(password + salt + SFAServer.getPepper()) + "\", salt=\"" + salt + "\", lastSuccessfulLogin=\"" + Common.getTime() +  "\" WHERE userID=\"" + clientID + "\"";
							rs.close();
							prep.close();
							prep = con.prepareStatement(updateSQL);
							System.out.println(updateSQL + "   " + salt + "     " + Common.hashPassword(password + salt + SFAServer.getPepper()));
							if (prep.executeUpdate() != 1)
							{
								sendError("Error: unable to validate");
								Common.log.logMessage("Validate user error: " + message.args[0] + " " + nick, LogLevel.ERROR);
							}
						}
						else
						{
							sendError("Error: unsuccessful login attempt");
							// TODO: log unsuccessful login attempt, restrict retries
						}

					}
				}
				catch (SQLException e) 
				{
					sendError("a login error has occured");
					Common.log.logMessage(e, LogLevel.ERROR);
				}
				finally // this should get called even if the try reaches a "return"
				{
					if (rs != null)
					{
						try 
						{
							rs.close();
						} 
						catch (SQLException e) 
						{
							Common.log.logMessage(e, LogLevel.ERROR);
						}
					}
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
				sendError("Error: invalid number of arguments");
			}	
		}
		else // database connection is down
		{
			Common.log.logMessage("unable to validate login", LogLevel.ERROR);
		}
		
		
	}
	
	private void lobby(ParsedMessage message) 
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
		
		if (ssl)
		{
			while (!clientSSLSocket.isClosed())
			{
				try
				{
					clientSSLSocket.close();
				}
				catch (IOException e)
				{
					Common.log.logMessage(e, MyLogger.LogLevel.INFO);
				}
			}
		}
		else
		{
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
		
	}

	private void notice(String notice) 
	{
		sendMessage(new ParsedMessage("NOTICE", notice));
	}

	
	private void sendError(String error)
    {

        if (error.isEmpty())
        {
            error = ":empty error";
        }
        if (!error.substring(0, 5).equals("ERROR "))
        {
            error = "ERROR " + Common.prefixColon(error);
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
        output.write(message + "\n");
        output.flush();
    }

}
