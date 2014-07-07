package com.adam4.common;

import java.security.NoSuchAlgorithmException;

public class HashPassword
{
    public static void main(String[] args) throws NoSuchAlgorithmException, InterruptedException
    {
        for (String arg : args)
        {
            System.out.println(Common.hashPassword(arg));
        }
        Thread pendingQueueThread = new Thread(new pendingQueueThread());
        Thread confirmedQueueThread = new Thread(new confirmedQueueThread());
        System.out.println("Starting");
        pendingQueueThread.start();
        confirmedQueueThread.start();
    }

    private static class  pendingQueueThread implements Runnable
    {

        pendingQueueThread()
        {
            
        }
        public void run() 
        {
            for (int i = 0; i < 10; i++)
            {
                System.out.println("pending + " + i);
                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
        }
        
    }


    private static class  confirmedQueueThread implements Runnable
    {

        confirmedQueueThread()
        {
            
        }
        public void run() 
        {
            for (int i = 0; i < 10; i++)
            {
                System.out.println("confirmed + " + i);
                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
        }
        
    }

}
