package com.musiccapehelper;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.musiccapehelper.data.MusicExpandedRows;
import com.musiccapehelper.data.MusicHintArrow;
import com.musiccapehelper.data.MusicList;
import com.musiccapehelper.data.MusicMapPoints;
import com.musiccapehelper.data.MusicPanelRows;
import com.musiccapehelper.enums.data.IconData;
import com.musiccapehelper.ui.map.MusicWorldMapPoint;
import com.musiccapehelper.ui.panels.Panel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.WidgetLoaded;
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
	private Panel panel;
	private MusicMapPoints musicMapPoints;
	private MusicExpandedRows musicExpandedRows;
	private MusicList musicList;
	private MusicPanelRows musicPanelRows;
	private MusicHintArrow musicHintArrow;

	/*
		Row Creation
		1) When the plugin is enabled a list of a music enums is created in musicList.
		2) Once start up has complete, updateMusicList is called which then causes a propertyChangeEvent to be fired.
		3) This calls the panel which calls MusicPanelRows to create all rows found by the musicList object
		4) The panel then adds these to the Panel PanelRows section.
		5) updateMusicList will continue to fire propertyChangedEvent if settings or changes to the music list.
		6) Other row specific changes (pin, expand and hint arrow) will lead to a similar propertyChangedEvents, however this will instead update all rows.
	 */

	@Override
	protected void startUp() throws Exception
	{
		musicList = new MusicList(config);
		musicHintArrow = new MusicHintArrow(this, client);
		musicPanelRows = new MusicPanelRows(this, config, musicList, itemManager, clientThread);
		musicMapPoints = new MusicMapPoints(config, musicPanelRows, musicList, worldMapPointManager, configManager, gson);
		musicExpandedRows = new MusicExpandedRows(configManager, gson);

		//this is so that rows can now get the information they require
		musicPanelRows.updateRowDependencies(musicMapPoints, musicExpandedRows, musicHintArrow);

		PropertyChangeListener propertyChangeListener = new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt.getPropertyName().equals("musicList"))
				{
					panel.addRowsToPanel("");
					panel.checkMapRowPanels();
					musicMapPoints.updateMapPoints();
				}
				else
				{
					log.info("update called");
					panel.updateAllRows();
				}
			}
		};

		musicList.addPropertyChangeListener(propertyChangeListener);
		musicHintArrow.addPropertyChangeListener(propertyChangeListener);
		musicExpandedRows.addPropertyChangeListener(propertyChangeListener);
		musicMapPoints.addPropertyChangeListener(propertyChangeListener);

		panel = new Panel(this, config, musicList, musicPanelRows, musicMapPoints, musicExpandedRows, musicHintArrow);
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
		musicMapPoints.saveMapMarkers();
		musicExpandedRows.saveExpandedRows();

		clientToolbar.removeNavigation(navigationButton);
		worldMapPointManager.removeIf(MusicWorldMapPoint.class::isInstance);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (configChanged.getKey().equals("differentiateQuestMarkers") || configChanged.getKey().equals("differentiateCompletedMarkers"))
		{
			clientThread.invokeAtTickEnd(musicMapPoints::updateMapPoints);
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
		else if (configChanged.getKey().equals("panelAllowSetArrow") ||configChanged.getKey().equals("panelIncludeDescription"))
		{
			clientThread.invokeAtTickEnd(musicList::updateMusicList);
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
			//todo - remove hint arrow of that type and to use updateMusicTrack
			clientThread.invokeAtTickEnd(musicList::updateMusicList);
		}
	}

	public boolean isPlayerLoggedIn()
	{
		return client.getGameState().equals(GameState.LOGGED_IN);
	}

	@Provides
	MusicCapeHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MusicCapeHelperConfig.class);
	}
}
