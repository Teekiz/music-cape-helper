package com.musiccapehelper;

import com.musiccapehelper.enums.Locked;
import com.musiccapehelper.enums.Music;
import com.musiccapehelper.enums.Optional;
import com.musiccapehelper.enums.OrderBy;
import com.musiccapehelper.enums.Quest;
import com.musiccapehelper.enums.Region;
import java.util.HashMap;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("MusicCapeHelperConfig")
public interface MusicCapeHelperConfig extends Config
{
	//Saved values
	@ConfigItem(
		keyName = "musicList",
		name = "",
		description = "",
		hidden = true
	)
	HashMap<Music, Boolean> musicList();

	@ConfigItem(
		keyName = "mustList",
		name = "",
		description = ""
	)
	void musicList(HashMap<Music, Boolean> musicList);

	//Panel settings
	@ConfigSection(
		name = "PanelSettings",
		description = "",
		position = 100
	)
	String panelSection = "PanelSettings";

	@ConfigItem(
		keyName = "panelSettingLocked",
		name = "",
		description = "Used in panel for combo-box locked",
		hidden = true,
		section = panelSection
	)

	default Locked panelSettingLocked()
	{
		return Locked.ALL;
	}

	@ConfigItem(
		keyName = "panelSettingLocked",
		name = "",
		description = "Used in panel for combo-box locked",
		hidden = true,
		section = panelSection
	)
	void panelSettingLocked(Locked locked);

	@ConfigItem(
		keyName = "panelSettingQuest",
		name = "",
		description = "Used in panel for combo-box quest",
		hidden = true,
		section = panelSection
	)
	default Quest panelSettingQuest()
	{
		return Quest.ALL;
	}

	@ConfigItem(
		keyName = "panelSettingQuest",
		name = "",
		description = "Used in panel for combo-box quest",
		hidden = true,
		section = panelSection
	)
	void panelSettingQuest(Quest quest);

	@ConfigItem(
		keyName = "panelSettingRegion",
		name = "",
		description = "Used in panel for combo-box region",
		hidden = true,
		section = panelSection
	)
	default Region panelSettingRegion()
	{
		return Region.ALL;
	}

	@ConfigItem(
		keyName = "panelSettingRegion",
		name = "",
		description = "Used in panel for combo-box region",
		hidden = true,
		section = panelSection
	)
	void panelSettingRegion(Region region);

	@ConfigItem(
		keyName = "panelSettingOptional",
		name = "",
		description = "Used in panel for combo-box optional",
		hidden = true,
		section = panelSection
	)
	default Optional panelSettingOptional()
	{
		return Optional.ALL;
	}

	@ConfigItem(
		keyName = "panelSettingOptional",
		name = "",
		description = "Used in panel for combo-box optional",
		hidden = true,
		section = panelSection
	)
	void panelSettingOptional(Optional optional);

	@ConfigItem(
		keyName = "panelSettingOrderBy",
		name = "",
		description = "Used in panel for combo-box optional",
		hidden = true,
		section = panelSection
	)
	default OrderBy panelSettingOrderBy()
	{
		return OrderBy.AZ;
	}

	@ConfigItem(
		keyName = "panelSettingOrderBy",
		name = "",
		description = "Used in panel for combo-box optional",
		hidden = true,
		section = panelSection
	)
	void panelSettingOrderBy(OrderBy orderBy);
}

