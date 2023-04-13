package net.runelite.client.plugins.groundwarnings;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.infobox.Timer;

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

	// Nightmare's attack animations
	private static final int NIGHTMARE_PRE_MUSHROOM = 37738;
	private static final int NIGHTMARE_MUSHROOM = 37739;
	private static final int NIGHTMARE_SHADOW = 1767;   // graphics object
	private static final int BABA_ROCK = 2250;
	private static final int BABA_ROCK2 = 2251;
	private static final int LIGHTNING = 1446;

	@Getter(AccessLevel.PACKAGE)
	private final Map<LocalPoint, GameObject> spores = new HashMap<>();
	@Getter(AccessLevel.PACKAGE)
	private final Set<GraphicsObject> shadows = new HashSet<>();
	@Getter(AccessLevel.PACKAGE)
	private final Set<GraphicsObject> lightnings = new HashSet<>();
	@Getter(AccessLevel.PACKAGE)
	private final Set<GraphicsObject> babarocks = new HashSet<>();

	@Nullable
	@Getter(AccessLevel.PACKAGE)
	private NPC nm;
	@Getter(AccessLevel.PACKAGE)
	private boolean inFight;
	@Getter(AccessLevel.PACKAGE)
	private boolean shadowsSpawning = false;
	@Getter(AccessLevel.PACKAGE)
	private boolean lightningSpawning = false;
	@Getter(AccessLevel.PACKAGE)
	private boolean rocksSpawning = false;
	@Getter(AccessLevel.PACKAGE)
	private int shadowsTicks, lightningTicks, rockTicks;

	public GroundWarningsPlugin()
	{
		inFight = false;
	}

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
		inFight = false;
		nm = null;
		shadowsSpawning = rocksSpawning = lightningSpawning = false;
		shadowsTicks = lightningTicks = rockTicks = 0;
		spores.clear();
		shadows.clear();
		lightnings.clear();
		babarocks.clear();
	}

	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event)
	{
		if (!inFight)
		{
			return;
		}

		GameObject gameObj = event.getGameObject();
		int id = gameObj.getId();
		if (id == NIGHTMARE_MUSHROOM || id == NIGHTMARE_PRE_MUSHROOM)
		{
			spores.put(gameObj.getLocalLocation(), gameObj);
		}
	}

	@Subscribe
	private void onGameObjectDespawned(GameObjectDespawned event)
	{
		if (!inFight)
		{
			return;
		}

		GameObject gameObj = event.getGameObject();
		int id = gameObj.getId();
		if (id == NIGHTMARE_MUSHROOM || id == NIGHTMARE_PRE_MUSHROOM)
		{
			spores.remove(gameObj.getLocalLocation());
		}
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated event)
	{
		if (!inFight)
		{
			return;
		}

		if (event.getGraphicsObject().getId() == NIGHTMARE_SHADOW)
		{
			shadows.add(event.getGraphicsObject());
			shadowsSpawning = true;
			shadowsTicks = 5;
		}
		if (event.getGraphicsObject().getId() == BABA_ROCK || event.getGraphicsObject().getId() == BABA_ROCK2)
		{
			babarocks.add(event.getGraphicsObject());
			rocksSpawning = true;
			rockTicks = 7;
		}
		if (event.getGraphicsObject().getId() == LIGHTNING)
		{
			lightnings.add(event.getGraphicsObject());
			lightningSpawning = true;
			lightningTicks = 3;
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		Actor actor = event.getActor();
		if (!(actor instanceof NPC))
		{
			return;
		}

		NPC npc = (NPC) actor;

		// this will trigger once when the fight begins
		if (nm == null && npc.getName() != null && (npc.getName().equalsIgnoreCase("Tumeken's Warden") || npc.getName().equalsIgnoreCase("The Nightmare") || npc.getName().equalsIgnoreCase("Phosani's Nightmare")))
		{
			//reset everything
			reset();
			nm = npc;
			inFight = true;
		}

	}


	@Subscribe
	private void onChatMessage(ChatMessage event)
	{
		if (!inFight || nm == null || event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		if (config.yawnInfoBox() && event.getMessage().toLowerCase().contains("the nightmare's spores have infected you, making you feel drowsy!"))
		{
			Timer yawnInfoBox = new Timer(15600L, ChronoUnit.MILLIS, spriteManager.getSprite(SpriteID.SPELL_DREAM, 0), this);
			yawnInfoBox.setTooltip("Yawning");
			infoBoxManager.addInfoBox(yawnInfoBox);
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		GameState gamestate = event.getGameState();

		//if loading happens while inFight, the user has left the area (either via death or teleporting).
		if (gamestate == GameState.LOADING && inFight)
		{
			reset();
		}
	}

	@Subscribe
	private void onGameTick(final GameTick event)
	{
		if (!inFight || nm == null)
		{
			return;
		}

		//the fight has ended and everything should be reset
		if (nm.getId() == 378 || nm.getId() == 377)
		{
			reset();
		}

		if (shadowsTicks > 0)
		{
			shadowsTicks--;
			if (shadowsTicks == 0)
			{
				shadowsSpawning = false;
				shadows.clear();
			}
		}

		if (lightningTicks > 0)
		{
			lightningTicks--;
			if (lightningTicks == 0)
			{
				lightningSpawning = false;
				lightnings.clear();
			}
		}

		if (rockTicks > 0)
		{
			rockTicks--;
			if (rockTicks == 0)
			{
				rocksSpawning = false;
				babarocks.clear();
			}
		}
	}
}
