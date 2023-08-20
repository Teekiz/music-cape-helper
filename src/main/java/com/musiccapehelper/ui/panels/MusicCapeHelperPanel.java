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
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
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
	@Getter
	private final List<MusicCapeHelperMusicRow> musicRows = new ArrayList<>();

	public MusicCapeHelperPanel(MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config, ItemManager itemManager, ClientThread clientThread)
	{
		this.plugin = plugin;
		this.config = config;
		this.itemManager = itemManager;
		this.clientThread = clientThread;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

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
		musicRows.clear();
		if (searchText.isEmpty())
		{
			plugin.filterMusicList().forEach((key, value) -> musicRows.add(new MusicCapeHelperMusicRow(key, value, plugin, config, itemManager, clientThread)));
		}
		else
		{
			plugin.getOriginalMusicList().entrySet()
				.stream()
				.filter(s -> StringUtils.containsIgnoreCase(s.getKey().getSongName(), searchText))
				.forEach(e -> musicRows.add(new MusicCapeHelperMusicRow(e.getKey(), e.getValue(), plugin, config, itemManager, clientThread)));
		}

		addMusicRows();
	}

	public List<MusicCapeHelperMusicRow> sortMusicRows(List<MusicCapeHelperMusicRow> unsortedList)
	{
		if (config.panelSettingOrderBy().equals(SettingsOrderBy.AZ))
		{
			unsortedList.sort(Comparator.comparing(s -> s.getMusic().getSongName()));
		}
		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.ZA))
		{
			unsortedList.sort((s1, s2) -> s2.getMusic().getSongName().compareTo(s1.getMusic().getSongName()));
		}

		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION))
		{
			unsortedList.sort(Comparator.comparing(s -> s.getMusic().getSettingsRegion().getName()));
		}

		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
		{
			unsortedList.sort(Comparator.comparing(s -> s.getMusic().getSongName()));
			unsortedList.sort((s1, s2) -> Boolean.compare(s1.getMusic().isRequired(), s2.getMusic().isRequired()));
		}

		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST))
		{
			unsortedList.sort(Comparator.comparing(s -> s.getMusic().getSongName()));
			unsortedList.sort((s1, s2) -> Boolean.compare(s2.getMusic().isRequired(), s1.getMusic().isRequired()));
		}

		return unsortedList;
	}

	public void addMusicRows()
	{
		if (musicPanel != null)
		{
			for (MusicCapeHelperMusicRow row : sortMusicRows(musicRows))
			{
				//this checks the order by and then added a header for each section
				if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION) || config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST)
					|| config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
				{
					if (musicRows.indexOf(row) == 0)
					{
						musicPanel.add(new MusicCapeHelperHeader(row.getMusic(), plugin, config));
					}
					else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION)
						&& !musicRows.get(musicRows.indexOf(row)-1).getMusic().getSettingsRegion().equals(row.getMusic().getSettingsRegion()))
					{
						musicPanel.add(new MusicCapeHelperHeader(row.getMusic(), plugin, config));
					}
					else if ((config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
					&& !musicRows.get(musicRows.indexOf(row)-1).getMusic().isRequired() == row.getMusic().isRequired())
					{
						musicPanel.add(new MusicCapeHelperHeader(row.getMusic(), plugin, config));
					}
				}
				musicPanel.add(row);
			}
		}
	}

	public void updateHeader(MusicCapeHelperRow row)
	{
		if (config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
		{
			if (row.getMusic().isRequired())
			{
				Arrays.stream(musicPanel.getComponents())
					.filter(r -> r instanceof MusicCapeHelperHeader)
					.filter(r -> ((MusicCapeHelperHeader) r).getHeaderType().equals(HeaderType.REQUIRED))
					.findFirst().ifPresent(r -> ((MusicCapeHelperHeader) r).updateRow());
			}
			else
			{
				Arrays.stream(musicPanel.getComponents())
					.filter(r -> r instanceof MusicCapeHelperHeader)
					.filter(r -> ((MusicCapeHelperHeader) r).getHeaderType().equals(HeaderType.OPTIONAL))
					.findFirst().ifPresent(r -> ((MusicCapeHelperHeader) r).updateRow());
			}
		}
		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION))
		{
			Arrays.stream(musicPanel.getComponents())
				.filter(r -> r instanceof MusicCapeHelperHeader)
				.filter(r -> ((MusicCapeHelperHeader) r).getHeaderType().getSettingsRegion().equals(row.getMusic().getSettingsRegion()))
				.findFirst().ifPresent(r -> ((MusicCapeHelperHeader) r).updateRow());
		}
	}

	public void updateRow(MusicCapeHelperRow row)
	{
		row.updateRow();
	}

	public void updateAllPanelRows(String searchText)
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
