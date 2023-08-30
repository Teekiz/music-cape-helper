package com.musiccapehelper;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.musiccapehelper.data.ExpandedRows;
import com.musiccapehelper.data.MapPoints;
import com.musiccapehelper.data.Music;
import com.musiccapehelper.enums.data.HeaderType;
import com.musiccapehelper.enums.data.IconData;
import com.musiccapehelper.enums.data.MusicData;
import com.musiccapehelper.enums.settings.SettingsOrderBy;
import com.musiccapehelper.ui.map.MusicWorldMapPoint;
import com.musiccapehelper.ui.rows.HeaderRow;
import com.musiccapehelper.ui.panels.Panel;
import com.musiccapehelper.ui.rows.MusicRow;
import com.musiccapehelper.ui.rows.Row;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Observable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

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
	private ClientToolbar clientToolbar;
	@Inject
	private WorldMapPointManager worldMapPointManager;
	@Inject
	private ConfigManager configManager;
	@Inject
	private ItemManager itemManager;
	@Inject
	private ClientThread clientThread;
	@Inject
	private Gson gson;
	private NavigationButton navigationButton;
	@Getter
	private Panel panel;
	@Getter
	//private List<MusicWorldMapPoint> mapPoints;
	private MapPoints mapPoints;
	@Getter
	private ExpandedRows expandedRows;
	@Getter
	private Music musicList;
	@Getter
	private MusicData hintArrowMusicData;


	//todo - update this
	/*
		Map Marker Creation
		1) When the plugin is enabled a list of a music enums is created.
		2) Update Music List is called, this checks to see if there has been any changes (requires the widget of music to be loaded)
		3) This calls the panel update method which adds new rows then places them on the panel
		4) Repeat step 2 for every update
	 */

	@Override
	protected void startUp() throws Exception
	{
		musicList = new Music(config);
		expandedRows = new ExpandedRows(configManager, gson);
		mapPoints = new MapPoints(this, config, worldMapPointManager,configManager, gson);

		PropertyChangeListener propertyChangeListener = new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt.getPropertyName().equals("musicList"))
				{
					panel.createAndRefreshRows("");
					mapPoints.updateMapPoints();
				}
				else
				{
					panel.updateAllRows();
				}
			}
		};

		musicList.addPropertyChangeListener(propertyChangeListener);
		expandedRows.addPropertyChangeListener(propertyChangeListener);
		mapPoints.addPropertyChangeListener(propertyChangeListener);

		hintArrowMusicData = null;

		panel = new Panel(this, config, itemManager, clientThread);
		navigationButton = NavigationButton.builder()
			.tooltip("Music Cape Helper Panel")
			.icon(IconData.PLUGIN_ICON.getImage())
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navigationButton);
		clientThread.invokeAtTickEnd(musicList::updateMusicList);
	}

	@Override
	protected void shutDown() throws Exception
	{
		mapPoints.saveMapMarkers();
		expandedRows.saveExpandedRows();

		clientToolbar.removeNavigation(navigationButton);
		worldMapPointManager.removeIf(MusicWorldMapPoint.class::isInstance);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (configChanged.getKey().equals("differentiateQuestMarkers") || configChanged.getKey().equals("differentiateCompletedMarkers"))
		{
			clientThread.invokeAtTickEnd(mapPoints::updateMapPoints);
		}
		else if (configChanged.getKey().equals("panelSettingLocked") || configChanged.getKey().equals("panelSettingQuest") ||
			configChanged.getKey().equals("panelSettingRegion") || configChanged.getKey().equals("panelSettingOptional") ||
			configChanged.getKey().equals("panelSettingOrderBy"))
		{
			clientThread.invokeAtTickEnd(musicList::updateMusicList);
		}
		else if (configChanged.getKey().equals("panelDefaultTextColour") || configChanged.getKey().equals("panelCompleteTextColour") ||
			configChanged.getKey().equals("panelIncompleteTextColour"))
		{
			panel.updateAllRows();
		}
		else if (configChanged.getKey().equals("worldMapMarkers"))
		{
			//if the map markers are saved, do nothing
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		if (widgetLoaded.getGroupId() != WidgetID.MUSIC_GROUP_ID)
		{
			return;

		}
		musicList.updateGameMusicWidget(client.getWidget(239, 6).getChildren());
		clientThread.invokeAtTickEnd(musicList::updateMusicList);
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		//todo update
		if (chatMessage.getType().equals(ChatMessageType.GAMEMESSAGE)
			&& chatMessage.getMessage().startsWith("You have unlocked a new music track: "))
		{
			//todo - remove hint arrow of that type
			clientThread.invokeAtTickEnd(musicList::updateMusicList);
		}
	}

	public boolean isPlayerLoggedIn()
	{
		return client.getGameState().equals(GameState.LOGGED_IN);
	}

	public void clearHintArrow()
	{
		client.clearHintArrow();
		panel.updateAllRows();
	}

	//clears the hint arrow and either sets it to null or updates the arrow to the new music unlock point
	public void setHintArrow(Row row)
	{
		client.clearHintArrow();

		//unsets the music
		if (hintArrowMusicData != null && hintArrowMusicData.equals(row.getMusicData()))
		{
			hintArrowMusicData = null;
		}
		else
		{
			hintArrowMusicData = row.getMusicData();
			client.setHintArrow(row.getMusicData().getSongUnlockPoint());
		}
		panel.updateAllRows();
	}

	@Provides
	MusicCapeHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MusicCapeHelperConfig.class);
	}
}
