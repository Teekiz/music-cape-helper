package com.musiccapehelper.ui.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.MusicCapeHelperWorldMapPoint;
import com.musiccapehelper.enums.HeaderType;
import com.musiccapehelper.enums.Music;
import com.musiccapehelper.enums.OrderBy;
import com.musiccapehelper.ui.rows.MusicCapeHelperMusicHeaderRow;
import com.musiccapehelper.ui.rows.MusicCapeHelperPanelMapRow;
import com.musiccapehelper.ui.rows.MusicCapeHelperPanelMusicRow;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
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
	@Getter
	private List<MusicCapeHelperPanelMusicRow> musicRows;
	@Getter
	private List<MusicCapeHelperPanelMapRow> mapRows;

	public MusicCapeHelperPanel(MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config)
	{
		this.plugin = plugin;
		this.config = config;

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

	//used to create all rows that match the filters
	public List<MusicCapeHelperPanelMusicRow> createMusicRows()
	{
		List<MusicCapeHelperPanelMusicRow> musicListRow = new ArrayList<>();
		plugin.filterMusicList().forEach((key, value) -> musicListRow.add(new MusicCapeHelperPanelMusicRow(key, value, plugin, config)));
		return musicListRow;
	}

	//used to create the rows based on the search value
	public List<MusicCapeHelperPanelMusicRow> createMusicRowsFromSearch(String search)
	{
		List<MusicCapeHelperPanelMusicRow> musicListRow = new ArrayList<>();
		plugin.getOriginalMusicList().entrySet()
			.stream()
			.filter(s -> StringUtils.containsIgnoreCase(s.getKey().getSongName(), search))
			.forEach(e -> musicListRow.add(new MusicCapeHelperPanelMusicRow(e.getKey(), e.getValue(), plugin, config)));
		return musicListRow;
	}

	public List<MusicCapeHelperPanelMapRow> createMapRows()
	{
		List<MusicCapeHelperPanelMapRow> mapRowList = new ArrayList<>();
		plugin.getMapPoints().forEach(point ->
			{
				plugin.filterMusicList().forEach((key, value) ->
				{
					if (key.equals(point.getMusic()))
					{
						mapRowList.add(new MusicCapeHelperPanelMapRow(key, value, plugin, config));
					}
				});
			}
		);

		return mapRowList;
	}

	public List<MusicCapeHelperPanelMapRow> createMapRowsFromSearch(String search)
	{
		List<MusicCapeHelperPanelMapRow> mapRowList = new ArrayList<>();
		plugin.getMapPoints()
			.stream()
			.filter(s -> StringUtils.containsIgnoreCase(s.getMusic().getSongName(), search))
			.forEach(e -> mapRowList.add(new MusicCapeHelperPanelMapRow(e.getMusic(), e.isCompleted(), plugin, config)));
		return mapRowList;
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

	public List<MusicCapeHelperPanelMapRow> sortMapRows(List<MusicCapeHelperPanelMapRow> unsortedList)
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
						musicPanel.add(new MusicCapeHelperMusicHeaderRow(config, row.getMusic(), plugin));
					}
					else if (config.panelSettingOrderBy().equals(OrderBy.REGION)
						&& !musicRows.get(musicRows.indexOf(row)-1).getMusic().getRegion().equals(row.getMusic().getRegion()))
					{
						musicPanel.add(new MusicCapeHelperMusicHeaderRow(config, row.getMusic(), plugin));
					}
					else if ((config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
					&& !musicRows.get(musicRows.indexOf(row)-1).getMusic().isRequired() == row.getMusic().isRequired())
					{
						musicPanel.add(new MusicCapeHelperMusicHeaderRow(config, row.getMusic(), plugin));
					}
				}
				musicPanel.add(row);
			}
		}
	}

	public void addMapRows()
	{
		if (mapPanel != null)
		{
			for (MusicCapeHelperPanelMapRow row : sortMapRows(mapRows))
			{
				mapPanel.add(row);
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

		Arrays.stream(mapPanel.getComponents())
			.filter(r -> r instanceof MusicCapeHelperPanelMapRow)
			.filter(r -> ((MusicCapeHelperPanelMapRow) r).getMusic().equals(music))
			.forEach(r ->
			{
				((MusicCapeHelperPanelMapRow) r).setEnabledDisabled(isOnMap);
				if (!r.isEnabled())
				{
					SwingUtilities.invokeLater(() ->
					{
						mapPanel.remove(r);
						mapPanel.revalidate();
						mapPanel.repaint();
					});
				}
			});
	}

	public void checkAndUpdateAllMusicRowHeader()
	{
		Arrays.stream(musicPanel.getComponents())
			.filter(r -> r instanceof MusicCapeHelperMusicHeaderRow)
			.forEach(r ->
			{
				HeaderType headerType = ((MusicCapeHelperMusicHeaderRow) r).getHeaderType();
				//checks to see if there are any matches to the region that of the label that are not enabled
				if (config.panelSettingOrderBy().equals(OrderBy.REGION))
				{
					if (Arrays.stream(musicPanel.getComponents())
						.filter(s -> s instanceof MusicCapeHelperPanelMusicRow)
						.filter(s -> ((MusicCapeHelperPanelMusicRow) s).getMusic().getRegion().equals(headerType.getRegion()))
						.anyMatch(s -> !s.isEnabled()))
					{
						//if there is one or more matches, then set the header to false (+)
						((MusicCapeHelperMusicHeaderRow) r).setHeader(false);
					}
					else
					{
						//other set it to true (-)
						((MusicCapeHelperMusicHeaderRow) r).setHeader(true);
					}
				}
				else if (config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
				{
					if (headerType.equals(HeaderType.REQUIRED) && Arrays.stream(musicPanel.getComponents())
						.filter(s -> s instanceof MusicCapeHelperPanelMusicRow)
						.filter(s -> ((MusicCapeHelperPanelMusicRow) s).getMusic().isRequired())
						.anyMatch(s -> !s.isEnabled()))
					{
						//if there is one or more matches, then set the header to false
						((MusicCapeHelperMusicHeaderRow) r).setHeader(false);
					}
					else if (headerType.equals(HeaderType.OPTIONAL) && Arrays.stream(musicPanel.getComponents())
						.filter(s -> s instanceof MusicCapeHelperPanelMusicRow)
						.filter(s -> !((MusicCapeHelperPanelMusicRow) s).getMusic().isRequired())
						.anyMatch(s -> !s.isEnabled()))
					{
						//if there is one or more matches, then set the header to false
						((MusicCapeHelperMusicHeaderRow) r).setHeader(false);
					}
					else
					{
						((MusicCapeHelperMusicHeaderRow) r).setHeader(true);
					}
				}
			});
	}

	public void musicSearch(String searchText)
	{
		SwingUtilities.invokeLater(() ->
		{
			musicPanel.removeAllMusicRows();
			mapPanel.removeAllMusicRows();
			musicRows = createMusicRowsFromSearch(searchText);
			mapRows = createMapRowsFromSearch(searchText);
			addMusicRows();
			addMapRows();
			checkAndUpdateAllMusicRowHeader();
			musicPanel.revalidate();
			musicPanel.repaint();
			mapPanel.revalidate();
			mapPanel.repaint();
		});
	}

	public void updateAllMusicPanelRows()
	{
		SwingUtilities.invokeLater(() ->
			{
				musicPanel.removeAllMusicRows();
				mapPanel.removeAllMusicRows();
				musicRows = createMusicRows();
				mapRows = createMapRows();
				addMusicRows();
				addMapRows();
				checkAndUpdateAllMusicRowHeader();
				musicPanel.revalidate();
				musicPanel.repaint();
				mapPanel.revalidate();
				mapPanel.repaint();
			});
	}
}
