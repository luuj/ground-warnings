package net.runelite.client.plugins.groundwarnings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Perspective;
import static net.runelite.api.Perspective.getCanvasTileAreaPoly;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

@Singleton
class GroundWarningsOverlay extends Overlay
{
	private final Client client;
	private final GroundWarningsPlugin plugin;
	private final GroundWarningsConfig config;
	private final ModelOutlineRenderer outliner;
	private int timeout;
	private static final int NIGHTMARE_SHADOW = 1767;

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
		if (!client.isInInstancedRegion() || !plugin.isInFight())
		{
			return null;
		}

		if (config.highlightShadows())
		{
			for (GraphicsObject graphicsObject : plugin.getShadows())
			{
				LocalPoint lp = graphicsObject.getLocation();
				Polygon poly = Perspective.getCanvasTilePoly(client, lp);
				Player localPlayer = client.getLocalPlayer();

				if (poly != null && localPlayer != null)
				{
					WorldPoint playerWorldPoint = localPlayer.getWorldLocation();
					WorldPoint shadowsWorldPoint = WorldPoint.fromLocal(client, lp);

					if (playerWorldPoint.distanceTo(shadowsWorldPoint) <= config.shadowsRenderDistance())
					{
						graphics.setPaintMode();
						graphics.setColor(config.shadowsBorderColour());
						graphics.draw(poly);
						graphics.setColor(config.shadowsColour());
						graphics.fill(poly);

						if (config.shadowsTickCounter())
						{
							String count = Integer.toString(plugin.getShadowsTicks());
							Point point = Perspective.getCanvasTextLocation(client, graphics, lp, count, 0);
							if (point != null)
							{
								renderTextLocation(graphics, count, 12, Font.BOLD, Color.WHITE, point);
							}
						}
					}
				}
			}
			if (plugin.isShadowsSpawning() && plugin.getNm() != null)
			{
				Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, plugin.getNm().getLocalLocation(), 5);
				OverlayUtil.renderPolygon(graphics, tilePoly, config.shadowsBorderColour());
			}
		}

		if (config.highlightBaba())
		{
			for (GraphicsObject graphicsObject : plugin.getBabarocks())
			{
				LocalPoint lp = graphicsObject.getLocation();
				Polygon poly = Perspective.getCanvasTilePoly(client, lp);
				Player localPlayer = client.getLocalPlayer();

				if (poly != null && localPlayer != null)
				{
					graphics.setPaintMode();
					graphics.setColor(config.wardenBorderColour());
					graphics.draw(poly);
					graphics.setColor(config.wardenColour());
					graphics.fill(poly);

					if (config.p3TickCounter())
					{
						String count = Integer.toString(plugin.getRockTicks());
						Point point = Perspective.getCanvasTextLocation(client, graphics, lp, count, 0);
						if (point != null)
						{
							renderTextLocation(graphics, count, 12, Font.BOLD, Color.WHITE, point);
						}
					}
				}
			}
			if (plugin.isRocksSpawning() && plugin.getNm() != null)
			{
				Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, plugin.getNm().getLocalLocation(), 5);
				OverlayUtil.renderPolygon(graphics, tilePoly, config.wardenBorderColour());
			}
		}

		if (config.highlightLightning())
		{
			for (GraphicsObject graphicsObject : plugin.getLightnings())
			{
				LocalPoint lp = graphicsObject.getLocation();
				Polygon poly = Perspective.getCanvasTilePoly(client, lp);
				Player localPlayer = client.getLocalPlayer();

				if (poly != null && localPlayer != null)
				{
					graphics.setPaintMode();
					graphics.setColor(config.wardenBorderColour());
					graphics.draw(poly);
					graphics.setColor(config.wardenColour());
					graphics.fill(poly);

					if (config.p3TickCounter())
					{
						String count = Integer.toString(plugin.getLightningTicks());
						Point point = Perspective.getCanvasTextLocation(client, graphics, lp, count, 0);
						if (point != null)
						{
							renderTextLocation(graphics, count, 12, Font.BOLD, Color.WHITE, point);
						}
					}
				}
			}
			if (plugin.isLightningSpawning() && plugin.getNm() != null)
			{
				Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, plugin.getNm().getLocalLocation(), 5);
				OverlayUtil.renderPolygon(graphics, tilePoly, config.wardenBorderColour());
			}
		}

		if (config.highlightSpores())
		{
			drawPoisonArea(graphics, plugin.getSpores());
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

	private void drawPoisonArea(Graphics2D graphics, Map<LocalPoint, GameObject> spores)
	{
		if (spores.size() < 1)
		{
			return;
		}

		Area poisonTiles = new Area();

		for (Map.Entry<LocalPoint, GameObject> entry : spores.entrySet())
		{
			LocalPoint point = entry.getKey();
			Polygon poly = getCanvasTileAreaPoly(client, point, 3);

			if (poly != null)
			{
				poisonTiles.add(new Area(poly));
			}
		}

		graphics.setPaintMode();
		graphics.setColor(config.poisonBorderCol());
		graphics.draw(poisonTiles);
		graphics.setColor(config.poisonCol());
		graphics.fill(poisonTiles);
	}
}