package net.runelite.client.plugins.groundwarnings;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("GroundWarnings")
public interface GroundWarningsConfig extends Config
{
	@ConfigSection(
		name = "Ground Warnings",
		description = "Toggle ground warning settings.",
		position = 1)
	String groundSection = "Ground";

	@ConfigSection(
			name = "Ground Config",
			description = "Configure ground warning settings.",
			position = 2)
	String groundConfig = "Ground Config";

	@ConfigItem(
			name = "Ground ID List",
			keyName = "allID",
			description = "GroundID, GroundTicks",
			position = 1,
			section = groundSection
	)
	default String allID() {
		return "";
	}

	@Alpha
	@ConfigItem(
		keyName = "borderCol",
		name = "Warning border color",
		description = "Color the edges of the area highlighted will be",
		position = 1,
		section = groundConfig
	)
	default Color borderCol()
	{
		return new Color(255, 0, 0, 100);
	}

	@Alpha
	@ConfigItem(
		keyName = "poisonCol",
		name = "Warning color",
		description = "Color the fill of the area highlighted will be",
		position = 2,
		section = groundConfig
	)
	default Color groundCol()
	{
		return new Color(255, 0, 0, 50);
	}

	@Range(
			max = 20,
			min = 1
	)
	@ConfigItem(
			keyName = "renderDistance",
			name = "Render Distance",
			description = "Render distance in tiles from your player",
			position = 3,
			section = groundConfig
	)
	@Units("tiles")
	default int renderDistance()
	{
		return 5;
	}

	@ConfigItem(
			keyName = "showTicks",
			name = "Show Ticks",
			description = "Show number of ticks until ground warning disappears",
			position = 4,
			section = groundConfig
	)
	default boolean showTicks()
	{
		return false;
	}
}
