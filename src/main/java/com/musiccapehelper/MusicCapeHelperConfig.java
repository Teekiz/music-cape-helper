package com.musiccapehelper;

import com.musiccapehelper.enums.Music;
import com.musiccapehelper.enums.OrderBy;
import com.musiccapehelper.enums.Region;
import com.musiccapehelper.enums.Locked;
import java.util.HashMap;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("MusicCapeHelperConfig")
public interface MusicCapeHelperConfig extends Config
{
	//Panel settings
	@ConfigItem(
		keyName = "panelSettingLocked",
		name = "",
		description = "Used in panel for combo-box locked",
		hidden = true
	)

	Locked panelSettingLocked();

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
	default boolean panelSettingQuest() {return true;}

	@ConfigItem(
		keyName = "panelSettingQuest",
		name = "",
		description = "Used in panel for combo-box quest",
		hidden = true
	)
	void panelSettingQuest(boolean bool);

	@ConfigItem(
		keyName = "panelSettingRegion",
		name = "",
		description = "Used in panel for combo-box region",
		hidden = true
	)
	Region panelSettingRegion();

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
	default boolean panelSettingOptional() {return true;}

	@ConfigItem(
		keyName = "panelSettingOptional",
		name = "",
		description = "Used in panel for combo-box optional",
		hidden = true
	)
	void panelSettingOptional(Boolean bool);

	@ConfigItem(
		keyName = "panelSettingOrderBy",
		name = "",
		description = "Used in panel for combo-box optional",
		hidden = true
	)
	OrderBy panelSettingOrderBy();

	@ConfigItem(
		keyName = "panelSettingOrderBy",
		name = "",
		description = "Used in panel for combo-box optional",
		hidden = true
	)
	void panelSettingOrderBy(OrderBy orderBy);

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
}
