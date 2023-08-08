package com.musiccapehelper;

import com.musiccapehelper.enums.Music;
import com.musiccapehelper.enums.OrderBy;
import com.musiccapehelper.enums.Region;
import com.musiccapehelper.enums.Locked;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface MusicCapeHelperConfig extends Config
{
	@ConfigItem(
		keyName = "greeting",
		name = "Welcome Greeting",
		description = "The message to show to the user when they login"
	)
	default String greeting()
	{
		return "Hello";
	}

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
		description = "Used in panel for combo-box locked"
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
	default Region panelSettingRegion() {return Region.ALL;}

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
	default OrderBy panelSettingOrderBy() {return OrderBy.AZ;}

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
