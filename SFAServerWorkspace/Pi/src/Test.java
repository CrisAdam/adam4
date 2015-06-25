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
    	
    	String URL = "192.168.123.23";
    	//String URL = "localhost";
        if (args.length > 0)
        {
            URL = args[0];
        }
        try
        {
            s = new Socket(URL, Network.port);
            System.out.println("connected to: " + URL);
            
            output = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true);
            
            input = s.getInputStream();
            
            new Thread(new Writer(s)).start();
            
            Scanner in = new Scanner(System.in);
            
            String consoleInput = "";
            System.out.println("enter quit to exit");
            while (!consoleInput.equals("quit") && !s.isClosed())
            {
            	consoleInput = in.nextLine();
            	output.write(consoleInput + '\n');
            	output.flush();
            	 System.out.println("sent: " + consoleInput);
            }
            s.close();
            System.out.println("closed");
            in.close();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        
    }
    
    static class Writer implements Runnable
    {
    	String str;
    	Socket sock;
    	BufferedReader input;
    	Writer(Socket s) throws IOException
    	{
    		sock = s;
    		input = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8.newDecoder()));
    	}

		@Override
		public void run() 
		{
			System.out.println("TestClient reader running from " + sock.getRemoteSocketAddress());
			try {
				while((str = input.readLine()) != null)
				{
					System.out.println("TestClient Recieved: " + str);
				}
			}
			catch (IOException e) 
			{
				if (sock.isClosed())
				{
					System.out.println("Client socket has been closed, ending reader");
				}
				else
				{
					e.printStackTrace();
				}
				
			}
		}
    }
    
    
}