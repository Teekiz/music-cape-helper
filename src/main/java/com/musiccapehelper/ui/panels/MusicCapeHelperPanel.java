package com.musiccapehelper.ui.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.HeaderType;
import com.musiccapehelper.enums.settings.SettingsOrderBy;
import com.musiccapehelper.ui.rows.MusicCapeHelperHeader;
import com.musiccapehelper.ui.rows.MusicCapeHelperMusicRow;
import com.musiccapehelper.ui.rows.MusicCapeHelperRow;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import org.apache.commons.lang3.StringUtils;

public class MusicCapeHelperPanel extends PluginPanel
{
	private final MusicCapeHelperPlugin plugin;
	private final MusicCapeHelperConfig config;
	private final MusicCapeHelperMusicPanel musicPanel;
	private final MusicCapeHelperMapPanel mapPanel;
	private ItemManager itemManager;
	private ClientThread clientThread;
	//todo - consider making it polymorhpic and then added everything to music rows.
	//then when it needs to be updated, call the list instead of components
	@Getter
	private final List<MusicCapeHelperRow> panelRows = new ArrayList<>();

	public MusicCapeHelperPanel(MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config, ItemManager itemManager, ClientThread clientThread)
	{
		this.plugin = plugin;
		this.config = config;
		this.itemManager = itemManager;
		this.clientThread = clientThread;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 10, 10, 10));

		//title panel
		JPanel titlePanel = new JPanel();
		JLabel titleLabel = new JLabel("Music Cape Helper");
		titleLabel.setFont(FontManager.getRunescapeBoldFont());
		titlePanel.add(titleLabel);
		add(titlePanel);

		add(new MusicCapeHelperSettingsPanel(plugin, config, this));

		musicPanel = new MusicCapeHelperMusicPanel(plugin, config);
		mapPanel = new MusicCapeHelperMapPanel(plugin, config);
		JPanel displayPanel = new JPanel();

		MaterialTabGroup musicMapTabGroup = new MaterialTabGroup(displayPanel);
		MaterialTab musicTab = new MaterialTab("Music", musicMapTabGroup, musicPanel);
		MaterialTab mapTab = new MaterialTab("Map", musicMapTabGroup, mapPanel);

		musicMapTabGroup.addTab(musicTab);
		musicMapTabGroup.select(musicTab);
		musicMapTabGroup.addTab(mapTab);
		add(musicMapTabGroup, BorderLayout.NORTH);
		add(displayPanel, BorderLayout.CENTER);
	}

	//used to create all rows that match the filters or if the contents of the search bar
	public void createMusicRows(String searchText)
	{
		panelRows.clear();
		if (searchText.isEmpty())
		{
			plugin.filterMusicList().forEach((key, value) -> panelRows.add(new MusicCapeHelperMusicRow(key, value, plugin, config, itemManager, clientThread)));
		}
		else
		{
			plugin.getOriginalMusicList().entrySet()
				.stream()
				.filter(s -> StringUtils.containsIgnoreCase(s.getKey().getSongName(), searchText))
				.forEach(e -> panelRows.add(new MusicCapeHelperMusicRow(e.getKey(), e.getValue(), plugin, config, itemManager, clientThread)));
		}

		//sorts the music by the filter and then adds headers to it.
		sortMusicRows();
		addRowHeaders();

		panelRows.forEach(musicPanel::add);
	}

	public void sortMusicRows()
	{
		if (config.panelSettingOrderBy().equals(SettingsOrderBy.AZ))
		{
			panelRows.sort(Comparator.comparing(s -> s.getMusic().getSongName()));
		}
		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.ZA))
		{
			panelRows.sort((s1, s2) -> s2.getMusic().getSongName().compareTo(s1.getMusic().getSongName()));
		}

		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION))
		{
			panelRows.sort(Comparator.comparing(s -> s.getMusic().getSettingsRegion().getName()));
		}

		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
		{
			panelRows.sort(Comparator.comparing(s -> s.getMusic().getSongName()));
			panelRows.sort((s1, s2) -> Boolean.compare(s1.getMusic().isRequired(), s2.getMusic().isRequired()));
		}

		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST))
		{
			panelRows.sort(Comparator.comparing(s -> s.getMusic().getSongName()));
			panelRows.sort((s1, s2) -> Boolean.compare(s2.getMusic().isRequired(), s1.getMusic().isRequired()));
		}
	}

	public void addRowHeaders()
	{
		TreeMap<Integer, MusicCapeHelperHeader> headersToAdd = new TreeMap<>();
		for (MusicCapeHelperRow row : panelRows)
		{
			if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION) || config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST)
				|| config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
			{
				if (panelRows.indexOf(row) == 0)
				{
					headersToAdd.put(panelRows.indexOf(row), new MusicCapeHelperHeader(row.getMusic(), plugin, config));
				}
				else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION)
					&& !panelRows.get(panelRows.indexOf(row) - 1).getMusic().getSettingsRegion().equals(row.getMusic().getSettingsRegion()))
				{
					headersToAdd.put(panelRows.indexOf(row), new MusicCapeHelperHeader(row.getMusic(), plugin, config));
				}
				else if ((config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
					&& !panelRows.get(panelRows.indexOf(row) - 1).getMusic().isRequired() == row.getMusic().isRequired())
				{
					headersToAdd.put(panelRows.indexOf(row), new MusicCapeHelperHeader(row.getMusic(), plugin, config));
				}
			}
		}

		//adds at a specific index backwards from the insertion order so that the index doesn't change for lower entries.
		for (Map.Entry<Integer, MusicCapeHelperHeader> entry : headersToAdd.descendingMap().entrySet())
		{
			panelRows.add(entry.getKey(), entry.getValue());
		}
	}

	public void updateHeader(MusicCapeHelperRow row)
	{
		if (config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
		{
			if (row.getMusic().isRequired())
			{
				panelRows.stream()
					.filter(r -> r instanceof MusicCapeHelperHeader)
					.filter(r -> ((MusicCapeHelperHeader) r).getHeaderType().equals(HeaderType.REQUIRED))
					.findFirst().ifPresent(MusicCapeHelperRow::updateRow);
			}
			else
			{
				panelRows.stream()
					.filter(r -> r instanceof MusicCapeHelperHeader)
					.filter(r -> ((MusicCapeHelperHeader) r).getHeaderType().equals(HeaderType.OPTIONAL))
					.findFirst().ifPresent(MusicCapeHelperRow::updateRow);
			}
		}
		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION))
		{
			panelRows.stream()
				.filter(r -> r instanceof MusicCapeHelperHeader)
				.filter(r -> ((MusicCapeHelperHeader) r).getHeaderType().getSettingsRegion().equals(row.getMusic().getSettingsRegion()))
				.findFirst().ifPresent(MusicCapeHelperRow::updateRow);
		}
	}

	public void updateRow(MusicCapeHelperRow row)
	{
		row.updateRow();
	}

	public void updateAllRows()
	{
		panelRows.forEach(MusicCapeHelperRow::updateRow);
	}

	//this method is used to completely update the panel, with new rows being added where required.
	public void createAndRefreshRows(String searchText)
	{
		SwingUtilities.invokeLater(() ->
			{
				musicPanel.removeAllMusicRows();
				mapPanel.removeAllMusicRows();
				this.createMusicRows(searchText);
				musicPanel.revalidate();
				musicPanel.repaint();
				mapPanel.revalidate();
				mapPanel.repaint();
			});
	}
}
