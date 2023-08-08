package com.musiccapehelper;

import java.util.HashMap;
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
