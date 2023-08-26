package com.musiccapehelper;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.musiccapehelper.enums.HeaderType;
import com.musiccapehelper.enums.settings.SettingsLocked;
import com.musiccapehelper.enums.Music;
import com.musiccapehelper.enums.settings.SettingsOptional;
import com.musiccapehelper.enums.settings.SettingsOrderBy;
import com.musiccapehelper.enums.settings.SettingsQuest;
import com.musiccapehelper.enums.settings.SettingsRegion;
import com.musiccapehelper.ui.rows.MusicCapeHelperHeader;
import com.musiccapehelper.ui.panels.MusicCapeHelperPanel;
import com.musiccapehelper.ui.rows.MusicCapeHelperMusicRow;
import com.musiccapehelper.ui.rows.MusicCapeHelperRow;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import javax.swing.ImageIcon;
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
	private MusicCapeHelperPanel musicCapeHelperPanel;
	private MusicCapeHelperAccess musicCapeHelperAccess;
	@Getter
	private List<MusicCapeHelperWorldMapPoint> mapPoints;
	@Getter
	private List<Music> expandedRows;
	private HashMap<Music, Boolean> musicList;
	@Getter
	private Music hintArrowMusic;
	//icons
	private final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/pluginicon.png");
	@Getter
	private final ImageIcon addIcon = new ImageIcon(ImageUtil.loadImageResource(getClass(), "/addicon.png"));
	@Getter
	private final ImageIcon removeIcon = new ImageIcon(ImageUtil.loadImageResource(getClass(), "/removeicon.png"));
	@Getter
	private final ImageIcon upIcon = new ImageIcon(ImageUtil.loadImageResource(getClass(), "/up_icon.png"));
	@Getter
	private final ImageIcon downIcon = new ImageIcon(ImageUtil.loadImageResource(getClass(), "/down_icon.png"));
	@Getter
	private final ImageIcon hintArrowShow = new ImageIcon(ImageUtil.loadImageResource(getClass(), "/arrow_show.png"));
	@Getter
	private final ImageIcon hintArrowHide = new ImageIcon(ImageUtil.loadImageResource(getClass(), "/arrow_hide.png"));


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
		musicCapeHelperAccess = new MusicCapeHelperAccess(config, configManager, gson, this);

		musicList = new HashMap<>();

		for (Music music : Music.values())
		{
			musicList.put(music, false);
		}
		hintArrowMusic = null;
		expandedRows = musicCapeHelperAccess.loadExpandedRows();
		mapPoints = musicCapeHelperAccess.loadMapMarkers();
		updateMapPoints();


		musicCapeHelperPanel = new MusicCapeHelperPanel(this, config, itemManager, clientThread);
		navigationButton = NavigationButton.builder()
			.tooltip("Music Cape Helper Panel")
			.icon(icon)
			.panel(musicCapeHelperPanel)
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
		worldMapPointManager.removeIf(MusicCapeHelperWorldMapPoint.class::isInstance);
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
			musicCapeHelperPanel.updateAllRows();
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

	public HashMap<Music, Boolean> getOriginalMusicList()
	{
		return musicList;
	}

	public boolean isPlayerLoggedIn()
	{
		return client.getGameState().equals(GameState.LOGGED_IN);
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
		musicCapeHelperPanel.createAndRefreshRows("");
		this.updateMapPoints();
	}

	public HashMap<Music, Boolean> filterMusicList()
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

	public void rowPinClicked(MusicCapeHelperRow row)
	{
		if (row instanceof MusicCapeHelperMusicRow)
		{
			MusicCapeHelperWorldMapPoint check = mapPoints.stream().filter(m -> m.music == row.getMusic()).findAny().orElse(null);

			//checks if the world map should be updated
			if (check == null)
			{
				mapPoints.add(new MusicCapeHelperWorldMapPoint(row.getMusic(), ((MusicCapeHelperMusicRow) row).isCompleted(), config));
			}
			else
			{
				mapPoints.remove(check);
			}

			musicCapeHelperPanel.updateRow(row);
			musicCapeHelperPanel.updateHeader(row);
		}

		else if (row instanceof MusicCapeHelperHeader)
		{
			//use the row name to determine what is updated in combo with the settings
			if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION))
			{
				if (row.isEnabled())
				{
					musicCapeHelperPanel.getPanelRows().stream()
						.filter(r -> r.getMusic().getSettingsRegion().equals(((MusicCapeHelperHeader) row).getHeaderType().getSettingsRegion()))
						.filter(r -> r.isEnabled())
						.forEach(r ->
						{
							mapPoints.remove(mapPoints.stream().filter(m -> m.music.equals(r.getMusic())).findFirst().orElse(null));
							musicCapeHelperPanel.updateRow(r);
						});
				}
				else
				{
					musicCapeHelperPanel.getPanelRows().stream()
						.filter(r -> r instanceof MusicCapeHelperMusicRow)
						.filter(r -> r.getMusic().getSettingsRegion().equals(((MusicCapeHelperHeader) row).getHeaderType().getSettingsRegion()))
						.filter(r -> !r.isEnabled())
						.forEach(r ->
						{
							mapPoints.add(new MusicCapeHelperWorldMapPoint(r.getMusic(), ((MusicCapeHelperMusicRow) r).isCompleted(), config));
							musicCapeHelperPanel.updateRow(r);
						});
				}
			}
			else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
			{
				if (((MusicCapeHelperHeader) row).getHeaderType().equals(HeaderType.REQUIRED))
				{
					if (row.isEnabled())
					{
						musicCapeHelperPanel.getPanelRows().stream()
							.filter(r -> r.getMusic().isRequired())
							.filter(r -> r.isEnabled())
							.forEach(r ->
							{
								mapPoints.remove(mapPoints.stream().filter(m -> m.music.equals(r.getMusic())).findFirst().orElse(null));
								musicCapeHelperPanel.updateRow(r);
							});
					}
					else
					{
						musicCapeHelperPanel.getPanelRows().stream()
							.filter(r -> r instanceof MusicCapeHelperMusicRow)
							.filter(r -> r.getMusic().isRequired())
							.filter(r -> !r.isEnabled())
							.forEach(r ->
							{
								mapPoints.add(new MusicCapeHelperWorldMapPoint(r.getMusic(), ((MusicCapeHelperMusicRow) r).isCompleted(), config));
								musicCapeHelperPanel.updateRow(r);
							});
					}
				}
				else if (((MusicCapeHelperHeader) row).getHeaderType().equals(HeaderType.OPTIONAL))
				{
					if (row.isEnabled())
					{
						musicCapeHelperPanel.getPanelRows().stream()
							.filter(r -> !r.getMusic().isRequired())
							.filter(r -> r.isEnabled())
							.forEach(r ->
							{
								mapPoints.remove(mapPoints.stream().filter(m -> m.music.equals(r.getMusic())).findFirst().orElse(null));
								musicCapeHelperPanel.updateRow(r);
							});
					}
					else
					{
						musicCapeHelperPanel.getPanelRows().stream()
							.filter(r -> r instanceof MusicCapeHelperMusicRow)
							.filter(r -> !r.getMusic().isRequired())
							.filter(r -> !r.isEnabled())
							.forEach(r ->
							{
								mapPoints.add(new MusicCapeHelperWorldMapPoint(r.getMusic(), ((MusicCapeHelperMusicRow) r).isCompleted(), config));
								musicCapeHelperPanel.updateRow(r);
							});
					}
				}
			}
			musicCapeHelperPanel.updateHeader(row);
		}
		updateMarkersOnMap();
	}

	public void rowExpandClicked(MusicCapeHelperRow row)
	{
		if (row instanceof MusicCapeHelperMusicRow)
		{
			boolean found = getExpandedRows().stream().anyMatch(e -> e.equals(row.getMusic()));
			loginfo(found + "bool");
			//checks if the world map should be updated
			if (!found)
			{
				expandedRows.add(row.getMusic());
			}
			else
			{
				expandedRows.remove(row.getMusic());
			}

			musicCapeHelperPanel.updateRow(row);
			musicCapeHelperAccess.saveExpandedRows(expandedRows);
		}
	}

	public void updateMapPoints()
	{
		//checks to see if the map point has been completed or not
		mapPoints.forEach(p -> {
			musicList.entrySet().forEach(m -> {
				if (m.getKey().equals(p.getMusic()))
				{
					p.setCompleted(m.getValue());
				}
			});
		});
		mapPoints.forEach(MusicCapeHelperWorldMapPoint::setMapPointImage);
		updateMarkersOnMap();
	}

	//this both adds and removes the markers, to update the map, add or remove from the mapPoint list
	public void updateMarkersOnMap()
	{
		worldMapPointManager.removeIf(MusicCapeHelperWorldMapPoint.class::isInstance);
		mapPoints.forEach(p -> worldMapPointManager.add(p));
		musicCapeHelperAccess.saveMapMarkers(mapPoints);
	}

	//clears the hint arrow and either sets it to null or updates the arrow to the new music unlock point
	public void setHintArrow(MusicCapeHelperRow row)
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
		musicCapeHelperPanel.updateAllRows();
	}

	//todo delete
	public void loginfo(String s)
	{
		log.info(s);
	}

	@Provides
	MusicCapeHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MusicCapeHelperConfig.class);
	}
}
