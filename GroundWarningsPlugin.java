package net.runelite.client.plugins.groundwarnings;

import com.google.common.base.Splitter;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
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
import java.util.List;

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
	private static final Splitter SPLITTER = Splitter.on("\n").omitEmptyStrings().trimResults();

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
		graphicsObjects.clear();
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated event)
	{
		GraphicsObject go = event.getGraphicsObject();
		List<String> strList = SPLITTER.splitToList(this.config.allID());
		Iterator var4 = strList.iterator();

		while(var4.hasNext()) {
			String str = (String)var4.next();
			String[] stringList = str.split(",");
			if (stringList.length > 1) {
				if (go.getId() == Integer.valueOf(stringList[0])){
					this.graphicsObjects.add(new GroundContainer(go, Integer.valueOf(stringList[1])));
					return;
				}
			}
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
