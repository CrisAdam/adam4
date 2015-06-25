

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Controller implements Runnable
{
	/*
	 * 				Pins	
	 * 	3v3 power	1	2	5v power	| corner
	 *************  3	4	5v power	| edge
	 Board	*****	5	6	Ground		| edge
	************** 	7	8
	 		Ground	9	10
	**********	0 	11	12	1
	*********** 2	13	14
	************3 	15	16
	************ 	17	18
	************ 	19	20
	************ 	21	22
	************ 	23	24
	************ 	25	26
	************ 	27	28
	************ 	29	30
	************ 	31	32
	************ 	33	34
	************ 	35	36
	************ 	37	38
	************ 	39	40
	 * 

	pin 0 = left motor on
	pin 1 = left motor reverse
	pin 2 = right motor on
	pin 3 = right motor reverse

	gpio mode n out // set it as an output pin
	gpio pin n 1
	gpio pin n 0

	 * 
	 * 
	 */
	Socket s;
	BufferedReader in;
	Process p;
	PrintWriter out;

	public Controller(Socket clientSocket)
	{
		s = clientSocket;

		String[] ex = { "/bin/sh", "-c" };
		try
		{
			in = new BufferedReader(new InputStreamReader(s.getInputStream(),
					StandardCharsets.UTF_8.newDecoder()));
			p = Runtime.getRuntime().exec(ex);
			out = new PrintWriter(p.getOutputStream());
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
		while (!s.isClosed())
		{
			String m = "";
			try
			{
				while ((m = in.readLine()) != null)
				{
					char[] c = m.toCharArray();
					switch (c[0])
					{
					case '0': // turn off left motor
						out.println("gpio pin 0 0");
						break;
					case '1': // spin left motor forward
						out.println("gpio pin 0 1");
						out.println("gpio pin 1 0");
						break;
					case '2': // spin left motor backwards
						out.println("gpio pin 0 1");
						out.println("gpio pin 1 1");
						break;
					default:
						break;
					}
					switch (c[1])
					{
					case '0': // turn off right motor
						out.println("gpio pin 2 0");
						break;
					case '1': // spin right motor forward
						out.println("gpio pin 2 1");
						out.println("gpio pin 3 0");
						break;
					case '2': // spin right motor backwards
						out.println("gpio pin 2 1");
						out.println("gpio pin 3 1");
						break;
					default:
						break;
					}
					out.flush();
				}
				Thread.sleep(1);
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
			} catch (InterruptedException e)
			{
				// thread was woken early, no issue
			}
		}

	}
}
