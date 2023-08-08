package com.musiccapehelper;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.api.worldmap.WorldMapData;
import net.runelite.api.worldmap.WorldMapRenderer;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.worldmap.WorldMapPlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.worldmap.WorldMapOverlay;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.ImageUtil;
import org.w3c.dom.events.Event;

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
		List<Widget> musicListFiltered = Arrays.stream(client.getWidget(239, 6).getChildren())
			.collect(Collectors.toList());

		for (Widget widget : musicListFiltered)
		{
			for (Music music : musicList.keySet())
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
			addMarkerToMap();
		}
		else
		{
			mapPoints.remove(check);
			removeMarkerFromMap(check);
		}
	}

	public void addMarkerToMap()
	{
		worldMapPointManager.removeIf(MusicCapeHelperWorldMapPoint.class::isInstance);
		for (MusicCapeHelperWorldMapPoint point : mapPoints)
		{
			worldMapPointManager.add(new MusicCapeHelperWorldMapPoint(point.music, point.completed));
		}
	}

	public void removeMarkerFromMap(MusicCapeHelperWorldMapPoint point)
	{
		worldMapPointManager.remove(point);
	}

	@Provides
	MusicCapeHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MusicCapeHelperConfig.class);
	}
}
