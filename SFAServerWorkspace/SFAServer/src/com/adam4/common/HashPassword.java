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
    }
}
