package com.adam4.SFA;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.adam4.common.Point;

/*
 * This class is intended to hold all the client data such as username, password, etc. 
 */

public class Client
{
    private ClientHandler clientHandler;
    public String password, nick, user;

    ConcurrentLinkedQueue<InputCommands> inputCommands;

    Client(ClientHandler handler)
    {
    	clientHandler = handler;
    }

    boolean logIn()
    {
    	// TODO: add security
    	if (password != null && nick != null && user != null)
    	{
    		return false;
    	}
        return true;
    }

    void disconnect(String reason)
    {
    	clientHandler.disconnect(reason);
    }

    public Player getPlayer()
    {
        // TODO Auto-generated method stub
        return null;
    }

} // end Player

class InputCommands
{
    Point screenCenter; // view center
    Point targetLocation; // mouse position
    char[] changedKeys;
}