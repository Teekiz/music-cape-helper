package com.musiccapehelper;

import com.musiccapehelper.enums.settings.SettingsLocked;
import com.musiccapehelper.enums.settings.SettingsOptional;
import com.musiccapehelper.enums.settings.SettingsOrderBy;
import com.musiccapehelper.enums.settings.SettingsQuest;
import com.musiccapehelper.enums.settings.SettingsRegion;
import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("MusicCapeHelperConfig")
public interface MusicCapeHelperConfig extends Config
{
	@ConfigSection(
		name = "Panel Settings",
		description = "",
		position = 1
	)
	String panelSection = "Panel Settings";

	@ConfigItem(
		keyName = "panelCompleteTextColour",
		name="Completed Text Colour",
		description = "The colour of the text for completed music tracks displayed on the panel",
		section = "Panel Settings",
		position = 1
	)
	default Color panelCompleteTextColour() {return Color.GREEN;}

	@ConfigItem(
		keyName = "panelIncompleteTextColour",
		name="Incomplete Text Colour",
		description = "The colour of the text for incomplete music tracks displayed on the panel",
		section = "Panel Settings",
		position = 2
	)
	default Color panelIncompleteTextColour() {return Color.RED;}

	@ConfigItem(
		keyName = "panelDefaultTextColour",
		name="Default Text Colour",
		description = "The colour for the music track text displayed on the panel if the player has not logged in",
		section = "Panel Settings",
		position = 3
	)
	default Color panelDefaultTextColour() {return Color.WHITE;}

	@ConfigSection(
		name = "Panel Row Settings",
		description = "",
		position = 2
	)
	String panelRowSection = "Panel Row Settings";
	@ConfigItem(
		keyName = "panelAllowSetArrow",
		name="Enable hint arrow feature:",
		description = "When ticked, the player is able to set an which arrow points towards the unlock point when near.",
		section = "Panel Row Settings"
	)
	default boolean panelAllowSetArrow() {return true;}

	@ConfigItem(
		keyName = "panelIncludeDescription",
		name="Include description in row:",
		description = "When ticked, the music tracks description is added.",
		section = "Panel Row Settings"
	)
	default boolean panelIncludeDescription() {return true;}

	@ConfigSection(
		name = "Map Settings",
		description = "",
		position = 3
	)
	String mapSection = "Map Settings";

	@ConfigItem(
		keyName = "differentiateQuestMarkers",
		name = "Differentiate quest unlocks:",
		description = "Changes the markers shown to show a different icon if unlocked as part of a quest",
		section = "Map Settings"
	)

	default boolean differentiateQuestMarkers()
	{
		return true;
	}

	@ConfigItem(
		keyName = "differentiateCompletedMarkers",
		name = "Differentiate completed markers: ",
		description = "Changes the markers shown to show a different icon if the music track has been unlocked",
		section = "Map Settings"
	)

	default boolean differentiateCompletedMarkers()
	{
		return true;
	}

	//todo - remove marker if completed? hide panel if all are unlocked
	//Panel settings

	/*
		The hidden settings for plugin functionality.
	 */

	@ConfigItem(
		keyName = "panelSettingLocked",
		name = "",
		description = "Used in panel for combo-box locked",
		hidden = true
	)

	default SettingsLocked panelSettingLocked()
	{
		return SettingsLocked.ALL;
	}

	@ConfigItem(
		keyName = "panelSettingLocked",
		name = "",
		description = "Used in panel for combo-box locked",
		hidden = true
	)
	void panelSettingLocked(SettingsLocked settingsLocked);

	@ConfigItem(
		keyName = "panelSettingQuest",
		name = "",
		description = "Used in panel for combo-box quest",
		hidden = true
	)
	default SettingsQuest panelSettingQuest()
	{
		return SettingsQuest.ALL;
	}

	@ConfigItem(
		keyName = "panelSettingQuest",
		name = "",
		description = "Used in panel for combo-box quest",
		hidden = true
	)
	void panelSettingQuest(SettingsQuest settingsQuest);

	@ConfigItem(
		keyName = "panelSettingRegion",
		name = "",
		description = "Used in panel for combo-box region",
		hidden = true
	)
	default SettingsRegion panelSettingRegion()
	{
		return SettingsRegion.ALL;
	}

	@ConfigItem(
		keyName = "panelSettingRegion",
		name = "",
		description = "Used in panel for combo-box region",
		hidden = true
	)
	void panelSettingRegion(SettingsRegion settingsRegion);

	@ConfigItem(
		keyName = "panelSettingOptional",
		name = "",
		description = "Used in panel for combo-box optional",
		hidden = true
	)
	default SettingsOptional panelSettingOptional()
	{
		return SettingsOptional.ALL;
	}

	@ConfigItem(
		keyName = "panelSettingOptional",
		name = "",
		description = "Used in panel for combo-box optional",
		hidden = true
	)
	void panelSettingOptional(SettingsOptional settingsOptional);

	@ConfigItem(
		keyName = "panelSettingOrderBy",
		name = "",
		description = "Used in panel for combo-box optional",
		hidden = true
	)
	default SettingsOrderBy panelSettingOrderBy()
	{
		return SettingsOrderBy.AZ;
	}

	@ConfigItem(
		keyName = "panelSettingOrderBy",
		name = "",
		description = "Used in panel for combo-box optional",
		hidden = true
	)
	void panelSettingOrderBy(SettingsOrderBy settingsOrderBy);
}

