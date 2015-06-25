import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Reciever
{
	static Process p;
	static PrintWriter out;
	static BufferedReader stdInput;
	static BufferedReader stdError;
	
	public static void main(String[] args)
	{
		Thread.currentThread().setName("MainThread");

		String[] ex = { "/bin/sh", "-c", "bash"};
		try
		{

			p = Runtime.getRuntime().exec(ex);
			out = new PrintWriter(Reciever.p.getOutputStream());
			for (int i = 0; i < 4; ++i)
			{
				Reciever.out.println("gpio mode " +i +" out");
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
