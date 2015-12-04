import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Receiver
{
	public static Process p;
	public static PrintWriter out;
	public static BufferedReader stdInput;
	public static BufferedReader stdError;
	
	public static void main(String[] args)
	{
		Thread.currentThread().setName("MainThread");

		String[] ex = { "/bin/sh", "-c", "bash"};
		try
		{

			p = Runtime.getRuntime().exec(ex);
			out = new PrintWriter(Receiver.p.getOutputStream());
			for (int i = 0; i < 4; ++i)
			{
				Receiver.out.println("gpio mode " +i +" out");
			}
			stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		new Network();
	}

}


