package com.musiccapehelper.ui.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.HeaderType;
import com.musiccapehelper.enums.Music;
import com.musiccapehelper.enums.OrderBy;
import com.musiccapehelper.ui.rows.MusicCapeHelperHeader;
import com.musiccapehelper.ui.rows.MusicCapeMapRow;
import com.musiccapehelper.ui.rows.MusicCapeHelperMusicRow;
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
	private List<MusicCapeHelperMusicRow> musicRows;
	@Getter
	private List<MusicCapeMapRow> mapRows;

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
	public List<MusicCapeHelperMusicRow> createMusicRows()
	{
		List<MusicCapeHelperMusicRow> musicListRow = new ArrayList<>();
		plugin.filterMusicList().forEach((key, value) -> musicListRow.add(new MusicCapeHelperMusicRow(key, value, plugin, config)));
		return musicListRow;
	}

	//used to create the rows based on the search value
	public List<MusicCapeHelperMusicRow> createMusicRowsFromSearch(String search)
	{
		List<MusicCapeHelperMusicRow> musicListRow = new ArrayList<>();
		plugin.getOriginalMusicList().entrySet()
			.stream()
			.filter(s -> StringUtils.containsIgnoreCase(s.getKey().getSongName(), search))
			.forEach(e -> musicListRow.add(new MusicCapeHelperMusicRow(e.getKey(), e.getValue(), plugin, config)));
		return musicListRow;
	}

	public List<MusicCapeMapRow> createMapRows()
	{
		List<MusicCapeMapRow> mapRowList = new ArrayList<>();
		plugin.getMapPoints().forEach(point ->
			{
				plugin.filterMusicList().forEach((key, value) ->
				{
					if (key.equals(point.getMusic()))
					{
						mapRowList.add(new MusicCapeMapRow(key, value, plugin, config));
					}
				});
			}
		);

		return mapRowList;
	}

	public List<MusicCapeMapRow> createMapRowsFromSearch(String search)
	{
		List<MusicCapeMapRow> mapRowList = new ArrayList<>();
		plugin.getMapPoints()
			.stream()
			.filter(s -> StringUtils.containsIgnoreCase(s.getMusic().getSongName(), search))
			.forEach(e -> mapRowList.add(new MusicCapeMapRow(e.getMusic(), e.isCompleted(), plugin, config)));
		return mapRowList;
	}

	public List<MusicCapeHelperMusicRow> sortMusicRows(List<MusicCapeHelperMusicRow> unsortedList)
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

	public List<MusicCapeMapRow> sortMapRows(List<MusicCapeMapRow> unsortedList)
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
			for (MusicCapeHelperMusicRow row : sortMusicRows(musicRows))
			{
				//this checks the order by and then added a header for each section
				if (config.panelSettingOrderBy().equals(OrderBy.REGION) || config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST)
					|| config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
				{
					if (musicRows.indexOf(row) == 0)
					{
						musicPanel.add(new MusicCapeHelperHeader(config, row.getMusic(), plugin));
					}
					else if (config.panelSettingOrderBy().equals(OrderBy.REGION)
						&& !musicRows.get(musicRows.indexOf(row)-1).getMusic().getRegion().equals(row.getMusic().getRegion()))
					{
						musicPanel.add(new MusicCapeHelperHeader(config, row.getMusic(), plugin));
					}
					else if ((config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
					&& !musicRows.get(musicRows.indexOf(row)-1).getMusic().isRequired() == row.getMusic().isRequired())
					{
						musicPanel.add(new MusicCapeHelperHeader(config, row.getMusic(), plugin));
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
			for (MusicCapeMapRow row : sortMapRows(mapRows))
			{
				mapPanel.add(row);
			}
		}
	}

	public void updateMusicRow(Music music, boolean isOnMap)
	{
		//there should not be multiple rows of music
		Arrays.stream(musicPanel.getComponents())
			.filter(r -> r instanceof MusicCapeHelperMusicRow)
			.filter(r -> ((MusicCapeHelperMusicRow) r).getMusic().equals(music))
			.forEach(r -> ((MusicCapeHelperMusicRow) r).setEnabledDisabled(isOnMap));

		Arrays.stream(mapPanel.getComponents())
			.filter(r -> r instanceof MusicCapeMapRow)
			.filter(r -> ((MusicCapeMapRow) r).getMusic().equals(music))
			.forEach(r ->
			{
				((MusicCapeMapRow) r).setEnabledDisabled(isOnMap);
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
			.filter(r -> r instanceof MusicCapeHelperHeader)
			.forEach(r ->
			{
				HeaderType headerType = ((MusicCapeHelperHeader) r).getHeaderType();
				//checks to see if there are any matches to the region that of the label that are not enabled
				if (config.panelSettingOrderBy().equals(OrderBy.REGION))
				{
					if (Arrays.stream(musicPanel.getComponents())
						.filter(s -> s instanceof MusicCapeHelperMusicRow)
						.filter(s -> ((MusicCapeHelperMusicRow) s).getMusic().getRegion().equals(headerType.getRegion()))
						.anyMatch(s -> !s.isEnabled()))
					{
						//if there is one or more matches, then set the header to false (+)
						((MusicCapeHelperHeader) r).setHeader(false);
					}
					else
					{
						//other set it to true (-)
						((MusicCapeHelperHeader) r).setHeader(true);
					}
				}
				else if (config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
				{
					if (headerType.equals(HeaderType.REQUIRED) && Arrays.stream(musicPanel.getComponents())
						.filter(s -> s instanceof MusicCapeHelperMusicRow)
						.filter(s -> ((MusicCapeHelperMusicRow) s).getMusic().isRequired())
						.anyMatch(s -> !s.isEnabled()))
					{
						//if there is one or more matches, then set the header to false
						((MusicCapeHelperHeader) r).setHeader(false);
					}
					else if (headerType.equals(HeaderType.OPTIONAL) && Arrays.stream(musicPanel.getComponents())
						.filter(s -> s instanceof MusicCapeHelperMusicRow)
						.filter(s -> !((MusicCapeHelperMusicRow) s).getMusic().isRequired())
						.anyMatch(s -> !s.isEnabled()))
					{
						//if there is one or more matches, then set the header to false
						((MusicCapeHelperHeader) r).setHeader(false);
					}
					else
					{
						((MusicCapeHelperHeader) r).setHeader(true);
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
