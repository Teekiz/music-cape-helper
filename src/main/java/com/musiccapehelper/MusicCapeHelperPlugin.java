package com.musiccapehelper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Provides;
import com.musiccapehelper.enums.Locked;
import com.musiccapehelper.enums.Music;
import com.musiccapehelper.enums.Optional;
import com.musiccapehelper.enums.OrderBy;
import com.musiccapehelper.enums.Quest;
import com.musiccapehelper.enums.Region;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
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
	@Inject
	private ConfigManager configManager;
	@Inject
	private Gson gson;
	@Getter @Setter
	private List<MusicCapeHelperWorldMapPoint> mapPoints;
	private NavigationButton navigationButton;
	private MusicCapeHelperPanel musicCapeHelperPanel;
	private HashMap<Music, Boolean> musicList;

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

		mapPoints = loadMapMarkers();
		mapPoints.forEach(m -> musicCapeHelperPanel.updateMusicRow(m.music, true));
		musicCapeHelperPanel.checkAndUpdateAllMusicRowHeader();
		updateMarkersOnMap();
	}

	@Override
	protected void shutDown() throws Exception
	{
		saveMapMarkers(mapPoints);
		clientToolbar.removeNavigation(navigationButton);
		worldMapPointManager.removeIf(MusicCapeHelperWorldMapPoint.class::isInstance);
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

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		//todo update
		if (chatMessage.getType().equals(ChatMessageType.GAMEMESSAGE)
			&& chatMessage.getMessage().startsWith("You have unlocked a new music track: "))
		{
			clientThread.invokeAtTickEnd(this::updateMusicList);
		}
	}

	public HashMap<Music, Boolean> getOriginalMusicList()
	{
		return musicList;
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
		return filteredList;
	}

	public void rowClicked(MusicCapeHelperPanelMusicRow row)
	{
		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
		{
			return;
		}

		MusicCapeHelperWorldMapPoint check = mapPoints.stream().filter(m -> m.music == row.getMusic()).findAny().orElse(null);

		//checks if the world map should be updated
		if (check == null)
		{
			mapPoints.add(new MusicCapeHelperWorldMapPoint(row.getMusic(), row.isCompleted()));
			musicCapeHelperPanel.updateMusicRow(row.getMusic(), true);
		}
		else
		{
			mapPoints.remove(check);
			musicCapeHelperPanel.updateMusicRow(row.getMusic(), false);
		}

		musicCapeHelperPanel.checkAndUpdateAllMusicRowHeader();
		updateMarkersOnMap();
	}

	public void rowHeaderClicked(MusicCapeHelperMusicRowHeader row)
	{
		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
		{
			return;
		}

		//use the row name to determine what is updated in combo with the settings
		if (config.panelSettingOrderBy().equals(OrderBy.REGION))
		{
			if (row.enabled)
			{
				musicCapeHelperPanel.getMusicRows().stream()
					.filter(r -> r.getMusic().getRegion().getName().equals(row.getRowLabel().getText()))
					.filter(r -> !r.isEnabled())
					.forEach(r ->
					{
						mapPoints.add(new MusicCapeHelperWorldMapPoint(r.getMusic(), r.isCompleted()));
						musicCapeHelperPanel.updateMusicRow(r.getMusic(), true);
					});
			}
			else
			{
				musicCapeHelperPanel.getMusicRows().stream()
					.filter(r -> r.getMusic().getRegion().getName().equals(row.getRowLabel().getText()))
					.filter(r -> r.isEnabled())
					.forEach(r ->
					{
						mapPoints.remove(mapPoints.stream().filter(m -> m.music.equals(r.getMusic())).findFirst().orElse(null));
						musicCapeHelperPanel.updateMusicRow(r.getMusic(), false);
					});
			}
		}
		else if (config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
		{
			if (row.getRowLabel().getText().equals("Required tracks: "))
			{
				if (row.enabled)
				{
					musicCapeHelperPanel.getMusicRows().stream()
						.filter(r -> r.getMusic().isRequired())
						.filter(r -> !r.isEnabled())
						.forEach(r ->
						{
							mapPoints.add(new MusicCapeHelperWorldMapPoint(r.getMusic(), r.isCompleted()));
							musicCapeHelperPanel.updateMusicRow(r.getMusic(), true);
						});
				}
				else
				{
					musicCapeHelperPanel.getMusicRows().stream()
						.filter(r -> r.getMusic().isRequired())
						.filter(r -> r.isEnabled())
						.forEach(r ->
						{
							mapPoints.remove(mapPoints.stream().filter(m -> m.music.equals(r.getMusic())).findFirst().orElse(null));
							musicCapeHelperPanel.updateMusicRow(r.getMusic(), false);
						});
				}
			}
			else if (row.getRowLabel().getText().equals("Optional tracks: "))
			{
				if (row.enabled)
				{
					musicCapeHelperPanel.getMusicRows().stream()
						.filter(r -> !r.getMusic().isRequired())
						.filter(r -> !r.isEnabled())
						.forEach(r ->
						{
							mapPoints.add(new MusicCapeHelperWorldMapPoint(r.getMusic(), r.isCompleted()));
							musicCapeHelperPanel.updateMusicRow(r.getMusic(), true);
						});
				}
				else
				{
					musicCapeHelperPanel.getMusicRows().stream()
						.filter(r -> !r.getMusic().isRequired())
						.filter(r -> r.isEnabled())
						.forEach(r ->
						{
							mapPoints.remove(mapPoints.stream().filter(m -> m.music.equals(r.getMusic())).findFirst().orElse(null));
							musicCapeHelperPanel.updateMusicRow(r.getMusic(), false);
						});
				}
			}
		}

		musicCapeHelperPanel.checkAndUpdateAllMusicRowHeader();
		updateMarkersOnMap();
	}

	//this both adds and removes the markers, to update the map, add or remove from the mapPoint list
	public void updateMarkersOnMap()
	{
		worldMapPointManager.removeIf(MusicCapeHelperWorldMapPoint.class::isInstance);
		for (MusicCapeHelperWorldMapPoint point : mapPoints)
		{
			worldMapPointManager.add(point);
		}
		saveMapMarkers(mapPoints);
	}

	public void saveMapMarkers(List<MusicCapeHelperWorldMapPoint> saveMapPoints)
	{
		if (!saveMapPoints.isEmpty())
		{
			//overwrites the existing data
			configManager.unsetConfiguration("musicTracksWorldPoints", "map_markers");
			JsonArray mapData = new JsonArray();
			saveMapPoints.forEach(m -> {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("music", m.music.getSongName());
				jsonObject.addProperty("completed", m.completed);
				mapData.add(jsonObject);
			});
			String json = mapData.toString();
			configManager.setConfiguration("musicTracksWorldPoints", "map_markers", json);
		}
	}

	public List<MusicCapeHelperWorldMapPoint> loadMapMarkers()
	{
		List<MusicCapeHelperWorldMapPoint> point = new ArrayList<>();

		String json = configManager.getConfiguration("musicTracksWorldPoints", "map_markers");
		for (JsonElement element : gson.fromJson(json, JsonArray.class))
		{
			String song = element.getAsJsonObject().get("music").getAsString();
			Music music = Arrays.stream(Music.values())
				.filter(m -> m.getSongName().equals(song))
				.findAny().orElse(null);
			boolean completed = element.getAsJsonObject().get("completed").getAsBoolean();
			if (music != null)
			{
				point.add(new MusicCapeHelperWorldMapPoint(music, completed));
			}
		}
		return point;
	}

	@Provides
	MusicCapeHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MusicCapeHelperConfig.class);
	}
}
