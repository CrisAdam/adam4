package com.adam4.SFA;

public class Point
{
    public double x, y;

    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public static double getAngleBetweenPoints(Point point1, Point point2)
    {
        double angle = Math.toDegrees(Math.atan2(point1.x - point2.x, point1.y - point2.y));
        if (angle < 0)
        {
            angle += 360;
        }
        return angle;
    }

    public static double getDistanceBetweenPoints(Point point1, Point point2)
    {
        Double dx = point1.x - point2.x;
        Double dy = point1.y - point2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static Point getPointfromOffset(Point p, double distance, double offsetAngle)
    {
        double xOffset = Math.sin(offsetAngle) * distance;
        double yOffset = Math.cos(offsetAngle) * distance;
        Point result = new Point((p.x + xOffset), (p.y + yOffset));
        return result;
    }
}
