package com.musiccapehelper.data;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.settings.SettingsOrderBy;
import com.musiccapehelper.ui.rows.HeaderRow;
import com.musiccapehelper.ui.rows.MusicRow;
import com.musiccapehelper.ui.rows.Row;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import org.apache.commons.lang3.StringUtils;

public class MusicPanelRows
{
	private final List<Row> rows = new ArrayList<>();
	private final MusicCapeHelperPlugin plugin;
	private final MusicCapeHelperConfig config;
	private final MusicList musicList;
	private final ItemManager itemManager;
	private final ClientThread clientThread;

	private MusicMapPoints musicMapPoints;
	private MusicExpandedRows musicExpandedRows;
	private MusicHintArrow musicHintArrow;

	public MusicPanelRows(MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config, MusicList musicList, ItemManager itemManager, ClientThread clientThread)
	{
		this.plugin = plugin;
		this.config = config;
		this.musicList = musicList;
		this.itemManager = itemManager;
		this.clientThread = clientThread;

		musicMapPoints = null;
		musicExpandedRows = null;
	}

	public void updateRowDependencies(MusicMapPoints musicMapPoints, MusicExpandedRows musicExpandedRows, MusicHintArrow musicHintArrow)
	{
		this.musicMapPoints = musicMapPoints;
		this.musicExpandedRows = musicExpandedRows;
		this.musicHintArrow = musicHintArrow;
	}

	public void createMusicRows(String searchText)
	{
		rows.clear();
		//creates a list from the music enums, sorts the music by the filter and then adds headers to it.
		if (searchText.isEmpty())
		{
			musicList.getFilteredMusicList().forEach((key, value) -> rows.add(new MusicRow(key, value, plugin, config,
				itemManager, clientThread, this, musicMapPoints, musicExpandedRows, musicHintArrow)));
			sortMusicRows();
			addRowHeaders();
		}
		else
		{
			musicList.getDefaultMusicList().entrySet()
				.stream()
				.filter(s -> StringUtils.containsIgnoreCase(s.getKey().getSongName(), searchText))
				.forEach(e -> rows.add(new MusicRow(e.getKey(), e.getValue(), plugin, config,
					itemManager, clientThread, this, musicMapPoints, musicExpandedRows, musicHintArrow)));
			sortMusicRows();
		}
	}

	public void sortMusicRows()
	{
		if (config.panelSettingOrderBy().equals(SettingsOrderBy.AZ))
		{
			rows.sort(Comparator.comparing(s -> s.getMusicData().getSongName()));
		}
		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.ZA))
		{
			rows.sort((s1, s2) -> s2.getMusicData().getSongName().compareTo(s1.getMusicData().getSongName()));
		}

		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION))
		{
			rows.sort(Comparator.comparing(s -> s.getMusicData().getSettingsRegion().getName()));
		}

		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
		{
			rows.sort(Comparator.comparing(s -> s.getMusicData().getSongName()));
			rows.sort((s1, s2) -> Boolean.compare(s1.getMusicData().isRequired(), s2.getMusicData().isRequired()));
		}

		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST))
		{
			rows.sort(Comparator.comparing(s -> s.getMusicData().getSongName()));
			rows.sort((s1, s2) -> Boolean.compare(s2.getMusicData().isRequired(), s1.getMusicData().isRequired()));
		}
	}

	public void addRowHeaders()
	{
		TreeMap<Integer, HeaderRow> headersToAdd = new TreeMap<>();
		for (Row row : rows)
		{
			if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION) || config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST)
				|| config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
			{
				if (rows.indexOf(row) == 0)
				{
					headersToAdd.put(rows.indexOf(row), new HeaderRow(row.getMusicData(), config, this, musicMapPoints, musicExpandedRows));
				}
				else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION)
					&& !rows.get(rows.indexOf(row) - 1).getMusicData().getSettingsRegion().equals(row.getMusicData().getSettingsRegion()))
				{
					headersToAdd.put(rows.indexOf(row), new HeaderRow(row.getMusicData(), config, this, musicMapPoints, musicExpandedRows));
				}
				else if ((config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
					&& !rows.get(rows.indexOf(row) - 1).getMusicData().isRequired() == row.getMusicData().isRequired())
				{
					headersToAdd.put(rows.indexOf(row), new HeaderRow(row.getMusicData(), config, this, musicMapPoints, musicExpandedRows));
				}
			}
		}

		//adds at a specific index backwards from the insertion order so that the index doesn't change for lower entries.
		for (Map.Entry<Integer, HeaderRow> entry : headersToAdd.descendingMap().entrySet())
		{
			rows.add(entry.getKey(), entry.getValue());
		}
	}

	public List<Row> getRows()
	{
		return rows;
	}
}
