package com.adam4.misc.dogfight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

import Planes.*;

public class Starfox extends PlaneControl
{
    boolean isAhead;
    private ArrayList<Point3D> corners;
    

    public Starfox(int arenaSize, int rounds)
    {
        super(arenaSize, rounds);
        isAhead = false;
        // add corners as places to be avoided
        corners.add(new Point3D(0, 0, 0));
        corners.add(new Point3D(0, 0, arenaSize - 1));
        corners.add(new Point3D(0, arenaSize - 1, 0));
        corners.add(new Point3D(0, arenaSize - 1, arenaSize - 1));
        corners.add(new Point3D(arenaSize - 1, 0, 0));
        corners.add(new Point3D(arenaSize - 1, 0, arenaSize - 1));
        corners.add(new Point3D(arenaSize - 1, arenaSize - 1, 0));
        corners.add(new Point3D(arenaSize - 1, arenaSize - 1, arenaSize - 1));
        
    }


    class bestMoves
    {
        bestMoves()
        {
            score = -10000;
        }
        int score;
        Move[] moves;
    }

    @Override
    public Move[] act()
    {
        ArrayList<Point3D> riskyPositions = new ArrayList<>();
        ArrayList<Point3D> dangerousPositions = new ArrayList<>(corners);
        
        // add enemy potential moves and firing area to risky positions
        for (Plane p : super.enemyPlanes)
        {
            for (Direction d : p.getPossibleDirections())
            {
                riskyPositions.add(p.getPosition().add(d.getAsPoint3D()));
                if (p.canShoot())
                {
                    for (Point3D range : p.simulateMove(new Move(d, true, false)).getShootRange())
                    {
                        riskyPositions.add(range);
                    }
                    for (Point3D range : p.simulateMove(new Move(d, false, false)).getShootRange())
                    {
                        riskyPositions.add(range);
                    }
                }
            }
        }
        
        return getBestMove(new ArrayList<Plane>(Arrays.asList(super.myPlanes)), dangerousPositions, riskyPositions).moves;

    }

    private bestMoves getBestMove(ArrayList<Plane> planes, ArrayList<Point3D> dangerousPositions, ArrayList<Point3D> riskyPositions)
    {
        
        // return n moves where n is number of planes passed in
        bestMoves bestMove = new bestMoves();
        bestMove.moves = new Move[planes.size()];
        ArrayList<Point3D> currentDangerousPositions = new ArrayList<Point3D>(dangerousPositions);
        
        for (int i = 0; i < planes.size(); i++)
        {
            ArrayList<Plane> others = new ArrayList<Plane>(planes);
            others.remove(i);
            
            
            
            bestMoves bestPlaneMove = new bestMoves();
            // calculate score for plane
            for (Direction d : p.getPossibleDirections())
            {
                bestMoves bestDirectionMove = new bestMoves();
                
                ArrayList<Move> moves = new ArrayList<>();
                moves.add(new Move(d, true, false));
                moves.add(new Move(d, false, false));
                if (p.canShoot())
                {
                    moves.add(new Move(d, true, true));
                    moves.add(new Move(d, true, true));
                }
                
                for (Move m : moves)
                {
                    int score = getScore(p,m, dangerousPositions, riskyPositions);
                    if (score > bestDirectionMove.score)
                    {
                        bestDirectionMove.score = score;
                        bestDirectionMove.moves = new Move[1];
                        bestDirectionMove.moves[0] = m;
                    }
                }
                
                if (bestDirectionMove.score > bestPlaneMove.score)
                {
                    bestPlaneMove.moves = bestDirectionMove.moves;
                }
            }
            //
        }
        return bestMove;
    }

    private int getScore(Plane p, Move move, ArrayList<Point3D> dangerousPositions, ArrayList<Point3D> riskyPositions)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void newFight(int fightsFought, int myScore, int enemyScore)
    {
        isAhead = (myScore > enemyScore);
    }

    @Override
    public void newOpponent(int fights)
    {
        // how many times you will fight
    }
}
