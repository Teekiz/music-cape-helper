package com.musiccapehelper.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.musiccapehelper.enums.data.MusicData;
import com.musiccapehelper.ui.rows.MusicRow;
import com.musiccapehelper.ui.rows.Row;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.client.config.ConfigManager;

public class MusicExpandedRows
{
	private final List<MusicData> expandedRows;
	private final ConfigManager configManager;
	private final Gson gson;

	private final PropertyChangeSupport propertyChangeSupport
		= new PropertyChangeSupport(this);

	public MusicExpandedRows(ConfigManager configManager, Gson gson)
	{
		this.configManager = configManager;
		this.gson = gson;

		expandedRows = loadExpandedRows();
	}

	public List<MusicData> getExpandedRows()
	{
		return expandedRows;
	}

	public void updateExpandedRows(MusicRow row)
	{
		boolean found = getExpandedRows().stream().anyMatch(e -> e.equals(row.getMusicData()));
		//checks if the world map should be updated
		if (!found)
		{
			expandedRows.add(row.getMusicData());
		}
		else
		{
			expandedRows.remove(row.getMusicData());
		}

		propertyChanged();
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
				.forEach(r -> expandedRows.add(r.getMusicData()));
		}
		//if false, shrink/hide all rows
		else
		{
			expandedRows.clear();
		}

		propertyChanged();
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

	public List<MusicData> loadExpandedRows()
	{
		List<MusicData> rows = new ArrayList<>();

		String json = configManager.getConfiguration("musicTrackInfoConfig", "expandedRows");
		if (json != null)
		{
			for (JsonElement element : gson.fromJson(json, JsonArray.class))
			{
				String song = element.getAsJsonObject().get("music").getAsString();
				Arrays.stream(MusicData.values())
					.filter(m -> m.getSongName().equals(song))
					.findAny().ifPresent(rows::add);
			}
		}
		return rows;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void propertyChanged()
	{
		propertyChangeSupport.firePropertyChange("expandedRows", null, null);
	}
}
