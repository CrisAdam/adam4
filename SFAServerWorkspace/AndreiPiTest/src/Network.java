import java.net.ServerSocket;
import java.net.Socket;

public class Network
{

	private static ClientListener clientListener;
	private static Thread clientListenerThread;

	Network()
	{
		clientListener = new ClientListener();
		clientListenerThread = new Thread(clientListener);
		clientListenerThread.start();
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

			try
			{
				serverSocket = new ServerSocket(9090);
				System.out.println("running network listener");
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			while (true)
			{
				try
				{
					serverSocket.setSoTimeout(1000);
					Socket clientSocket = serverSocket.accept();
					new Thread(new Controller(clientSocket)).start();
				} catch (Exception e)
				{
					//  e.printStackTrace();  // so timeout errors
				}
			}

		}

	}

}
