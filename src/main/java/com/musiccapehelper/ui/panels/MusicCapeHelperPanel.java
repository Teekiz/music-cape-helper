package com.musiccapehelper.ui.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.HeaderType;
import com.musiccapehelper.enums.OrderBy;
import com.musiccapehelper.ui.rows.MusicCapeHelperHeader;
import com.musiccapehelper.ui.rows.MusicCapeHelperMapRow;
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
	private final List<MusicCapeHelperMusicRow> musicRows = new ArrayList<>();
	@Getter
	private List<MusicCapeHelperMapRow> mapRows;

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

	//used to create all rows that match the filters or if the contents of the search bar
	public void createMusicRows(String searchText)
	{
		musicRows.clear();
		if (searchText.isEmpty())
		{
			plugin.filterMusicList().forEach((key, value) -> musicRows.add(new MusicCapeHelperMusicRow(key, value, plugin, config)));
		}
		else
		{
			plugin.getOriginalMusicList().entrySet()
				.stream()
				.filter(s -> StringUtils.containsIgnoreCase(s.getKey().getSongName(), searchText))
				.forEach(e -> musicRows.add(new MusicCapeHelperMusicRow(e.getKey(), e.getValue(), plugin, config)));
		}

		addMusicRows();
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
						musicPanel.add(new MusicCapeHelperHeader(row.getMusic(), plugin, config));
					}
					else if (config.panelSettingOrderBy().equals(OrderBy.REGION)
						&& !musicRows.get(musicRows.indexOf(row)-1).getMusic().getRegion().equals(row.getMusic().getRegion()))
					{
						musicPanel.add(new MusicCapeHelperHeader(row.getMusic(), plugin, config));
					}
					else if ((config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
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
		if (config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
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
		else if (config.panelSettingOrderBy().equals(OrderBy.REGION))
		{
			Arrays.stream(musicPanel.getComponents())
				.filter(r -> r instanceof MusicCapeHelperHeader)
				.filter(r -> ((MusicCapeHelperHeader) r).getHeaderType().getRegion().equals(row.getMusic().getRegion()))
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
