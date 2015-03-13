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
        if (args.length == 4)
        {
            Authenticator.setDefault(new Authenticator()
            {
                @Override
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    if (getRequestorType() == RequestorType.PROXY)
                    {
                        String prot = getRequestingProtocol().toLowerCase();
                        String host = System.getProperty(prot + ".proxyHost", args[0]);
                        String port = System.getProperty(prot + ".proxyPort", args[1]);
                        String user = System.getProperty(prot + ".proxyUser", args[2]);
                        String password = System.getProperty(prot + ".proxyPassword", args[3]);
                        if (getRequestingHost().equalsIgnoreCase(host))
                        {
                            if (Integer.parseInt(port) == getRequestingPort())
                            {
                                // Seems to be OK.
                                return new PasswordAuthentication(user, password.toCharArray());
                            }
                        }
                    }
                    return null;
                }
            });

            System.out.println(args[0] + "  " + args[1] + "  " + args[2] + "  " + args[3].length());
        }
        try
        {

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.64.10.40", 80));

            InetSocketAddress SFA = new InetSocketAddress("ec2-54-186-196-159.us-west-2.compute.amazonaws.com", 9995);
            // URLConnection conn = new
            // URL("https://www.google.com:443").openConnection(proxy);

            // URLConnection s = new
            // URL("ec2-54-186-196-159.us-west-2.compute.amazonaws.com:9995").openConnection(proxy);

            Socket s = new Socket(proxy);
            s.connect(SFA);

            OutputStream output = s.getOutputStream();
            InputStream input = s.getInputStream();

            final char SEPARATOR = (char) 31;
            String connect = "c" + SEPARATOR;
            connect += "Name" + "Test" + SEPARATOR;
            connect += "Test" + "pw" + SEPARATOR;
            connect += "DesktopTestCLient" + SEPARATOR;
            connect += "1.0" + '\n';

            output.write((connect.getBytes("UTF-8")));
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8.newDecoder()));
            
            s.close();

        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // startThreads(1);
        

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
            s = new Socket("ec2-54-186-196-159.us-west-2.compute.amazonaws.com", 9995);

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