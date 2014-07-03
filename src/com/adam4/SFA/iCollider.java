package com.adam4.SFA;

import com.adam4.common.Circle;
import com.adam4.common.Common.*;

public interface iCollider
{
	void update(double delta, QuadTree current);

	void resetUpdate();

	Circle getBody();

	ColliderType getType();
}

enum ColliderType
{
	SHIP, ASTEROID, PROJECTILE;
}

class Asteroid implements iCollider
{
	double size, mass, xVelocity, yVelocity;
	Circle body;
	boolean movedThisTick;
	Player owner;
	ColliderType type;

	public Asteroid(Circle body, Double mass, Double xVelocity, Double yVelocity)
	{
		this.body = body;
		this.xVelocity = xVelocity;
		this.yVelocity = yVelocity;
		this.mass = mass;
	}

	public void update(double delta, QuadTree current)
	{
		if (movedThisTick)
		{
			return;
		}
		movedThisTick = true;
		body.center.x += xVelocity * delta;
		body.center.y += yVelocity * delta;
		if (!current.fitsWithin(body))
		{
			current.relocate(this);
		}
	}

	public void resetUpdate()
	{
		movedThisTick = false;
	}

	public ColliderType getType()
	{
		return ColliderType.ASTEROID;
	}

	public Circle getBody()
	{
		return body;
	}

}