package com.musiccapehelper.data;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.enums.data.MusicData;
import com.musiccapehelper.enums.settings.SettingsLocked;
import com.musiccapehelper.enums.settings.SettingsOptional;
import com.musiccapehelper.enums.settings.SettingsQuest;
import com.musiccapehelper.enums.settings.SettingsRegion;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.runelite.api.widgets.Widget;

public class MusicList
{
	private final HashMap<MusicData, Boolean> musicList = new HashMap<>();
	private Widget[] gameMusicWidget;
	private final MusicCapeHelperConfig config;
	private final PropertyChangeSupport propertyChangeSupport
		= new PropertyChangeSupport(this);

	public MusicList(MusicCapeHelperConfig config)
	{
		this.config = config;

		Arrays.stream(MusicData.values()).forEach(r -> musicList.put(r, false));
	}

	public Widget[] getGameMusicWidget()
	{
		return gameMusicWidget;
	}

	public void updateGameMusicWidget(Widget[] updatedList)
	{
		gameMusicWidget = updatedList;
	}

	public void updateMusicList()
	{
		if (gameMusicWidget != null)
		{
			for (MusicData musicData : musicList.keySet())
			{
				for (Widget widget : gameMusicWidget)
				{
					if (widget.getText().equals(musicData.getSongName()))
					{
						if (Integer.toHexString(widget.getTextColor()).equals("dc10d"))
						{

							musicList.put(musicData, true);
						}
						else
						{
							musicList.put(musicData, false);
						}
					}
				}
			}
		}
		propertyChanged();
	}

	public void updateMusicTrack(MusicData music)
	{
		musicList.put(music, true);
		propertyChanged();
	}

	public HashMap<MusicData, Boolean> getDefaultMusicList()
	{
		return musicList;
	}

	public HashMap<MusicData, Boolean> getFilteredMusicList()
	{
		HashMap<MusicData, Boolean> filteredList = new HashMap<>(musicList);

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

	//returns list as it is possible to unlock multiple tracks in one tick
	public List<MusicData> getUpdatedTracks()
	{
		List<MusicData> updatedMusicData = new ArrayList<>();
		HashMap<MusicData, Boolean> oldMusicList = new HashMap<>(getDefaultMusicList());
		updateMusicList();

		//in the new version of the list, only the completed tracks are needed
		musicList.entrySet().stream().filter(r -> r.getValue().equals(true)).forEach(r ->
		{
			//gets the matching music track
			oldMusicList.entrySet().stream().filter(o -> o.getKey().equals(r.getKey())).forEach(o ->
			{
				//if it is false in the old list then it has been updated in which it gets added.
				if (!o.getValue())
				{
					updatedMusicData.add(o.getKey());
				}
			});
		});

		return updatedMusicData;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void propertyChanged()
	{
		propertyChangeSupport.firePropertyChange("musicList", null, null);
	}
}
