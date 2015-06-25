

import java.io.IOException;
import javax.net.ssl.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;



public class Network
{

    private static AtomicBoolean acceptingNewClients;

    private static ClientListener clientListener;
    private static Thread clientListenerThread;


    Network()
    {
    	acceptingNewClients = new AtomicBoolean(true);
        clientListener = new ClientListener();
        clientListenerThread = new Thread(clientListener);
        clientListenerThread.start();
    }

    void acceptNewClients()
    {
        acceptingNewClients.set(true);
    }

    void stopAcceptingNewClients()
    {
        acceptingNewClients.set(false);
    }

    class ClientListener implements Runnable
    {

        ServerSocket serverSocket = null;

        ClientListener()
        {
            
        }

        @Override
        public void run()
        {
        	Thread.currentThread().setName("Network Client Main");
        	int port = 9090;
        	try
            {
        		
                serverSocket = new ServerSocket(port);
            }
            catch (Exception e)
            {
            	 System.out.println("Unable to listen to port " + port);
                System.exit(1);
                return;
            }
        	acceptNewClients();
            while (acceptingNewClients.get())
            {
                try
                {
                	serverSocket.setSoTimeout(1000);
                    Socket clientSocket = serverSocket.accept();
                    if (acceptingNewClients.get())
                    {
                    	new Thread(new Controller(clientSocket)).start();
                    }
                    else
                    {
                    	clientSocket.close();
                    }
                }
                catch (SocketTimeoutException e)
                {
                    ;
                    // do nothing; this is an exit due to SoTimeout such that it
                    // can check if it should still be listening or not
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
            }
        }

    }
    
 
    

}
