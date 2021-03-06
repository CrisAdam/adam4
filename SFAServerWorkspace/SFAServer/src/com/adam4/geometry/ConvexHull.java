package com.adam4.geometry;

import java.util.ArrayList;

import com.adam4.common.Point;

public class ConvexHull
{
	/*
	 * This function will make compartmentalize the convex hull problem and solve it using 
	 * a divide and conquer approach. To being, it will identify four key points (North, East, South, and West)
	 * based on the largest magnitude of X and Y coordinates, then create four boxes between SE, EN, NW, WS (in counter-clockwise order),
	 * and 
	 */
	public static ArrayList<Point> findConvexPoints(ArrayList<Point> input)
	{
		ArrayList<Point> output = new ArrayList<Point>();
		if (input.size() < 3)
		{
			return input;
		}
		
		Point n, e, s, w;
		n = e = s = w = input.get(0);
		for (Point p : input)
		{
			if (p.y < s.y)
			{
				s = p;
			}
			if (p.x > e.x)
			{
				e = p;
			}
			if (p.y > n.y)
			{
				n = p;
			}
			if (p.x < w.x)
			{
				w = p;
			}
		}
		
		ArrayList<Point> q1 = new ArrayList<Point>();
		ArrayList<Point> q2 = new ArrayList<Point>();
		ArrayList<Point> q3 = new ArrayList<Point>();
		ArrayList<Point> q4 = new ArrayList<Point>();
		
		for (Point p : input)
		{
			if (p.x > n.x && p.y > e.y)
			{
				q1.add(p);
			}
			else if (p.y > w.y && p.x < n.x)
			{
				q2.add(p);
			}
			else if (p.y < w.y && p.x < s.x)
			{
				q3.add(p);
			}
			else if (p.x > s.x && p.y < e.y)
			{
				q4.add(p);
			}
		}
		
		output.addAll(findConvexPoints(q1, s, e));
		output.addAll(findConvexPoints(q2, e, n));
		output.addAll(findConvexPoints(q3, n, w));
		output.addAll(findConvexPoints(q4, w, s));

		
		return output;
	}
	
	/* This function will go through the points in a counter-clockwise order from start to end and trim all the
	 * input points that are below the line 
	 */
	public static ArrayList<Point> findConvexPoints(ArrayList<Point> input, Point start, Point end)
	{
		// TODO: remove trig and use computational geometry instead, i.e. vectors
		ArrayList<Point> output = new ArrayList<Point>();
		output.add(start);
		if (input.size() == 0)
		{
			return output;
		}
		
		double comp = Point.getAngleBetweenPoints(start, end);
		Point split = null;
		for (Point p : input)
		{
			if (Point.getAngleBetweenPoints(start, p) < comp)
			{
				input.remove(p);
			}
			else if (Point.getAngleBetweenPoints(start, p) > comp)
			{
				split = p;
				break;
			}
		}
		if (split != null)
		{
			ArrayList<Point> firstHalf = new ArrayList<Point>();
			ArrayList<Point> secondHalf = new ArrayList<Point>();
			comp = Point.getAngleBetweenPoints(new Point(0,0), split);
			for (Point p : input)
			{
				if (Point.getAngleBetweenPoints(new Point(0,0), p) < comp)
				{
					firstHalf.add(p);
				}
				else
				{
					secondHalf.add(p);
				}
			}
			output.addAll(findConvexPoints(firstHalf, start, split));
			output.addAll(findConvexPoints(secondHalf, split, end));
		}	
		return output;
	}
}
