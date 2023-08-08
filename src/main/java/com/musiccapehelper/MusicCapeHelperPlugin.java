package com.musiccapehelper;

import com.google.inject.Provides;
import com.musiccapehelper.enums.Music;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
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
	@Getter @Setter
	private HashMap<Music, Boolean> musicList;
	private List<MusicCapeHelperWorldMapPoint> mapPoints;


	//button at bottom of panel to add list
	//instead of multiple lists, create a hashmap at start up

	@Override
	protected void startUp() throws Exception
	{
		//todo - check at startup for config
		if (config.musicList() == null)
		{
			musicList = new HashMap<>();
			for (Music music : Music.values())
			{
				musicList.put(music, false);
			}
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

		mapPoints = new ArrayList<>();
	}

	@Override
	protected void shutDown() throws Exception
	{

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
		for (Widget widget : client.getWidget(239, 6).getChildren())
		{
			for (Music music : Music.values())
			{
				if (widget.getText().equals(music.getSongName()))
				{
					if (Integer.toHexString(widget.getTextColor()).equals("dc10d"))
					{
						musicList.put(music, true);
					}
				}
			}
		}
		musicCapeHelperPanel.updateAllMusicPanelRows();
	}

	public void filterMusicList(Music music, boolean completed)
	{

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
