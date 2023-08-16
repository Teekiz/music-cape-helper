package com.musiccapehelper.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.Locked;
import com.musiccapehelper.enums.Music;
import com.musiccapehelper.enums.Optional;
import com.musiccapehelper.enums.OrderBy;
import com.musiccapehelper.enums.Quest;
import com.musiccapehelper.enums.Region;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import org.apache.commons.lang3.StringUtils;

public class MusicCapeHelperPanel extends PluginPanel
{
	private MusicCapeHelperPlugin plugin;
	private MusicCapeHelperConfig config;
	private JLabel titleLabel;
	private JPanel titlePanel;
	private MusicCapeHelperSettingsPanel settingsPanel;

	private MusicCapeHelperMusicPanel musicPanel;
	private MusicCapeHelperMapPanel mapPanel;
	private JPanel displayPanel;
	private MaterialTabGroup musicMapTabGroup;
	@Getter
	private List<MusicCapeHelperPanelMusicRow> musicRows;

	public MusicCapeHelperPanel(MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config)
	{
		this.plugin = plugin;
		this.config = config;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		//title panel
		titlePanel = new JPanel();
		titleLabel = new JLabel("Music Cape Helper");
		titleLabel.setFont(FontManager.getRunescapeBoldFont());
		titlePanel.add(titleLabel);
		add(titlePanel);

		add(settingsPanel = new MusicCapeHelperSettingsPanel(plugin, config, this));

		musicPanel = new MusicCapeHelperMusicPanel(plugin, config);
		mapPanel = new MusicCapeHelperMapPanel(plugin, config);
		displayPanel = new JPanel();

		musicMapTabGroup = new MaterialTabGroup(displayPanel);
		MaterialTab musicTab = new MaterialTab("Music", musicMapTabGroup, musicPanel);
		MaterialTab mapTab = new MaterialTab("Map", musicMapTabGroup, mapPanel);

		musicMapTabGroup.addTab(musicTab);
		musicMapTabGroup.select(musicTab);
		musicMapTabGroup.addTab(mapTab);
		add(musicMapTabGroup, BorderLayout.NORTH);
		add(displayPanel, BorderLayout.CENTER);
	}

	//used to create all rows that match the filters
	public List<MusicCapeHelperPanelMusicRow> createMusicRows()
	{
		List<MusicCapeHelperPanelMusicRow> musicListRow = new ArrayList<>();
		plugin.filterMusicList().forEach((key, value) -> musicListRow.add(new MusicCapeHelperPanelMusicRow(key, value, plugin)));
		return musicListRow;
	}

	//used to create the rows based on the search value
	public List<MusicCapeHelperPanelMusicRow> createMusicRowsFromSearch(String search)
	{
		List<MusicCapeHelperPanelMusicRow> musicListRow = new ArrayList<>();
		plugin.getOriginalMusicList().entrySet()
			.stream()
			.filter(s -> StringUtils.containsIgnoreCase(s.getKey().getSongName(), search))
			.forEach(e -> musicListRow.add(new MusicCapeHelperPanelMusicRow(e.getKey(), e.getValue(), plugin)));
		return musicListRow;
	}

	public List<MusicCapeHelperPanelMusicRow> sortMusicRows(List<MusicCapeHelperPanelMusicRow> unsortedList)
	{
		if (config.panelSettingOrderBy().equals(OrderBy.AZ))
		{
			unsortedList.sort(Comparator.comparing(s -> s.getMusic().getSongName()));
		}
		else if (config.panelSettingOrderBy().equals(OrderBy.ZA))
		{
			unsortedList.sort((s1, s2) -> s2.getMusic().getSongName().compareTo(s1.getMusic().getSongName()));
		}

		else if (config.panelSettingOrderBy().equals(OrderBy.REGION))
		{
			unsortedList.sort(Comparator.comparing(s -> s.getMusic().getRegion().getName()));
		}

		else if (config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
		{
			unsortedList.sort(Comparator.comparing(s -> s.getMusic().getSongName()));
			unsortedList.sort((s1, s2) -> Boolean.compare(s1.getMusic().isRequired(), s2.getMusic().isRequired()));
		}

		else if (config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST))
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
			for (MusicCapeHelperPanelMusicRow row : sortMusicRows(musicRows))
			{
				//this checks the order by and then added a header for each section
				if (config.panelSettingOrderBy().equals(OrderBy.REGION) || config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST)
					|| config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
				{
					if (musicRows.indexOf(row) == 0)
					{
						musicPanel.add(new MusicCapeHelperMusicRowHeader(config, row.getMusic(), plugin));
					}
					else if (config.panelSettingOrderBy().equals(OrderBy.REGION)
						&& !musicRows.get(musicRows.indexOf(row)-1).getMusic().getRegion().equals(row.getMusic().getRegion()))
					{
						musicPanel.add(new MusicCapeHelperMusicRowHeader(config, row.getMusic(), plugin));
					}
					else if ((config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
					&& !musicRows.get(musicRows.indexOf(row)-1).getMusic().isRequired() == row.getMusic().isRequired())
					{
						musicPanel.add(new MusicCapeHelperMusicRowHeader(config, row.getMusic(), plugin));
					}
				}
				musicPanel.add(row);
			}
		}
	}

	public void updateMusicRow(Music music, boolean isOnMap)
	{
		//there should not be multiple rows of music
		Arrays.stream(musicPanel.getComponents())
			.filter(r -> r instanceof MusicCapeHelperPanelMusicRow)
			.filter(r -> ((MusicCapeHelperPanelMusicRow) r).getMusic().equals(music))
			.forEach(r -> ((MusicCapeHelperPanelMusicRow) r).setEnabledDisabled(isOnMap));
	}

	public void checkAndUpdateAllMusicRowHeader()
	{
		Arrays.stream(musicPanel.getComponents())
			.filter(r -> r instanceof MusicCapeHelperMusicRowHeader)
			.forEach(r ->
			{
				String label = ((MusicCapeHelperMusicRowHeader) r).getRowTextType();
				//checks to see if there are any matches to the region that of the label that are not enabled
				if (config.panelSettingOrderBy().equals(OrderBy.REGION))
				{
					if (Arrays.stream(musicPanel.getComponents())
						.filter(s -> s instanceof MusicCapeHelperPanelMusicRow)
						.filter(s -> ((MusicCapeHelperPanelMusicRow) s).getMusic().getRegion().getName().equals(label))
						.anyMatch(s -> !s.isEnabled()))
					{
						//if there is one or more matches, then set the header to false (+)
						((MusicCapeHelperMusicRowHeader) r).setHeader(false);
					}
					else
					{
						//other set it to true (-)
						((MusicCapeHelperMusicRowHeader) r).setHeader(true);
					}
				}
				else if (config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
				{
					if (label.equals("Required tracks") && Arrays.stream(musicPanel.getComponents())
						.filter(s -> s instanceof MusicCapeHelperPanelMusicRow)
						.filter(s -> ((MusicCapeHelperPanelMusicRow) s).getMusic().isRequired())
						.anyMatch(s -> !s.isEnabled()))
					{
						//if there is one or more matches, then set the header to false
						((MusicCapeHelperMusicRowHeader) r).setHeader(false);
					}
					else if (label.equals("Optional tracks") && Arrays.stream(musicPanel.getComponents())
						.filter(s -> s instanceof MusicCapeHelperPanelMusicRow)
						.filter(s -> !((MusicCapeHelperPanelMusicRow) s).getMusic().isRequired())
						.anyMatch(s -> !s.isEnabled()))
					{
						//if there is one or more matches, then set the header to false
						((MusicCapeHelperMusicRowHeader) r).setHeader(false);
					}
					else
					{
						((MusicCapeHelperMusicRowHeader) r).setHeader(true);
					}
				}
			});
	}

	public void musicSearch(String searchText)
	{
		SwingUtilities.invokeLater(() ->
		{
			musicPanel.removeAllMusicRows();
			musicRows = createMusicRowsFromSearch(searchText);
			addMusicRows();
			checkAndUpdateAllMusicRowHeader();
			musicPanel.revalidate();
			musicPanel.repaint();
		});
	}

	public void updateAllMusicPanelRows()
	{
		SwingUtilities.invokeLater(() ->
			{
				musicPanel.removeAllMusicRows();
				musicRows = createMusicRows();
				addMusicRows();
				checkAndUpdateAllMusicRowHeader();
				musicPanel.revalidate();
				musicPanel.repaint();
			});
	}
}
