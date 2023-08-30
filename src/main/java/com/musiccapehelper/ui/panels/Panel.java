package com.musiccapehelper.ui.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.data.HeaderType;
import com.musiccapehelper.enums.settings.SettingsOrderBy;
import com.musiccapehelper.ui.rows.HeaderRow;
import com.musiccapehelper.ui.rows.MusicRow;
import com.musiccapehelper.ui.rows.Row;
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

public class Panel extends PluginPanel
{
	private final MusicCapeHelperPlugin plugin;
	private final MusicCapeHelperConfig config;
	private final PanelRows rowPanel;
	private final ItemManager itemManager;
	private final ClientThread clientThread;
	@Getter
	private final List<Row> rows = new ArrayList<>();

	public Panel(MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config, ItemManager itemManager, ClientThread clientThread)
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

		add(new PanelSettings(plugin, config, this));

		rowPanel = new PanelRows(plugin, config, this, false);
		JPanel displayPanel = new JPanel();
		MaterialTabGroup musicMapTabGroup = new MaterialTabGroup(displayPanel);
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
		rows.clear();
		//creates a list from the music enums, sorts the music by the filter and then adds headers to it.
		if (searchText.isEmpty())
		{
			plugin.getMusicList().getFilteredMusicList().forEach((key, value) -> rows.add(new MusicRow(key, value, plugin, config, this, itemManager, clientThread)));
			sortMusicRows();
			addRowHeaders();
		}
		else
		{
			plugin.getMusicList().getDefaultMusicList().entrySet()
				.stream()
				.filter(s -> StringUtils.containsIgnoreCase(s.getKey().getSongName(), searchText))
				.forEach(e -> rows.add(new MusicRow(e.getKey(), e.getValue(), plugin, config, this, itemManager, clientThread)));
			sortMusicRows();
		}
		rowPanel.addMusicRows();
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
					headersToAdd.put(rows.indexOf(row), new HeaderRow(row.getMusicData(), plugin, config, this));
				}
				else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION)
					&& !rows.get(rows.indexOf(row) - 1).getMusicData().getSettingsRegion().equals(row.getMusicData().getSettingsRegion()))
				{
					headersToAdd.put(rows.indexOf(row), new HeaderRow(row.getMusicData(), plugin, config, this));
				}
				else if ((config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
					&& !rows.get(rows.indexOf(row) - 1).getMusicData().isRequired() == row.getMusicData().isRequired())
				{
					headersToAdd.put(rows.indexOf(row), new HeaderRow(row.getMusicData(), plugin, config, this));
				}
			}
		}

		//adds at a specific index backwards from the insertion order so that the index doesn't change for lower entries.
		for (Map.Entry<Integer, HeaderRow> entry : headersToAdd.descendingMap().entrySet())
		{
			rows.add(entry.getKey(), entry.getValue());
		}
	}

	public void updateHeader(Row row)
	{
		if (config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
		{
			if (row.getMusicData().isRequired())
			{
				rows.stream()
					.filter(r -> r instanceof HeaderRow)
					.filter(r -> ((HeaderRow) r).getHeaderType().equals(HeaderType.REQUIRED))
					.findFirst().ifPresent(Row::updateRowValues);
			}
			else
			{
				rows.stream()
					.filter(r -> r instanceof HeaderRow)
					.filter(r -> ((HeaderRow) r).getHeaderType().equals(HeaderType.OPTIONAL))
					.findFirst().ifPresent(Row::updateRowValues);
			}
		}
		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION))
		{
			rows.stream()
				.filter(r -> r instanceof HeaderRow)
				.filter(r -> ((HeaderRow) r).getHeaderType().getSettingsRegion().equals(row.getMusicData().getSettingsRegion()))
				.findFirst().ifPresent(Row::updateRowValues);
		}
		rowPanel.refreshList();
	}

	public void updateRow(Row row)
	{
		row.updateRowValues();
		rowPanel.refreshList();
	}

	public void updateAllRows()
	{
		rows.stream().filter(r -> r instanceof MusicRow).forEach(Row::updateRowValues);
		rows.stream().filter(r -> r instanceof HeaderRow).forEach(Row::updateRowValues);
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
