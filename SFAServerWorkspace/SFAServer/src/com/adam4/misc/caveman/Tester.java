package com.adam4.misc.caveman;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;

public class Tester
{
    public static void main(String[] args) throws IOException
    {
        int p1, p2, tie;
        p1 = p2 = tie = 0;
        String resultsPath = System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + "results.csv";
        PrintWriter fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(resultsPath, true)));
        System.out.println(resultsPath);
        
        for (int i = 0; i < 100000; i++)
        {
            int result = fight(fileWriter);
            switch(result)
            {
            case 0:
                tie++;
                break;
            case 1:
                p1++;
                break;
            case 2:
                p2++;
                break;
            }
        }
        System.out.println("p1: " + p1 + " p2: " + p2 + " ties: " + tie);
        fileWriter.println("p1: " + p1 + " p2: " + p2 + " ties: " + tie);
        fileWriter.flush();
        fileWriter.close();

    }
    
    private static int fight(PrintWriter fileWriter)
    {
        String player1Moves = "";
        String player2Moves = "";

        for (int i = 0; i < 100; i++)
        {
            Player p1 = new Player(player1Moves.toCharArray());
            Player p2 = new Player(player2Moves.toCharArray());

            String[] p1Input = new String[] { player1Moves+","+player2Moves};
            String[] p2Input = new String[] { player2Moves+","+player1Moves};
            
            if (player1Moves.length() == 0)
            {
                p1Input = new String[0];
                p2Input = new String[0];
            }
            
            String p1Move = Javaman.run(p1Input);
            String p2Move = ViceCaveman.run(p2Input);

            if(checkForWinner(p1, p1Move, p2, p2Move) != 0)
            {
                
                fileWriter.println(i+","+ checkForWinner(p1, p1Move, p2, p2Move));
                return checkForWinner(p1, p1Move, p2, p2Move);
            }
            
            player1Moves = player1Moves + p1Move;
            player2Moves = player2Moves + p2Move;
            
            
           // System.out.println(player1Moves + "," + player2Moves);
        }
        
        return 0; // tie
    }

    private static int checkForWinner(Player p1, String p1Move, Player p2, String p2Move)
    {
     //   System.out.println(p1Move + " " + p1.sharpLevel + " vs " + p2Move + " " + p2.sharpLevel);
        if (p1Move.equals("P") && p1.canPoke())
        {
            if (p2Move.equals("S") || (p2Move.equals("B") && p1.hasSword()))
            {
              //  System.out.println("Javaman wins");
                return 1;
            }
        }
        if (p2Move.equals("P") && p2.canPoke())
        {
            if (p1Move.equals("S") || (p1Move.equals("B") && p2.hasSword()))
            {
            //    System.out.println("ViceCaveman wins");
                return 2;
            }
        }

        return 0;
    }

}
