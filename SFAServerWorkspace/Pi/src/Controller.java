

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Controller implements Runnable
{
	Socket s;
	BufferedReader in;

	public Controller(Socket clientSocket)
	{
		s = clientSocket;

		try
		{
			in = new BufferedReader(new InputStreamReader(s.getInputStream(),
					StandardCharsets.UTF_8.newDecoder()));
		} catch (IOException e)
		{
			try
			{
				s.close();
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		Thread.currentThread().setName("Client thread " + s.getInetAddress());
		while (!s.isClosed())
		{
			String m = "";
			try
			{
				while ((m = in.readLine()) != null)
				{
					char[] c = m.toCharArray();
					if (c.length == 2)
					{
						//System.out.println(m);
						switch (c[0])
						{
						case '0': // turn off left motor
							Receiver.out.println("gpio write 0 0");
							Receiver.out.println("gpio write 1 0");
							break;
						case '1': // spin left motor forward
							Receiver.out.println("gpio write 0 1");
							Receiver.out.println("gpio write 1 0");
							break;
						case '2': // spin left motor backwards
							Receiver.out.println("gpio write 0 0");
							Receiver.out.println("gpio write 1 1");
							break;
						default:
							break;
						}
						switch (c[1])
						{
						case '0': // turn off right motor
							Receiver.out.println("gpio write 2 0");
							Receiver.out.println("gpio write 3 0");
							break;
						case '1': // spin right motor forward
							Receiver.out.println("gpio write 2 1");
							Receiver.out.println("gpio write 3 0");
							break;
						case '2': // spin right motor backwards
							Receiver.out.println("gpio write 2 0");
							Receiver.out.println("gpio write 3 1");
							break;
						default:
							break;
						}
						Receiver.out.flush();
						while ((m = Receiver.stdError.readLine()) != null)
						{
							System.out.println(m);
						}
						while ((m = Receiver.stdInput.readLine()) != null)
						{
							System.out.println(m);
						}
					}
					
				}
			} catch (IOException e)
			{
				try
				{
					s.close();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}

	}
}
