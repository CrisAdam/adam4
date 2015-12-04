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

		} catch (Exception e)
		{
		}
	}

	@Override
	public void run()
	{
		while (!s.isClosed())
		{
			String m = "";
			try
			{
				while ((m = in.readLine()) != null)
				{
					System.out.println("recieved " + m);
					switch (m)
					{
					case "f":
						Reciever.out.println("gpio write 0 1");
						Reciever.out.println("gpio write 1 0");
						Reciever.out.println("gpio write 2 1");
						Reciever.out.println("gpio write 3 0");
						System.out.println("set pins to forward");
						break;
					case "b":
						Reciever.out.println("gpio write 0 0");
						Reciever.out.println("gpio write 1 1");
						Reciever.out.println("gpio write 2 0");
						Reciever.out.println("gpio write 3 1");
						break;
					case "l":
						Reciever.out.println("gpio write 0 1");
						Reciever.out.println("gpio write 1 1");
						Reciever.out.println("gpio write 2 0");
						Reciever.out.println("gpio write 3 0");
						break;
					case "r":
						Reciever.out.println("gpio write 0 0");
						Reciever.out.println("gpio write 1 0");
						Reciever.out.println("gpio write 2 1");
						Reciever.out.println("gpio write 3 1");
						break;
					case "s":
						Reciever.out.println("gpio write 0 0");
						Reciever.out.println("gpio write 1 0");
						Reciever.out.println("gpio write 2 0");
						Reciever.out.println("gpio write 3 0");
						break;
					}
					Reciever.out.flush();

				}

			} catch (IOException e)
			{
				e.printStackTrace();
			}

		}

	}

}
