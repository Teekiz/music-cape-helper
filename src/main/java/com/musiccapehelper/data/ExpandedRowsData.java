package com.musiccapehelper.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.enums.data.Music;
import com.musiccapehelper.ui.rows.MusicRow;
import com.musiccapehelper.ui.rows.Row;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import net.runelite.client.config.ConfigManager;

public class ExpandedRowsData
{
	private final List<Music> expandedRows;
	private final ConfigManager configManager;
	private final Gson gson;

	public ExpandedRowsData(ConfigManager configManager, Gson gson)
	{
		this.configManager = configManager;
		this.gson = gson;

		expandedRows = loadExpandedRows();
	}

	public List<Music> getExpandedRows()
	{
		return expandedRows;
	}

	public void updateExpandedRows(MusicRow row)
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

		saveExpandedRows();
	}

	public void addOrRemoveAllExpandedRows(boolean expandRow, List<Row> rows)
	{
		//if expandRow is true, expand all rows
		if (expandRow)
		{
			rows.stream()
				.filter(r -> r instanceof MusicRow)
				.filter(r -> !r.isExpanded())
				.forEach(r -> expandedRows.add(r.getMusic()));
		}
		//if false, shrink/hide all rows
		else
		{
			expandedRows.clear();
		}

		saveExpandedRows();
	}

	public void saveExpandedRows()
	{
		//overwrites the existing data
		configManager.unsetConfiguration("musicTrackInfoConfig", "expandedRows");
		JsonArray expandedData = new JsonArray();
		expandedRows.forEach(m -> {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("music", m.getSongName());
			expandedData.add(jsonObject);
		});
		String json = expandedData.toString();
		configManager.setConfiguration("musicTrackInfoConfig", "expandedRows", json);
	}

	public List<Music> loadExpandedRows()
	{
		List<Music> rows = new ArrayList<>();

		String json = configManager.getConfiguration("musicTrackInfoConfig", "expandedRows");
		if (json != null)
		{
			for (JsonElement element : gson.fromJson(json, JsonArray.class))
			{
				String song = element.getAsJsonObject().get("music").getAsString();
				Arrays.stream(Music.values())
					.filter(m -> m.getSongName().equals(song))
					.findAny().ifPresent(rows::add);
			}
		}
		return rows;
	}
}
