package com.musiccapehelper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.musiccapehelper.enums.Music;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;

public class MusicCapeHelperAccess
{
	private final MusicCapeHelperConfig config;
	private final ConfigManager configManager;
	private final Gson gson;

	/*
		This class is used to access the config file for more complex saved items (MapPoint data and Row Expanded Data).
	 */

	public MusicCapeHelperAccess(MusicCapeHelperConfig config, ConfigManager configManager, Gson gson)
	{
		this.config = config;
		this.configManager = configManager;
		this.gson = gson;

	}

	public void saveMapMarkers(List<MusicCapeHelperWorldMapPoint> saveMapPoints)
	{
		if (!saveMapPoints.isEmpty())
		{
			//overwrites the existing data
			configManager.unsetConfiguration("musicTracksWorldPoints", "worldMapMarkers");
			JsonArray mapData = new JsonArray();
			saveMapPoints.forEach(m -> {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("music", m.music.getSongName());
				jsonObject.addProperty("completed", m.completed);
				mapData.add(jsonObject);
			});
			String json = mapData.toString();
			configManager.setConfiguration("musicTracksWorldPoints", "worldMapMarkers", json);
		}
	}

	public List<MusicCapeHelperWorldMapPoint> loadMapMarkers()
	{
		List<MusicCapeHelperWorldMapPoint> point = new ArrayList<>();

		String json = configManager.getConfiguration("musicTracksWorldPoints", "worldMapMarkers");
		for (JsonElement element : gson.fromJson(json, JsonArray.class))
		{
			String song = element.getAsJsonObject().get("music").getAsString();
			Music music = Arrays.stream(Music.values())
				.filter(m -> m.getSongName().equals(song))
				.findAny().orElse(null);

			boolean completed = element.getAsJsonObject().get("completed").getAsBoolean();

			if (music != null)
			{
				point.add(new MusicCapeHelperWorldMapPoint(music, completed, config));
			}
		}

		return point;
	}
}
