package net.runelite.client.plugins.groundwarnings;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@PluginDescriptor(
	name = "<html><font color=#b82584>[J] Ground Warnings",
	enabledByDefault = false,
	description = "Highlights ground attacks",
	tags = {"bosses", "combat"}
)


@Singleton
public class GroundWarningsPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private GroundWarningsConfig config;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private InfoBoxManager infoBoxManager;
	@Inject
	private SpriteManager spriteManager;
	@Inject
	private ItemManager itemManager;
	@Inject
	private GroundWarningsOverlay overlay;

	private static final int NIGHTMARE_PRE_MUSHROOM = 37738;
	private static final int NIGHTMARE_MUSHROOM = 37739;
	private static final int NIGHTMARE_SHADOW = 1767;
	private static final int NIGHTMARE_SHADOW_TICKS = 5;
	private static final int BABA_ROCKS = 317;
	private static final int BABA_ROCKS2 = 2250;
	private static final int BABA_ROCKS3 = 2251;
	private static final int BABA_ROCKS_TICKS = 7;

	//yellow 1595

	@Getter(AccessLevel.PACKAGE)
	private final Map<LocalPoint, GameObject> spores = new HashMap<>();
	@Getter(AccessLevel.PACKAGE)
	private final ArrayList<GroundContainer> graphicsObjects = new ArrayList();

	public GroundWarningsPlugin() {}

	@Provides
	GroundWarningsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GroundWarningsConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		reset();
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		reset();
	}

	private void reset()
	{
		spores.clear();
		graphicsObjects.clear();
	}

	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject gameObj = event.getGameObject();
		int id = gameObj.getId();
		if ((id == NIGHTMARE_MUSHROOM || id == NIGHTMARE_PRE_MUSHROOM) && this.config.highlightSpores())
		{
			spores.put(gameObj.getLocalLocation(), gameObj);
		}
	}

	@Subscribe
	private void onGameObjectDespawned(GameObjectDespawned event)
	{
		GameObject gameObj = event.getGameObject();
		int id = gameObj.getId();
		if ((id == NIGHTMARE_MUSHROOM || id == NIGHTMARE_PRE_MUSHROOM) && this.config.highlightSpores())
		{
			spores.remove(gameObj.getLocalLocation());
		}
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated event)
	{
		if ((event.getGraphicsObject().getId() == NIGHTMARE_SHADOW) && this.config.highlightShadows())
		{
			graphicsObjects.add(new GroundContainer(event.getGraphicsObject(), NIGHTMARE_SHADOW_TICKS));
		}
		if ((event.getGraphicsObject().getId() == BABA_ROCKS ||
				event.getGraphicsObject().getId() == BABA_ROCKS2 ||
				event.getGraphicsObject().getId() == BABA_ROCKS3) && this.config.highlightBaba())
		{
			graphicsObjects.add(new GroundContainer(event.getGraphicsObject(), BABA_ROCKS_TICKS));
		}
	}

	@Subscribe
	private void onGameTick(final GameTick event)
	{
		for (int i = graphicsObjects.size() - 1; i >= 0; --i) {
			GroundContainer curr = this.graphicsObjects.get(i);
			--curr.ticks;

			if(curr.ticks == 0 || curr.graphicsObject.finished()){
				this.graphicsObjects.remove(curr);
			}
		}
	}
}
