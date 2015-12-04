import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Test
{
	static Socket s;
	static PrintWriter output;
	static InputStream input;

	public static void main(String[] args)
	{
		String url = "192.168.123.14";
		try
		{
			s = new Socket(url, 9090);
			output = new PrintWriter(new OutputStreamWriter(
					s.getOutputStream(), StandardCharsets.UTF_8), true);
			input = s.getInputStream();
			new Thread(new Writer(s)).start();
			Scanner in = new Scanner(System.in);
			String consoleInput = "";

			while (!s.isClosed())
			{
				consoleInput = in.nextLine();
				output.write(consoleInput + '\n');
				output.flush();
				System.out.println("sent " + consoleInput);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	static class Writer implements Runnable
	{
		String str;
		Socket sock;
		BufferedReader input;

		public Writer(Socket s)
		{
			sock = s;
			try
			{
				input = new BufferedReader(
						new InputStreamReader(s.getInputStream(),
								StandardCharsets.UTF_8.newDecoder()));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void run()
		{
			try
			{
				while ((str = input.readLine()) != null)
				{
					System.out.println(str);
				}
			} catch (Exception e)
			{
			}
			;

		}
	}

}
