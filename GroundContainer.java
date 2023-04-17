package net.runelite.client.plugins.groundwarnings;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GraphicsObject;


@Getter(AccessLevel.PACKAGE)
class GroundContainer
{
	public GraphicsObject graphicsObject;
	public int ticks;

	GroundContainer(GraphicsObject graphicsObject, int ticks)
	{
		this.graphicsObject = graphicsObject;
		this.ticks = ticks;
	}
}
