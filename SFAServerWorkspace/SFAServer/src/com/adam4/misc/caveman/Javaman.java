package com.adam4.misc.caveman;

public class Javaman
{
    public static void main(String[] args)
    {
       System.out.println(run(args));
    }

    public static String run(String[] args)
    {
        // input: SPB,SBB
        // me, enemy
        // S: sharpen, P: poke, B: block

        if (args.length == 0 || !args[0].contains(",")) 
        {
            return("S");
        }
        else
        {
            String[] states = args[0].split(",");
            Player me = new Player(states[0].toCharArray());
            Player enemy = new Player(states[0].toCharArray());

            if (me.hasSword())
            {
                return("P");
            }
            else if (!enemy.canPoke())
            {
                if (me.canPoke() && (Math.random() * 95) < states[0].length())
                {
                    return("P");
                }
                else
                {
                    return("S");
                }
            }
            else if (enemy.hasSword())
            {
                if (me.canPoke())
                {
                    return("P");
                }
                else
                {
                    return("S");
                }

            }
            else if (enemy.canPoke())
            {
                if (me.canPoke())
                {
                    if ((Math.random() * 95) < states[0].length())
                    {
                        return("P");
                    }
                    else
                    {
                        return("B");
                    }
                }
                else
                {
                    if ((Math.random() * 95) < states[0].length())
                    {
                        return("S");
                    }
                    else
                    {
                        return("B");
                    }
                }
            }
            else
            {
                return("S");
            }
        }
    }
}
