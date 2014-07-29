package com.adam4.misc.dogfight;

import java.util.ArrayList;
import java.util.function.Predicate;

public class Starfox extends PlaneControl
{

    public Starfox(int arenaSize, int rounds)
    {
        super(arenaSize, rounds);
    }

    private ArrayList<Point3D> dangerousPositions;
    private ArrayList<Point3D> riskyPositions;

    @Override
    public Move[] act()
    {
        dangerousPositions = new ArrayList<>();
        riskyPositions = new ArrayList<>();

        // add corners as places to be avoided
        dangerousPositions.add(new Point3D(0, 0, 0));
        dangerousPositions.add(new Point3D(0, 0, arenaSize - 1));
        dangerousPositions.add(new Point3D(0, arenaSize - 1, 0));
        dangerousPositions.add(new Point3D(0, arenaSize - 1, arenaSize - 1));
        dangerousPositions.add(new Point3D(arenaSize - 1, 0, 0));
        dangerousPositions.add(new Point3D(arenaSize - 1, 0, arenaSize - 1));
        dangerousPositions.add(new Point3D(arenaSize - 1, arenaSize - 1, 0));
        dangerousPositions.add(new Point3D(arenaSize - 1, arenaSize - 1, arenaSize - 1));

        System.out.println("enemy planes: " + super.enemyPlanes.length);
        for (Plane p : super.enemyPlanes)
        {
            if (p.getPossibleDirections().length > 0)
            {
                for (Direction d : p.getPossibleDirections())
                {
                    Point3D potentialPosition = new Point3D(p.getX(), p.getY(), p.getZ()).add(d.getAsPoint3D());
                    if (potentialPosition.isInArena(arenaSize))
                    {
                        riskyPositions.add(potentialPosition);
                        if (p.canShoot())
                        {
                            for (Point3D range : p.getShootRange())
                            {
                                riskyPositions.add(range.add(potentialPosition));
                            }
                        }
                    }
                }
            }
        }

        ArrayList<Move> moves = new ArrayList<>();

        for (Plane p : myPlanes)
        {
            if (p.isAlive())
            {
                ArrayList<Direction> potentialDirections = new ArrayList<>();

                for (Direction d : p.getPossibleDirections())
                {
                    Point3D potentialPosition = new Point3D(p.getX(), p.getY(), p.getZ()).add(d.getAsPoint3D());
                    if (potentialPosition.isInArena(arenaSize))
                    {
                        potentialDirections.add(d);
                    }
                }

                System.out.println("potentialDirections 1: " + potentialDirections.size());

                // remove dangerous positions from flight plan
                potentialDirections.removeIf(new Predicate<Direction>()
                {
                    @Override
                    public boolean test(Direction test)
                    {
                        boolean result = false;
                        for (Point3D compare : dangerousPositions)
                        {
                            if (p.getPosition().add(test.getAsPoint3D()).equals(compare))
                            {
                                result = true;
                            }
                        }
                        return result && potentialDirections.size() > 0;
                    }
                });
                
              System.out.println("potentialDirections 2: " + potentialDirections.size());

                // remove positions with no future from flight plan
                /*
                 * potentialDirections.removeIf(new Predicate<Direction>() {
                 * 
                 * @Override public boolean test(Direction test) { boolean hasFuture = false; for (Direction compare : p.getPossibleDirections()) { Plane future = new Plane(arenaSize, 0, compare,
                 * p.getPosition().add(compare.getAsPoint3D())); if (future.getPossibleDirections().length == 0) { continue; } for (Direction d : future.getPossibleDirections()) { if
                 * (future.getPosition().add(d.getAsPoint3D()).isInArena(arenaSize)) { hasFuture = true; break; } } } return !hasFuture; } });
                 */

                // remove risky positions from flight plan
              
            
              
                potentialDirections.removeIf(new Predicate<Direction>()
                {
                    @Override
                    public boolean test(Direction test)
                    {
                        boolean result = false;
                        for (Point3D compare : riskyPositions)
                        {
                            if (p.getPosition().add(test.getAsPoint3D()).equals(compare))
                            {
                                result = true;
                            }
                        }
                        return result && potentialDirections.size() > 0;
                    }
                });
                
                System.out.println("potentialDirections 3: " + potentialDirections.size());

                // check for targets
                Direction best = null;
                if (p.canShoot())
                {
                    int potentialHits = 0;
                    for (Direction d : potentialDirections)
                    {
                        Plane future = new Plane(arenaSize, 0, d, p.getPosition().add(d.getAsPoint3D()));
                        for (Point3D t : future.getShootRange())
                        {
                            int targets = 0;
                            for (Plane e : super.enemyPlanes)
                            {
                                for (Direction s : e.getPossibleDirections())
                                {
                                    Plane target = new Plane(arenaSize, 0, s, e.getPosition().add(s.getAsPoint3D()));
                                    if (target.getPosition().equals(t))
                                    {
                                        targets++;
                                    }

                                }
                            }
                            if (targets > potentialHits)
                            {
                                best = d;
                                potentialHits = targets;
                            }
                        }
                    }
                }

                if (potentialDirections.size() == 0)
                {
                    moves.add(new Move(new Direction("N"), false, false));
                    continue;
                }

                if (best == null)
                {
                    best = potentialDirections.get((int) Math.floor(Math.random() * potentialDirections.size()));
                }

                moves.add(new Move(best, true, false));
                dangerousPositions.add(p.getPosition().add(best.getAsPoint3D()));

            }
            else
            {
                // this plane is dead, not much to do but go hide in corner
                moves.add(new Move(new Direction("N"), false, false));

            }
        }

        return moves.toArray(new Move[moves.size()]);
    }

    @Override
    public void newFight(int fightsFought, int myScore, int enemyScore)
    {
        // Using information is for schmucks.
    }

    @Override
    public void newOpponent(int fights)
    {
        // What did I just say about information?
    }
}
