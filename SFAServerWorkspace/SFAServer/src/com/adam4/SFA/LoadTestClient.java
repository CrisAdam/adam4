package com.adam4.SFA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

public class LoadTestClient
{

    static LinkedBlockingQueue<String> results;

    public static void main(final String[] args)
    {
      

    }

    static void startThreads(int numThreads)
    {
        for (int i = 0; i < numThreads; i++)
        {
            new Thread(new CreateConnection(String.valueOf(i))).run();
            System.out.println("Starting thread: " + i);

            try
            {
                Thread.sleep(5);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

class CreateConnection implements Runnable
{
    Socket s;
    OutputStream output;
    InputStream input;
    String name;

    // LinkedBlockingQueue<String> results;

    CreateConnection(String name)
    {
        this.name = name;
        // results = r;
    }

    @Override
    public void run()
    {
        try
        {
            Thread.sleep((int) Math.floor(Math.random() * 101));
            s = new Socket("54.172.235.102", 9995);

            output = s.getOutputStream();
            input = s.getInputStream();

            // 0=c (Connect), 1 = name, 2 = password, 3 = clientType, 4 =
            // clientVersion
            final char SEPARATOR = (char) 31;
            String connect = "c" + SEPARATOR;
            connect += "Name" + name + SEPARATOR;
            connect += name + "pw" + SEPARATOR;
            connect += "DesktopTestCLient" + SEPARATOR;
            connect += "1.0" + '\n';

            output.write((connect.getBytes("UTF-8")));
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8.newDecoder()));

            // System.out.println(name + "  " + reader.readLine());
            if (s.isConnected())
            {
                System.out.println("connected with" + connect);
            }
            s.close();
        }
        catch (IOException e)
        {
            System.out.println("Error: " + name);
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
