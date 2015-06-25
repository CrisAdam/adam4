package com.adam4.rcdriver;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;


public class MainActivity extends ActionBarActivity {

    EditText ip;
    PrintWriter output;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip = (EditText) findViewById(R.id.ip);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void connect (View view)
    {
        new Thread(new Runnable()
        {
            public void run()
            {
            try
            {
                InetAddress serverAddr = InetAddress.getByName(ip.getText().toString());
                socket = new Socket(serverAddr, 9090);
                output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true); // needs API level 19
            }
            catch (Exception e)
            {
            }
            }
        }).start();

}

    public void forward (View view)
    {
        connect(view);
        if (output != null)
        {
            new Thread(new Runnable()
            {
                public void run()
                {
                    output.println("11");
                }
            }).start();
        }
    }

    public void backwards (View view)
    {
        connect(view);
        if (output != null)
        {
            new Thread(new Runnable()
            {
                public void run()
                {
                    output.println("22");
                }
            }).start();
        }
    }

    public void right (View view)
    {
        connect(view);
        if (output != null)
        {
            new Thread(new Runnable()
            {
                public void run()
                {
                    output.println("21");
                }
            }).start();
        }
    }
    public void left (View view)
    {
        connect(view);
        if (output != null)
        {
            new Thread(new Runnable()
            {
                public void run()
                {
                    output.println("12");
                }
            }).start();
        }
    }

    public void stop (View view)
    {
        connect(view);
        if (output != null)
        {
            new Thread(new Runnable()
            {
                public void run()
                {
                    output.println("00");
                }
            }).start();
        }
    }
}
