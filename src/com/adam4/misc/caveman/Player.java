package com.adam4.misc.caveman;

public class Player
{
    int sharpLevel;

    public Player(char[] state)
    {
        sharpLevel = 0;
        for (char c : state)
        {
            switch (c)
            {
            case 'S':
                sharpLevel++;
                break;
            case 'P':
                sharpLevel--;
                break;
            case 'B':
                break;
            default:
                return;
            }
        }
    }

    public boolean hasSword()
    {
        return sharpLevel > 4;
    }

    public boolean canPoke()
    {
        return sharpLevel > 0;
    }
}