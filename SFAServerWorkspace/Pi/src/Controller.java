

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

	pin 0,1 = left motor
	pin 2,3 = right motor

	gpio mode n out // set it as an output pin
	gpio write n 1
	gpio write n 0

	 * 
	 * 
	 */
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
							Reciever.out.println("gpio write 0 0");
							Reciever.out.println("gpio write 1 0");
							break;
						case '1': // spin left motor forward
							Reciever.out.println("gpio write 0 1");
							Reciever.out.println("gpio write 1 0");
							break;
						case '2': // spin left motor backwards
							Reciever.out.println("gpio write 0 0");
							Reciever.out.println("gpio write 1 1");
							break;
						default:
							break;
						}
						switch (c[1])
						{
						case '0': // turn off right motor
							Reciever.out.println("gpio write 2 0");
							Reciever.out.println("gpio write 3 0");
							break;
						case '1': // spin right motor forward
							Reciever.out.println("gpio write 2 1");
							Reciever.out.println("gpio write 3 0");
							break;
						case '2': // spin right motor backwards
							Reciever.out.println("gpio write 2 0");
							Reciever.out.println("gpio write 3 1");
							break;
						default:
							break;
						}
						Reciever.out.flush();
						while ((m = Reciever.stdError.readLine()) != null)
						{
							System.out.println(m);
						}
						while ((m = Reciever.stdInput.readLine()) != null)
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
