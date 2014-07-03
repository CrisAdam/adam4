package com.adam4.common;

import com.adam4.SFA.Point;

public class Circle
{

	public Point center;
	public Double radius;
	
	public Circle (Point center, Double radius)
	{
		this.center = center;
		this.radius = radius;
	}
	
	public double getArea()
	{
		return Math.PI * radius * radius;
	}
	
	public double getCircumference()
	{
		return Math.PI * 2 * radius;
	}
}