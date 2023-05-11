package net.runelite.client.plugins.groundwarnings;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;


@Singleton
class GroundWarningsOverlay extends Overlay
{
	private final Client client;
	private final GroundWarningsPlugin plugin;
	private final GroundWarningsConfig config;
	private final ModelOutlineRenderer outliner;


	@Inject
	private GroundWarningsOverlay(final Client client, final GroundWarningsPlugin plugin, final GroundWarningsConfig config, ModelOutlineRenderer outliner)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.outliner = outliner;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.LOW);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		for (GroundContainer gc : plugin.getGraphicsObjects())
		{
			GraphicsObject graphicsObject = gc.graphicsObject;
			LocalPoint lp = graphicsObject.getLocation();
			Polygon poly = Perspective.getCanvasTilePoly(client, lp);
			Player localPlayer = client.getLocalPlayer();

			if (poly != null && localPlayer != null)
			{
				WorldPoint playerWorldPoint = localPlayer.getWorldLocation();
				WorldPoint shadowsWorldPoint = WorldPoint.fromLocal(client, lp);

				if (playerWorldPoint.distanceTo(shadowsWorldPoint) <= config.renderDistance())
				{
					graphics.setPaintMode();
					graphics.setColor(config.borderCol());
					graphics.draw(poly);
					graphics.setColor(config.groundCol());
					graphics.fill(poly);

					if (config.showTicks())
					{
						String count = Integer.toString(gc.ticks);
						Point point = Perspective.getCanvasTextLocation(client, graphics, lp, count, 0);
						if (point != null)
						{
							renderTextLocation(graphics, count, 12, Font.BOLD, Color.WHITE, point);
						}
					}
				}
			}
		}
		return null;
	}

	protected void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, Point canvasPoint)
	{
		graphics.setFont(new Font("Arial", fontStyle, fontSize));
		if (canvasPoint != null)
		{
			final Point canvasCenterPoint = new Point(canvasPoint.getX(), canvasPoint.getY());
			final Point canvasCenterPointShadow = new Point(canvasPoint.getX() + 1, canvasPoint.getY() + 1);

			OverlayUtil.renderTextLocation(graphics, canvasCenterPointShadow, txtString, Color.BLACK);
			OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
		}
	}
}