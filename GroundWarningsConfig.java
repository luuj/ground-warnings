package net.runelite.client.plugins.groundwarnings;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("GroundWarnings")
public interface GroundWarningsConfig extends Config
{
	@ConfigSection(
		name = "Nightmare Shadows",
		description = "Configure Shadows settings.",
		position = 1)
	String shadowsSection = "Shadows";

	@ConfigSection(
		name = "Nightmare Spores",
		description = "Configure Spores settings.",
		position = 2)
	String sporesSection = "Spores";

	@ConfigSection(
			name = "Warden P3",
			description = "Configure Warden P3 settings",
			position = 3)
	String wardenSection = "Warden";

	//SHADOWS SECTION
	@ConfigItem(
		keyName = "highlightShadows",
		name = "Highlight Shadows",
		description = "Highlights the Shadow Attacks",
		position = 0,
		section = shadowsSection
	)
	default boolean highlightShadows()
	{
		return true;
	}

	@ConfigItem(
			keyName = "shadowsTickCounter",
			name = "Shadows Tick Counter",
			description = "Displays the number of ticks until shadows do damage",
			position = 1,
			section = shadowsSection
	)
	default boolean shadowsTickCounter()
	{
		return true;
	}

	@Range(
		max = 20,
		min = 1
	)
	@ConfigItem(
		keyName = "shadowsRenderDistance",
		name = "Render Distance",
		description = "Render shadows distance in tiles from your player",
		position = 2,
		section = shadowsSection
	)
	@Units("tiles")
	default int shadowsRenderDistance()
	{
		return 5;
	}

	@Alpha
	@ConfigItem(
		keyName = "shadowsBorderColour",
		name = "Shadows border colour",
		description = "Colour the edges of the area highlighted by shadows",
		position = 3,
		section = shadowsSection
	)
	default Color shadowsBorderColour()
	{
		return new Color(0, 255, 255, 100);
	}

	@Alpha
	@ConfigItem(
		keyName = "shadowsColour",
		name = "Shadows colour",
		description = "Colour for shadows highlight",
		position = 4,
		section = shadowsSection
	)
	default Color shadowsColour()
	{
		return new Color(0, 255, 255, 50);
	}

	//SPORES SECTION
	@ConfigItem(
		keyName = "highlightSpores",
		name = "Highlight Spores",
		description = "Highlights spores that will make you yawn",
		position = 0,
		section = sporesSection
	)
	default boolean highlightSpores()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "poisonBorderCol",
		name = "Poison border colour",
		description = "Colour the edges of the area highlighted by poison special will be",
		position = 1,
		section = sporesSection
	)
	default Color poisonBorderCol()
	{
		return new Color(255, 0, 0, 100);
	}

	@Alpha
	@ConfigItem(
		keyName = "poisonCol",
		name = "Poison colour",
		description = "Colour the fill of the area highlighted by poison special will be",
		position = 2,
		section = sporesSection
	)
	default Color poisonCol()
	{
		return new Color(255, 0, 0, 50);
	}

	@ConfigItem(
		keyName = "yawnInfoBox",
		name = "Yawn InfoBox",
		description = "InfoBox telling you the time until your yawning ends",
		position = 3,
		section = sporesSection
	)
	default boolean yawnInfoBox()
	{
		return true;
	}

	@ConfigItem(
			keyName = "highlightBaba",
			name = "Highlight Baba Rocks",
			description = "Highlights baba rocks that fall during P3",
			position = 0,
			section = wardenSection
	)
	default boolean highlightBaba()
	{
		return true;
	}

	@ConfigItem(
			keyName = "highlightLightning",
			name = "Highlight Lightning",
			description = "Highlights lightning in final phase",
			position = 1,
			section = wardenSection
	)
	default boolean highlightLightning()
	{
		return true;
	}

	@ConfigItem(
			keyName = "p3TickCounter",
			name = "P3 Tick Counter",
			description = "Displays the number of ticks until attacks do damage",
			position = 2,
			section = wardenSection
	)
	default boolean p3TickCounter()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			keyName = "wardenBorderColour",
			name = "Warden border colour",
			description = "Colour the edges of the area highlighted",
			position = 3,
			section = wardenSection
	)
	default Color wardenBorderColour()
	{
		return new Color(247, 0, 255, 100);
	}

	@Alpha
	@ConfigItem(
			keyName = "wardenColour",
			name = "Warden colour",
			description = "Colour for warden highlight",
			position = 4,
			section = wardenSection
	)
	default Color wardenColour()
	{
		return new Color(213, 99, 236, 50);
	}
}
