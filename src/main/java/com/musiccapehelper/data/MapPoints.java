package com.musiccapehelper.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.data.HeaderType;
import com.musiccapehelper.enums.data.MusicData;
import com.musiccapehelper.enums.settings.SettingsOrderBy;
import com.musiccapehelper.ui.map.MusicWorldMapPoint;
import com.musiccapehelper.ui.rows.HeaderRow;
import com.musiccapehelper.ui.rows.MusicRow;
import com.musiccapehelper.ui.rows.Row;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

public class MapPoints
{
	private final List<MusicWorldMapPoint> mapPoints;
	private final MusicCapeHelperPlugin plugin;
	private final MusicCapeHelperConfig config;
	private final WorldMapPointManager worldMapPointManager;
	private final ConfigManager configManager;
	private final Gson gson;
	private final PropertyChangeSupport propertyChangeSupport
		= new PropertyChangeSupport(this);

	public MapPoints(MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config, WorldMapPointManager worldMapPointManager, ConfigManager configManager, Gson gson)
	{
		this.plugin = plugin;
		this.config = config;
		this.worldMapPointManager = worldMapPointManager;
		this.configManager = configManager;
		this.gson = gson;

		mapPoints = loadMapMarkers();
		updateMapPoints();
	}

	public List<MusicWorldMapPoint> getMapPoints()
	{
		return mapPoints;
	}

	public void rowPinClicked(Row row)
	{
		if (row instanceof MusicRow)
		{
			MusicWorldMapPoint check = mapPoints.stream().filter(m -> m.getMusicData() == row.getMusicData()).findAny().orElse(null);

			//checks if the world map should be updated
			if (check == null)
			{
				mapPoints.add(new MusicWorldMapPoint(row.getMusicData(), ((MusicRow) row).isCompleted(), config));
			}
			else
			{
				mapPoints.remove(check);
			}
		}

		else if (row instanceof HeaderRow)
		{
			//use the row name to determine what is updated in combo with the settings
			if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION))
			{
				if (row.isEnabled())
				{
					plugin.getPanel().getRows().stream()
						.filter(r -> r.getMusicData().getSettingsRegion().equals(((HeaderRow) row).getHeaderType().getSettingsRegion()))
						.filter(r -> r.isEnabled())
						.forEach(r ->
						{
							mapPoints.remove(mapPoints.stream().filter(m -> m.getMusicData().equals(r.getMusicData())).findFirst().orElse(null));
						});
				}
				else
				{
					plugin.getPanel().getRows().stream()
						.filter(r -> r instanceof MusicRow)
						.filter(r -> r.getMusicData().getSettingsRegion().equals(((HeaderRow) row).getHeaderType().getSettingsRegion()))
						.filter(r -> !r.isEnabled())
						.forEach(r ->
						{
							mapPoints.add(new MusicWorldMapPoint(r.getMusicData(), ((MusicRow) r).isCompleted(), config));
						});
				}
			}
			else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
			{
				if (((HeaderRow) row).getHeaderType().equals(HeaderType.REQUIRED))
				{
					if (row.isEnabled())
					{
						plugin.getPanel().getRows().stream()
							.filter(r -> r.getMusicData().isRequired())
							.filter(r -> r.isEnabled())
							.forEach(r ->
							{
								mapPoints.remove(mapPoints.stream().filter(m -> m.getMusicData().equals(r.getMusicData())).findFirst().orElse(null));
							});
					}
					else
					{
						plugin.getPanel().getRows().stream()
							.filter(r -> r instanceof MusicRow)
							.filter(r -> r.getMusicData().isRequired())
							.filter(r -> !r.isEnabled())
							.forEach(r ->
							{
								mapPoints.add(new MusicWorldMapPoint(r.getMusicData(), ((MusicRow) r).isCompleted(), config));
							});
					}
				}
				else if (((HeaderRow) row).getHeaderType().equals(HeaderType.OPTIONAL))
				{
					if (row.isEnabled())
					{
						plugin.getPanel().getRows().stream()
							.filter(r -> !r.getMusicData().isRequired())
							.filter(r -> r.isEnabled())
							.forEach(r ->
							{
								mapPoints.remove(mapPoints.stream().filter(m -> m.getMusicData().equals(r.getMusicData())).findFirst().orElse(null));
							});
					}
					else
					{
						plugin.getPanel().getRows().stream()
							.filter(r -> r instanceof MusicRow)
							.filter(r -> !r.getMusicData().isRequired())
							.filter(r -> !r.isEnabled())
							.forEach(r ->
							{
								mapPoints.add(new MusicWorldMapPoint(r.getMusicData(), ((MusicRow) r).isCompleted(), config));
							});
					}
				}
			}
		}

		propertyChanged();
		addMapPointsToMap();

		plugin.getPanel().checkMapRowPanels();
	}

	public void addOrRemoveAllMapPoints(boolean addMapPoint)
	{
		//if addMapPoint is true, add all the existing rows to the map
		if (addMapPoint)
		{
			plugin.getPanel().getRows()
				.stream()
				.filter(r -> r instanceof MusicRow)
				.filter(r -> !r.isEnabled())
				.forEach(r -> mapPoints.add(new MusicWorldMapPoint(r.getMusicData(), ((MusicRow) r).isCompleted(), config)));
		}
		//if it is false, remove all the rows
		else
		{
			mapPoints.clear();
		}

		propertyChanged();
		addMapPointsToMap();

		plugin.getPanel().checkMapRowPanels();
	}

	//this both adds and removes the markers, to update the map, add or remove from the mapPoint list
	public void updateMapPoints()
	{
		//checks to see if the map point has been completed or not
		mapPoints.forEach(p -> {
			plugin.getMusicList().getDefaultMusicList().entrySet().forEach(m -> {
				if (m.getKey().equals(p.getMusicData()))
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
		mapPoints.forEach(worldMapPointManager::add);
		saveMapMarkers();
	}

	public void saveMapMarkers()
	{
		//overwrites the existing data
		configManager.unsetConfiguration("musicTrackInfoConfig", "worldMapMarkers");
		JsonArray mapData = new JsonArray();
		mapPoints.forEach(m -> {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("music", m.getMusicData().getSongName());
			jsonObject.addProperty("completed", m.isCompleted());
			mapData.add(jsonObject);
		});
		String json = mapData.toString();
		configManager.setConfiguration("musicTrackInfoConfig", "worldMapMarkers", json);
	}

	public List<MusicWorldMapPoint> loadMapMarkers()
	{
		List<MusicWorldMapPoint> point = new ArrayList<>();

		String json = configManager.getConfiguration("musicTrackInfoConfig", "worldMapMarkers");
		if (json != null)
		{
			for (JsonElement element : gson.fromJson(json, JsonArray.class))
			{
				String song = element.getAsJsonObject().get("music").getAsString();
				MusicData musicData = Arrays.stream(MusicData.values())
					.filter(m -> m.getSongName().equals(song))
					.findAny().orElse(null);

				boolean completed = element.getAsJsonObject().get("completed").getAsBoolean();

				if (musicData != null)
				{
					point.add(new MusicWorldMapPoint(musicData, completed, config));
				}
			}
		}
		return point;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	//todo change with new value
	public void propertyChanged()
	{
		propertyChangeSupport.firePropertyChange("mapPoints", null, null);
	}
}
