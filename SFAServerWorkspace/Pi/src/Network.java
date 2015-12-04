import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Network
{

	private static AtomicBoolean acceptingNewClients;
	final static int port = 9090;
	private static LanClientListener lanClientListener;
	private static BTClientListener bTClientListener;
	private static Thread lanClientListenerThread, bTClientListenerThread;

	Network()
	{
		acceptingNewClients = new AtomicBoolean(true);
		lanClientListener = new LanClientListener();
		lanClientListenerThread = new Thread(lanClientListener);
		lanClientListenerThread.start();
	}

	void acceptNewClients()
	{
		acceptingNewClients.set(true);
	}

	void stopAcceptingNewClients()
	{
		acceptingNewClients.set(false);
	}
	
	class BTClientListener implements Runnable
	{
		ServerSocket serverSocket = null;

		BTClientListener()
		{

		}

		@Override
		public void run()
		{
			Thread.currentThread().setName("BT Server");

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
			try
			{
				serverSocket.setSoTimeout(1000);
			}
			catch (SocketException e1)
			{
				e1.printStackTrace();
			}
			while (acceptingNewClients.get())
			{
				try
				{
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

	class LanClientListener implements Runnable
	{

		ServerSocket serverSocket = null;

		LanClientListener()
		{

		}

		@Override
		public void run()
		{
			Thread.currentThread().setName("Lan Server");

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
			try
			{
				serverSocket.setSoTimeout(1000);
			}
			catch (SocketException e1)
			{
				e1.printStackTrace();
			}
			while (acceptingNewClients.get())
			{
				try
				{
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
