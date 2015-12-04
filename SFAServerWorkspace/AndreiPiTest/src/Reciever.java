import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Reciever
{

	public static Process p;
	static PrintWriter out;
	static BufferedReader input;
	static BufferedReader error;

	public static void main(String[] args)
	{
		String[] ex = { "/bin/sh", "-c", "bash" };
		try
		{
			p = Runtime.getRuntime().exec(ex);

			out = new PrintWriter(p.getOutputStream());
			
			for (int i = 0; i < 4; ++i)
			{
				out.println("gpio mode " + i + " out");
			}
			input = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			error = new BufferedReader(
					new InputStreamReader(p.getErrorStream()));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("GPIOs setup");

		new Network();
	}

}
