package com.musiccapehelper;

import com.musiccapehelper.enums.Locked;
import com.musiccapehelper.enums.Optional;
import com.musiccapehelper.enums.OrderBy;
import com.musiccapehelper.enums.Quest;
import com.musiccapehelper.enums.Region;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("MusicCapeHelperConfig")
public interface MusicCapeHelperConfig extends Config
{
	@ConfigItem(
		keyName = "differentiateQuestMarkers",
		name = "Differentiate quest unlocks:",
		description = "Changes the markers shown to show a different icon if unlocked as part of a quest"
	)

	default boolean differentiateQuestMarkers()
	{
		return true;
	}

	@ConfigItem(
		keyName = "differentiateCompletedMarkers",
		name = "Differentiate completed markers: ",
		description = "Changes the markers shown to show a different icon if the music track has been unlocked"
	)

	default boolean differentiateCompletedMarkers()
	{
		return true;
	}

	//todo - remove marker if completed? hide panel if all are unlocked, set the arrows to the default colours
	//Panel settings

	@ConfigItem(
		keyName = "panelSettingLocked",
		name = "",
		description = "Used in panel for combo-box locked",
		hidden = true
	)

	default Locked panelSettingLocked()
	{
		return Locked.ALL;
	}

	@ConfigItem(
		keyName = "panelSettingLocked",
		name = "",
		description = "Used in panel for combo-box locked",
		hidden = true
	)
	void panelSettingLocked(Locked locked);

	@ConfigItem(
		keyName = "panelSettingQuest",
		name = "",
		description = "Used in panel for combo-box quest",
		hidden = true
	)
	default Quest panelSettingQuest()
	{
		return Quest.ALL;
	}

	@ConfigItem(
		keyName = "panelSettingQuest",
		name = "",
		description = "Used in panel for combo-box quest",
		hidden = true
	)
	void panelSettingQuest(Quest quest);

	@ConfigItem(
		keyName = "panelSettingRegion",
		name = "",
		description = "Used in panel for combo-box region",
		hidden = true
	)
	default Region panelSettingRegion()
	{
		return Region.ALL;
	}

	@ConfigItem(
		keyName = "panelSettingRegion",
		name = "",
		description = "Used in panel for combo-box region",
		hidden = true
	)
	void panelSettingRegion(Region region);

	@ConfigItem(
		keyName = "panelSettingOptional",
		name = "",
		description = "Used in panel for combo-box optional",
		hidden = true
	)
	default Optional panelSettingOptional()
	{
		return Optional.ALL;
	}

	@ConfigItem(
		keyName = "panelSettingOptional",
		name = "",
		description = "Used in panel for combo-box optional",
		hidden = true
	)
	void panelSettingOptional(Optional optional);

	@ConfigItem(
		keyName = "panelSettingOrderBy",
		name = "",
		description = "Used in panel for combo-box optional",
		hidden = true
	)
	default OrderBy panelSettingOrderBy()
	{
		return OrderBy.AZ;
	}

	@ConfigItem(
		keyName = "panelSettingOrderBy",
		name = "",
		description = "Used in panel for combo-box optional",
		hidden = true
	)
	void panelSettingOrderBy(OrderBy orderBy);
}

