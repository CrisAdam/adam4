package com.adam4.SFA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TestClient
{
    static Socket s;
    static OutputStream output;
    static InputStream input;

    public static void main(String[] args)
    {
        String URL = "localhost";
        if (args.length > 0)
        {
            URL = args[0];
        }
        try
        {
            s = new Socket(URL, 9995);
            System.out.println("connected to: " + URL);
            output = s.getOutputStream();
            input = s.getInputStream();

            // 0=c (Connect), 1 = name, 2 = password, 3 = clientType, 4 =
            // clientVersion
            final char SEPARATOR = (char) 31;
            String connect = "c" + SEPARATOR;
            connect += "Merlin" + SEPARATOR;
            connect += "AbraKadabra" + SEPARATOR;
            connect += "DesktopTestCLient" + SEPARATOR;
            connect += "1.0" + '\n';

            System.out.println(connect);

            output.write((connect.getBytes("UTF-8")));
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8.newDecoder()));

            // System.out.println(reader.readLine() + i);

            s.close();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
