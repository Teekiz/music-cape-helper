package com.musiccapehelper;

import com.google.inject.Provides;
import com.musiccapehelper.enums.Locked;
import com.musiccapehelper.enums.Music;
import com.musiccapehelper.enums.Optional;
import com.musiccapehelper.enums.OrderBy;
import com.musiccapehelper.enums.Quest;
import com.musiccapehelper.enums.Region;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.ImageUtil;

@Slf4j
@PluginDescriptor(
	name = "Music Cape Helper"
)
public class MusicCapeHelperPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private MusicCapeHelperConfig config;
	@Inject
	private ClientThread clientThread;
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private WorldMapPointManager worldMapPointManager;
	private NavigationButton navigationButton;
	private MusicCapeHelperPanel musicCapeHelperPanel;
	private HashMap<Music, Boolean> musicList;
	private List<MusicCapeHelperWorldMapPoint> mapPoints;

	//button at bottom of panel to add list
	//instead of multiple lists, create a hashmap at start up

	@Override
	protected void startUp() throws Exception
	{
		musicList = new HashMap<>();
		for (Music music : Music.values())
		{
			musicList.put(music, false);
		}

		musicCapeHelperPanel = new MusicCapeHelperPanel(this, config);

		//TODO change
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/skill_icons/overall.png");

		navigationButton = NavigationButton.builder()
			.tooltip("Music Cape Helper Panel")
			.icon(icon)
			.panel(musicCapeHelperPanel)
			.build();

		clientToolbar.addNavigation(navigationButton);

		clientThread.invokeAtTickEnd(this::updateMusicList);

		mapPoints = new ArrayList<>();
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navigationButton);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		clientThread.invokeAtTickEnd(this::updateMusicList);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		if (widgetLoaded.getGroupId() != 239)
		{
			return;
		}

		clientThread.invokeAtTickEnd(this::updateMusicList);
	}

	public void updateMusicList()
	{
		if (client.getWidget(239, 6) != null)
		{
			for (Music music : Music.values())
			{
				for (Widget widget : client.getWidget(239, 6).getChildren())
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
		musicCapeHelperPanel.updateAllMusicPanelRows();
	}

	public HashMap<Music, Boolean> filterMusicList()
	{
		HashMap<Music, Boolean> filteredList = new HashMap<>(musicList);

		//check one - does the music match the selected settings for quest discovered status
		if (!config.panelSettingLocked().equals(Locked.ALL))
		{
			if (config.panelSettingLocked().equals(Locked.LOCKED))
			{
				filteredList.entrySet().removeIf(b -> b.getValue());
			}
			else
			{
				filteredList.entrySet().removeIf(b -> !b.getValue());
			}
		}

		//check two - does the music match the selected settings for the selected region
		if (!config.panelSettingRegion().equals(Region.ALL))
		{
			filteredList.entrySet().removeIf(r -> !r.getKey().getRegion().equals(config.panelSettingRegion()));
		}

		//check three - does the music match the selected settings for the selected quest option
		if (!config.panelSettingQuest().equals(Quest.ALL))
		{
			if (config.panelSettingQuest().equals(Quest.NOT_QUEST_UNLOCK))
			{
				filteredList.entrySet().removeIf(q -> q.getKey().isQuest());
			}
			else
			{
				filteredList.entrySet().removeIf(q -> !q.getKey().isQuest());
			}
		}

		//check four - does the music match the selected settings for the selected optional option
		if (!config.panelSettingOptional().equals(Optional.ALL))
		{
			if (config.panelSettingOptional().equals(Optional.OPTIONAL_ONLY))
			{
				filteredList.entrySet().removeIf(q -> q.getKey().isRequired());
			}
			else
			{
				filteredList.entrySet().removeIf(q -> !q.getKey().isRequired());
			}
		}
		log.info(String.valueOf("filer 3 " + musicList.size()));
		return filteredList;
	}

	public void rowClicked(MusicCapeHelperPanelMusicRow row)
	{
		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
		{
			return;
		}

		MusicCapeHelperWorldMapPoint check = mapPoints.stream().filter(m -> m.music == row.getMusic()).findAny().orElse(null);

		if (check == null)
		{
			mapPoints.add(new MusicCapeHelperWorldMapPoint(row.getMusic(), row.isCompleted()));
		}
		else
		{
			mapPoints.remove(check);
		}

		updateMarkersOnMap();
	}

	public void updateMarkersOnMap()
	{
		worldMapPointManager.removeIf(MusicCapeHelperWorldMapPoint.class::isInstance);
		for (MusicCapeHelperWorldMapPoint point : mapPoints)
		{
			worldMapPointManager.add(new MusicCapeHelperWorldMapPoint(point.music, point.completed));
		}
	}

	@Provides
	MusicCapeHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MusicCapeHelperConfig.class);
	}
}
