package com.adam4.common;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.junit.Test;

public class UnitTests
{

    @Test
    public void testMajority()
    {
        if (!Common.hasMajority(1, 1))
        {
            fail("1 1");
        }
        if (Common.hasMajority(1, 2))
        {
            fail("1 2");
        }
        if (!Common.hasMajority(2, 2))
        {
            fail("1 2");
        }
        if (!Common.hasMajority(2, 3))
        {
            fail("2 3");
        }
        if (Common.hasMajority(1, 3))
        {
            fail("1 3");
        }
        if (!Common.hasMajority(2, 3))
        {
            fail("2 3");
        }
        if (Common.hasMajority(2, 4))
        {
            fail("2 4");
        }
        if (Common.hasMajority(2, 5))
        {
            fail("2 5");
        }
        if (!Common.hasMajority(3, 5))
        {
            fail("3 5");
        }
    }

    @Test
    public void testHashPassword()
    {
        String secretHash = "";
        String hashed = "d46fe866408f7b224cebc677378ca69134c060b90f5b697e3789ef063b5d26e7c4f7cffd1d14f299faa1b75cf47adc70f2f4ca38fbbd3ee0506858fba4cd604c";
        secretHash = Common.hashPassword("Secret");

        if (!hashed.equals(secretHash))
        {
            fail("bad hash");
        }
    }

    @Test
    public void testIsGoodUserName()
    {
        if (!(Common.isGoodUserName("bob1")))
        {
            fail("bob is a good user name");
        }
        if ((Common.isGoodUserName("thisUserNameIsWayTooLong")))
        {
            fail("thisUserNameIsWayTooLong");
        }
        if ((Common.isGoodUserName("$pecialCh@rs")))
        {
            fail("$pecialCh@rs");
        }
        if ((Common.isGoodUserName("s p a c e s")))
        {
            fail("s p a c e s");
        }

    }

    @Test
    public void testGetSystem()
    {
        String system = Common.getSystem().toLowerCase();
        if (!(system.contains("windows") || system.contains("linux")))
        {
            fail("Unsupported system: " + system);
        }

    }

    @Test
    public void getSSLStrength() throws IOException
    {
        // Get the SSLServerSocket
        SSLServerSocketFactory ssl;
        SSLServerSocket sslServerSocket;
        ssl = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        sslServerSocket = (SSLServerSocket) ssl.createServerSocket();

        boolean pass = false;
        // Get the list of all supported cipher suites.
        String[] cipherSuites = sslServerSocket.getSupportedCipherSuites();
        for (String suite : cipherSuites)
        {
            if (suite.equals("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA"))
            {
                pass = true;
            }
        }
        if (!pass)
        {
            fail("cipher TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA not found, make sure the unlimited strength cryptography policy files are used");
        }

        pass = false;
        // Get the list of all supported protocols
        String[] protocols = sslServerSocket.getSupportedProtocols();
        for (String protocol : protocols)
        {
            if (protocol.equals("TLSv1.2"))
            {
                pass = true;
            }
        }

        if (!pass)
        {
            fail("protocol TLSv1.2 not found ");
        }

    }

}
