package com.musiccapehelper.ui.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.HeaderType;
import com.musiccapehelper.enums.Music;
import com.musiccapehelper.enums.settings.SettingsOrderBy;
import com.musiccapehelper.ui.rows.MusicCapeHelperHeader;
import com.musiccapehelper.ui.rows.MusicCapeHelperMusicRow;
import com.musiccapehelper.ui.rows.MusicCapeHelperRow;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
	private final MusicCapeHelperRowPanel rowPanel;
	private ItemManager itemManager;
	private ClientThread clientThread;
	//then when it needs to be updated, call the list instead of components
	@Getter
	private final List<MusicCapeHelperRow> panelRows = new ArrayList<>();
	private final JPanel displayPanel = new JPanel();
	private final MaterialTabGroup musicMapTabGroup = new MaterialTabGroup(displayPanel);

	public MusicCapeHelperPanel(MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config, ItemManager itemManager, ClientThread clientThread)
	{
		this.plugin = plugin;
		this.config = config;
		this.itemManager = itemManager;
		this.clientThread = clientThread;

		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setLayout(new GridBagLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 10, 10, 10));

		//title panel
		JPanel titlePanel = new JPanel();
		JLabel titleLabel = new JLabel("Music Cape Helper");
		titleLabel.setFont(FontManager.getRunescapeBoldFont());
		titlePanel.add(titleLabel);
		add(titlePanel);

		add(new MusicCapeHelperSettingsPanel(plugin, config, this));

		rowPanel = new MusicCapeHelperRowPanel(plugin, config, false);
		MaterialTab musicTab = new MaterialTab("Music", musicMapTabGroup, rowPanel);
		MaterialTab mapTab = new MaterialTab("Map", musicMapTabGroup, rowPanel);

		musicMapTabGroup.addTab(musicTab);
		musicMapTabGroup.addTab(mapTab);
		musicMapTabGroup.select(musicTab);
		add(musicMapTabGroup, BorderLayout.NORTH);
		add(displayPanel, BorderLayout.CENTER);

		musicTab.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				rowPanel.tabSwitched(false);
			}
		});

		mapTab.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				rowPanel.tabSwitched(true);
			}
		});
	}

	//used to create all rows that match the filters or if the contents of the search bar
	public void createMusicRows(String searchText)
	{
		panelRows.clear();
		//creates a list from the music enums, sorts the music by the filter and then adds headers to it.
		if (searchText.isEmpty())
		{
			plugin.filterMusicList().forEach((key, value) -> panelRows.add(new MusicCapeHelperMusicRow(key, value, plugin, config, itemManager, clientThread)));
			sortMusicRows();
			addRowHeaders();
		}
		else
		{
			plugin.getOriginalMusicList().entrySet()
				.stream()
				.filter(s -> StringUtils.containsIgnoreCase(s.getKey().getSongName(), searchText))
				.forEach(e -> panelRows.add(new MusicCapeHelperMusicRow(e.getKey(), e.getValue(), plugin, config, itemManager, clientThread)));
			sortMusicRows();
		}

		rowPanel.addMusicRows(panelRows);

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
		plugin.loginfo("trhkhth Update header called " + row);

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
			plugin.loginfo("AAAAAAA region called " + row);

			panelRows.stream()
				.filter(r -> r instanceof MusicCapeHelperHeader)
				.filter(r -> ((MusicCapeHelperHeader) r).getHeaderType().getSettingsRegion().equals(row.getMusic().getSettingsRegion()))
				.findFirst().ifPresent(MusicCapeHelperRow::updateRow);
		}
		rowPanel.refreshList();
	}

	public MusicCapeHelperRow getRowByMusic(Music music)
	{
		 return panelRows.stream()
			.filter(r -> r instanceof MusicCapeHelperMusicRow)
			.filter(r -> r.getMusic().equals(music))
			 .findFirst().orElse(null);
	}

	public void updateRow(MusicCapeHelperRow row)
	{
		row.updateRow();
		rowPanel.refreshList();
	}

	public void updateAllRows()
	{
		panelRows.forEach(MusicCapeHelperRow::updateRow);
		rowPanel.refreshList();
	}

	//this verifies that only the correct rows are showing.
	public void checkMapRowPanels()
	{
		rowPanel.tabSwitched(rowPanel.isOnMapPanel());
	}

	//this method is used to completely update the panel, with new rows being added where required.
	public void createAndRefreshRows(String searchText)
	{
		SwingUtilities.invokeLater(() -> this.createMusicRows(searchText));
	}
}
