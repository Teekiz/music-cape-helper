package com.musiccapehelper;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.musiccapehelper.enums.data.HeaderType;
import com.musiccapehelper.enums.data.Icon;
import com.musiccapehelper.enums.settings.SettingsLocked;
import com.musiccapehelper.enums.data.Music;
import com.musiccapehelper.enums.settings.SettingsOptional;
import com.musiccapehelper.enums.settings.SettingsOrderBy;
import com.musiccapehelper.enums.settings.SettingsQuest;
import com.musiccapehelper.enums.settings.SettingsRegion;
import com.musiccapehelper.ui.map.MusicWorldMapPoint;
import com.musiccapehelper.ui.rows.HeaderRow;
import com.musiccapehelper.ui.panels.Panel;
import com.musiccapehelper.ui.rows.MusicRow;
import com.musiccapehelper.ui.rows.Row;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
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
	private MusicCapeHelperAccess musicCapeHelperAccess;
	@Getter
	private List<MusicWorldMapPoint> mapPoints;
	@Getter
	private List<Music> expandedRows;
	@Getter
	private MusicCollection musicList;
	@Getter
	private Music hintArrowMusic;

	//button at bottom of panel to add list
	//instead of multiple lists, create a hashmap at start up

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
		musicCapeHelperAccess = new MusicCapeHelperAccess(config, configManager, gson);
		musicList = new MusicCollection(config);

		hintArrowMusic = null;
		expandedRows = musicCapeHelperAccess.loadExpandedRows();
		mapPoints = musicCapeHelperAccess.loadMapMarkers();
		updateMapPoints();

		panel = new Panel(this, config, itemManager, clientThread);
		navigationButton = NavigationButton.builder()
			.tooltip("Music Cape Helper Panel")
			.icon(Icon.PLUGIN_ICON.getImage())
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navigationButton);
		clientThread.invokeAtTickEnd(this::updateMusicList);
	}

	@Override
	protected void shutDown() throws Exception
	{
		musicCapeHelperAccess.saveMapMarkers(mapPoints);
		musicCapeHelperAccess.saveExpandedRows(expandedRows);
		clientToolbar.removeNavigation(navigationButton);
		worldMapPointManager.removeIf(MusicWorldMapPoint.class::isInstance);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (configChanged.getKey().equals("differentiateQuestMarkers") || configChanged.getKey().equals("differentiateCompletedMarkers"))
		{
			clientThread.invokeAtTickEnd(this::updateMapPoints);
		}
		else if (configChanged.getKey().equals("worldMapMarkers"))
		{
			//if the map markers are saved, do nothing
		}
		else if (configChanged.getKey().equals("panelSettingLocked") || configChanged.getKey().equals("panelSettingQuest") ||
			configChanged.getKey().equals("panelSettingRegion") || configChanged.getKey().equals("panelSettingOptional") ||
			configChanged.getKey().equals("panelSettingOrderBy"))
		{
			clientThread.invokeAtTickEnd(this::updateMusicList);
		}
		else if (configChanged.getKey().equals("panelDefaultTextColour") || configChanged.getKey().equals("panelCompleteTextColour") ||
			configChanged.getKey().equals("panelIncompleteTextColour"))
		{
			panel.updateAllRows();
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		//todo - update this to use WidgetInfo.MUSIC_TRACK_LIST
		if (widgetLoaded.getGroupId() != 239)
		{
			return;
		}

		clientThread.invokeAtTickEnd(this::updateMusicList);
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		//todo update
		if (chatMessage.getType().equals(ChatMessageType.GAMEMESSAGE)
			&& chatMessage.getMessage().startsWith("You have unlocked a new music track: "))
		{
			//todo - remove hint arrow of that type
			clientThread.invokeAtTickEnd(this::updateMusicList);
		}
	}

	public boolean isPlayerLoggedIn()
	{
		return client.getGameState().equals(GameState.LOGGED_IN);
	}

	//todo - update this
	public void updateMusicList()
	{
		if (client.getWidget(239, 6) != null)
		{

		}

		panel.createAndRefreshRows("");
		this.updateMapPoints();
	}

	//consider moving to event handling class
	public void rowPinClicked(Row row)
	{
		if (row instanceof MusicRow)
		{
			MusicWorldMapPoint check = mapPoints.stream().filter(m -> m.getMusic() == row.getMusic()).findAny().orElse(null);

			//checks if the world map should be updated
			if (check == null)
			{
				mapPoints.add(new MusicWorldMapPoint(row.getMusic(), ((MusicRow) row).isCompleted(), config));
			}
			else
			{
				mapPoints.remove(check);
			}

			panel.updateRow(row);
			panel.updateHeader(row);
		}

		else if (row instanceof HeaderRow)
		{
			//use the row name to determine what is updated in combo with the settings
			if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION))
			{
				if (row.isEnabled())
				{
					panel.getPanelRows().stream()
						.filter(r -> r.getMusic().getSettingsRegion().equals(((HeaderRow) row).getHeaderType().getSettingsRegion()))
						.filter(r -> r.isEnabled())
						.forEach(r ->
						{
							mapPoints.remove(mapPoints.stream().filter(m -> m.getMusic().equals(r.getMusic())).findFirst().orElse(null));
							panel.updateRow(r);
						});
				}
				else
				{
					panel.getPanelRows().stream()
						.filter(r -> r instanceof MusicRow)
						.filter(r -> r.getMusic().getSettingsRegion().equals(((HeaderRow) row).getHeaderType().getSettingsRegion()))
						.filter(r -> !r.isEnabled())
						.forEach(r ->
						{
							mapPoints.add(new MusicWorldMapPoint(r.getMusic(), ((MusicRow) r).isCompleted(), config));
							panel.updateRow(r);
						});
				}
			}
			else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
			{
				if (((HeaderRow) row).getHeaderType().equals(HeaderType.REQUIRED))
				{
					if (row.isEnabled())
					{
						panel.getPanelRows().stream()
							.filter(r -> r.getMusic().isRequired())
							.filter(r -> r.isEnabled())
							.forEach(r ->
							{
								mapPoints.remove(mapPoints.stream().filter(m -> m.getMusic().equals(r.getMusic())).findFirst().orElse(null));
								panel.updateRow(r);
							});
					}
					else
					{
						panel.getPanelRows().stream()
							.filter(r -> r instanceof MusicRow)
							.filter(r -> r.getMusic().isRequired())
							.filter(r -> !r.isEnabled())
							.forEach(r ->
							{
								mapPoints.add(new MusicWorldMapPoint(r.getMusic(), ((MusicRow) r).isCompleted(), config));
								panel.updateRow(r);
							});
					}
				}
				else if (((HeaderRow) row).getHeaderType().equals(HeaderType.OPTIONAL))
				{
					if (row.isEnabled())
					{
						panel.getPanelRows().stream()
							.filter(r -> !r.getMusic().isRequired())
							.filter(r -> r.isEnabled())
							.forEach(r ->
							{
								mapPoints.remove(mapPoints.stream().filter(m -> m.getMusic().equals(r.getMusic())).findFirst().orElse(null));
								panel.updateRow(r);
							});
					}
					else
					{
						panel.getPanelRows().stream()
							.filter(r -> r instanceof MusicRow)
							.filter(r -> !r.getMusic().isRequired())
							.filter(r -> !r.isEnabled())
							.forEach(r ->
							{
								mapPoints.add(new MusicWorldMapPoint(r.getMusic(), ((MusicRow) r).isCompleted(), config));
								panel.updateRow(r);
							});
					}
				}
			}
			panel.updateHeader(row);
		}
		panel.checkMapRowPanels();
		addMapPointsToMap();
	}

	public void rowExpandClicked(Row row)
	{
		if (row instanceof MusicRow)
		{
			boolean found = getExpandedRows().stream().anyMatch(e -> e.equals(row.getMusic()));
			//checks if the world map should be updated
			if (!found)
			{
				expandedRows.add(row.getMusic());
			}
			else
			{
				expandedRows.remove(row.getMusic());
			}

			panel.updateRow(row);
			musicCapeHelperAccess.saveExpandedRows(expandedRows);
		}
	}

	//this both adds and removes the markers, to update the map, add or remove from the mapPoint list
	public void updateMapPoints()
	{
		//checks to see if the map point has been completed or not
		mapPoints.forEach(p -> {
			musicList.getDefaultMusicList().entrySet().forEach(m -> {
				if (m.getKey().equals(p.getMusic()))
				{
					p.setCompleted(m.getValue());
				}
			});
		});

		mapPoints.forEach(MusicWorldMapPoint::setMapPointImage);
		addMapPointsToMap();
	}

	public void addMapPointsToMap()
	{
		worldMapPointManager.removeIf(MusicWorldMapPoint.class::isInstance);
		mapPoints.forEach(p -> worldMapPointManager.add(p));
		musicCapeHelperAccess.saveMapMarkers(mapPoints);
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
		if (hintArrowMusic != null && hintArrowMusic.equals(row.getMusic()))
		{
			hintArrowMusic = null;
		}
		else
		{
			hintArrowMusic = row.getMusic();
			client.setHintArrow(row.getMusic().getSongUnlockPoint());
		}
		panel.updateAllRows();
	}

	public void addOrRemoveAllMapPoints(boolean addMapPoint)
	{
		//if addMapPoint is true, add all the existing rows to the map
		if (addMapPoint)
		{
			panel.getPanelRows()
				.stream()
				.filter(r -> r instanceof MusicRow)
				.filter(r -> !r.isEnabled())
				.forEach(r -> mapPoints.add(new MusicWorldMapPoint(r.getMusic(), ((MusicRow) r).isCompleted(), config)));
		}
		//if it is false, remove all the rows
		else
		{
			mapPoints.clear();
		}

		addMapPointsToMap();
		panel.updateAllRows();
		musicCapeHelperAccess.saveMapMarkers(mapPoints);
	}

	public void addOrRemoveAllExpandedRows(boolean expandRow)
	{
		//if expandRow is true, expand all rows
		if (expandRow)
		{
			panel.getPanelRows()
				.stream()
				.filter(r -> r instanceof MusicRow)
				.filter(r -> !r.isExpanded())
				.forEach(r -> expandedRows.add(r.getMusic()));
		}
		//if false, shrink/hide all rows
		else
		{
			expandedRows.clear();
		}

		panel.updateAllRows();
		musicCapeHelperAccess.saveExpandedRows(expandedRows);
	}

	@Provides
	MusicCapeHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MusicCapeHelperConfig.class);
	}
}
