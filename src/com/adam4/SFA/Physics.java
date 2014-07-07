package com.adam4.SFA;

public class Physics implements Runnable
{
    Game game;
    QuadTree map;
    long pTime; // previous tick time
    long time; // current time
    long dTime; // delta time

    Physics(Game game)
    {
        this.game = game;
        map = new QuadTree(new Point(0, 0), SFAServer.rootQuadTreeSize, null);

        time = pTime = System.nanoTime();
        dTime = 500;
        game.updatesPerSecond = (game.updatesPerSecond * .99) + (10000000 / (dTime));
    }

    public void run()
    {
        while (game.getRunning())
        {
            update();
        }
    }

    void update()
    {
        try
        {
            pTime = time; // previous tick time
            time = System.nanoTime();
            dTime = time - pTime; // delta time
            while ((System.nanoTime() - time) < 15000000) // max of 66 hz
            {
                Thread.sleep(1);
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("unable to sleep");
            e.printStackTrace();
        }
    }
}
