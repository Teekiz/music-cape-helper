package com.musiccapehelper;

import com.musiccapehelper.enums.data.Music;
import com.musiccapehelper.enums.settings.SettingsLocked;
import com.musiccapehelper.enums.settings.SettingsOptional;
import com.musiccapehelper.enums.settings.SettingsQuest;
import com.musiccapehelper.enums.settings.SettingsRegion;
import java.util.Arrays;
import java.util.HashMap;
import net.runelite.api.widgets.Widget;

public class MusicCollection
{
	private final HashMap<Music, Boolean> musicList = new HashMap<>();
	private final MusicCapeHelperConfig config;

	public MusicCollection(MusicCapeHelperConfig config)
	{
		this.config = config;
		Arrays.stream(Music.values()).forEach(r -> musicList.put(r, false));
	}

	public void updateMusicList(Widget[] musicWidgetChildren)
	{
		for (Music music : musicList.keySet())
		{
			for (Widget widget : musicWidgetChildren)
			{
				if (widget.getText().equals(music.getSongName()))
				{
					if (Integer.toHexString(widget.getTextColor()).equals("dc10d"))
					{

						musicList.put(music, true);
					}
					else
					{
						musicList.put(music, false);
					}
				}
			}
		}
	}

	public HashMap<Music, Boolean> getDefaultMusicList()
	{
		return musicList;
	}

	public HashMap<Music, Boolean> getFilteredMusicList()
	{
		HashMap<Music, Boolean> filteredList = new HashMap<>(musicList);

		//check one - does the music match the selected settings for quest discovered status
		if (!config.panelSettingLocked().equals(SettingsLocked.ALL))
		{
			if (config.panelSettingLocked().equals(SettingsLocked.LOCKED))
			{
				filteredList.entrySet().removeIf(b -> b.getValue());
			}
			else
			{
				filteredList.entrySet().removeIf(b -> !b.getValue());
			}
		}

		//check two - does the music match the selected settings for the selected region
		if (!config.panelSettingRegion().equals(SettingsRegion.ALL))
		{
			filteredList.entrySet().removeIf(r -> !r.getKey().getSettingsRegion().equals(config.panelSettingRegion()));
		}

		//check three - does the music match the selected settings for the selected quest option
		if (!config.panelSettingQuest().equals(SettingsQuest.ALL))
		{
			if (config.panelSettingQuest().equals(SettingsQuest.NOT_QUEST_UNLOCK))
			{
				filteredList.entrySet().removeIf(q -> q.getKey().isQuest());
			}
			else
			{
				filteredList.entrySet().removeIf(q -> !q.getKey().isQuest());
			}
		}

		//check four - does the music match the selected settings for the selected optional option
		if (!config.panelSettingOptional().equals(SettingsOptional.ALL))
		{
			if (config.panelSettingOptional().equals(SettingsOptional.OPTIONAL_ONLY))
			{
				filteredList.entrySet().removeIf(q -> q.getKey().isRequired());
			}
			else
			{
				filteredList.entrySet().removeIf(q -> !q.getKey().isRequired());
			}
		}
		return filteredList;
	}
}
