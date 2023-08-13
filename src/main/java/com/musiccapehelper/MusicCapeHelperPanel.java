package com.musiccapehelper;

import com.musiccapehelper.enums.Locked;
import com.musiccapehelper.enums.Music;
import com.musiccapehelper.enums.Optional;
import com.musiccapehelper.enums.OrderBy;
import com.musiccapehelper.enums.Quest;
import com.musiccapehelper.enums.Region;
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
import net.runelite.api.ChatMessageType;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;

public class MusicCapeHelperPanel extends PluginPanel
{
	private MusicCapeHelperPlugin plugin;
	private MusicCapeHelperConfig config;
	//settings and search components
	private IconTextField searchBar;
	private JPanel settingsPanel;
	private JComboBox<String> completedCombo;
	private JComboBox<String> regionCombo;
	private JComboBox<String> unlockedDuringQuestCombo;
	private JComboBox<String> includeOptionalCombo;
	private JComboBox<String> orderCombo;

	//music row components
	private JPanel musicPanel;
	private JPanel musicHeaderPanel;
	private List<MusicCapeHelperPanelMusicRow> musicRows;

	public MusicCapeHelperPanel(MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config)
	{
		this.plugin = plugin;
		this.config = config;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		buildSearchAndSettings();
		buildMusicPanel();
	}

	public void buildSearchAndSettings()
	{
		// -- Search Bar --
		searchBar = new IconTextField();
		searchBar.setIcon(IconTextField.Icon.SEARCH);
		searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		searchBar.setPreferredSize(new Dimension(100, 30));
		//todo - add listener

		//Order of the panel Components - Search Bar, Settings tab, Music List
		// -- Settings Panel --
		settingsPanel = new JPanel();
		settingsPanel.setLayout(new DynamicGridLayout(5, 2, 5, 5));
		settingsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
		settingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// -- Completed --
		JLabel completedLabel = new JLabel("Show: ");
		completedLabel.setToolTipText("Filters music tracks based on whether they have been discovered or not.");
		completedCombo = new JComboBox<>();
		completedCombo.setToolTipText("Filters music tracks based on whether they have been discovered or not.");
		completedLabel.setLabelFor(completedCombo);
		for (Locked locked : Locked.values()) {completedCombo.addItem(locked.getText());}
		completedCombo.setPreferredSize(new Dimension(80 ,20));
		if (config.panelSettingLocked() != null) {completedCombo.setSelectedItem(config.panelSettingLocked().getText());}
		else {completedCombo.setSelectedIndex(0);}
		completedCombo.addActionListener(e ->
		{
			config.panelSettingLocked(
				Arrays.stream(Locked.values())
				.filter(n -> n.getText().equals(completedCombo.getSelectedItem()))
				.findFirst().orElse(Locked.ALL));
		});
		settingsPanel.add(completedLabel);
		settingsPanel.add(completedCombo);

		// -- Region --
		JLabel regionLabel = new JLabel("Region: ");
		regionCombo = new JComboBox<>();
		regionCombo.setToolTipText("Filters music based on the region they are unlocked in.");
		regionLabel.setLabelFor(regionCombo);
		regionLabel.setToolTipText("Filters music based on the region they are unlocked in.");
		for (Region regionName : Region.values()) {regionCombo.addItem(regionName.getName());}
		regionCombo.setPreferredSize(new Dimension(80 ,20));
		if (config.panelSettingRegion() != null) {regionCombo.setSelectedItem(config.panelSettingRegion().getName());}
		else {regionCombo.setSelectedIndex(0);}
		regionCombo.addActionListener(e ->
		{
			config.panelSettingRegion(
				Arrays.stream(Region.values())
					.filter(n -> n.getName().equals(regionCombo.getSelectedItem()))
					.findFirst().orElse(Region.ALL));
		});
		settingsPanel.add(regionLabel);
		settingsPanel.add(regionCombo);

		// -- Unlocked During Quest
		JLabel unlockedDuringQuestLabel = new JLabel("Include Quest Unlocks: ");
		unlockedDuringQuestLabel.setToolTipText("Filters music tracks unlocked during quests.");
		unlockedDuringQuestCombo = new JComboBox<>();
		unlockedDuringQuestCombo.setToolTipText("Filters music tracks unlocked during quests.");
		for (Quest quest : Quest.values()) {unlockedDuringQuestCombo.addItem(quest.getText());}
		unlockedDuringQuestLabel.setLabelFor(unlockedDuringQuestCombo);
		unlockedDuringQuestLabel.setPreferredSize(new Dimension(80 ,20));
		if (config.panelSettingQuest() != null) {unlockedDuringQuestCombo.setSelectedItem(config.panelSettingQuest().getText());}
		unlockedDuringQuestCombo.addActionListener(e ->
		{
			config.panelSettingQuest(
				Arrays.stream(Quest.values())
					.filter(n -> n.getText().equals(unlockedDuringQuestCombo.getSelectedItem()))
					.findFirst().orElse(Quest.ALL));
		});
		settingsPanel.add(unlockedDuringQuestLabel);
		settingsPanel.add(unlockedDuringQuestCombo);

		// -- Included Optional
		JLabel includeOptionalLabel = new JLabel("Include Optional Unlocks: ");
		includeOptionalLabel.setToolTipText("Filters music tracks that are not required for the basic music cape unlock.");
		includeOptionalCombo = new JComboBox<>();
		for (Optional optional : Optional.values()) {includeOptionalCombo.addItem(optional.getText());}
		includeOptionalCombo.setToolTipText("Filters music tracks that are not required for the basic music cape unlock.");
		includeOptionalLabel.setLabelFor(includeOptionalCombo);
		includeOptionalLabel.setPreferredSize(new Dimension(80 ,20));
		if (config.panelSettingOptional() != null) {includeOptionalCombo.setSelectedItem(config.panelSettingOptional().getText());}
		includeOptionalCombo.addActionListener(e ->
		{
			config.panelSettingOptional(
				Arrays.stream(Optional.values())
					.filter(n -> n.getText().equals(includeOptionalCombo.getSelectedItem()))
					.findFirst().orElse(Optional.ALL));
		});
		settingsPanel.add(includeOptionalLabel);
		settingsPanel.add(includeOptionalCombo);

		// -- Order by --
		JLabel orderLabel = new JLabel("Order by: ");
		orderLabel.setToolTipText("The order the tracks are shown in.");
		orderCombo = new JComboBox<>();
		orderCombo.setToolTipText("The order the tracks are shown in.");
		orderLabel.setLabelFor(orderCombo);
		for (OrderBy order : OrderBy.values()) {orderCombo.addItem(order.getText());}
		orderCombo.setPreferredSize(new Dimension(80 ,20));
		if (config.panelSettingOrderBy() != null) {orderCombo.setSelectedItem(config.panelSettingOrderBy().getText());}
		else {orderCombo.setSelectedIndex(0);}
		orderCombo.addActionListener(e ->
		{
			config.panelSettingOrderBy(
				Arrays.stream(OrderBy.values())
					.filter(n -> n.getText().equals(orderCombo.getSelectedItem()))
					.findFirst().orElse(OrderBy.AZ));
		});
		settingsPanel.add(orderLabel);
		settingsPanel.add(orderCombo);

		add(searchBar);
		add(settingsPanel);
		searchBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		settingsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
	}

	public void buildMusicPanel()
	{
		//name region required
		musicPanel = new JPanel();
		musicPanel.setLayout(new BoxLayout(musicPanel, BoxLayout.Y_AXIS));
		musicPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

		musicHeaderPanel = new JPanel();
		musicHeaderPanel.setLayout(new GridLayout(0, 4, 5, 5));
		JLabel songNameLabelHeader = new JLabel("Name");
		songNameLabelHeader.setToolTipText("The name of the music track.");
		songNameLabelHeader.setFont(FontManager.getRunescapeBoldFont());
		JLabel songRegionLabelHeader = new JLabel("Region");
		songRegionLabelHeader.setToolTipText("The region the music track is unlocked in or where the associated quest starts.");
		songRegionLabelHeader.setFont(FontManager.getRunescapeBoldFont());
		JLabel songIsRequiredLabelHeader = new JLabel("Required?");
		songIsRequiredLabelHeader.setToolTipText("Seasonal tracks are not required for the untrimmed cape.");
		songIsRequiredLabelHeader.setFont(FontManager.getRunescapeBoldFont());
		JLabel blankSpace = new JLabel("");
		musicHeaderPanel.add(songNameLabelHeader);
		musicHeaderPanel.add(songRegionLabelHeader);
		musicHeaderPanel.add(songIsRequiredLabelHeader);
		musicHeaderPanel.add(blankSpace);

		add(musicHeaderPanel);
		add(musicPanel);
	}

	public List<MusicCapeHelperPanelMusicRow> createMusicRows()
	{
		List<MusicCapeHelperPanelMusicRow> musicListRow = new ArrayList<>();
		plugin.filterMusicList().forEach((key, value) -> musicListRow.add(new MusicCapeHelperPanelMusicRow(key, value, plugin)));
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
						musicPanel.add(new MusicCapeHelperMusicRowHeader(config, row.getMusic()));
					}
					else if (config.panelSettingOrderBy().equals(OrderBy.REGION)
						&& !musicRows.get(musicRows.indexOf(row)-1).getMusic().getRegion().equals(row.getMusic().getRegion()))
					{
						musicPanel.add(new MusicCapeHelperMusicRowHeader(config, row.getMusic()));
					}
					else if ((config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
					&& !musicRows.get(musicRows.indexOf(row)-1).getMusic().isRequired() == row.getMusic().isRequired())
					{
						musicPanel.add(new MusicCapeHelperMusicRowHeader(config, row.getMusic()));
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
				String label = ((MusicCapeHelperMusicRowHeader) r).getRowLabel().getText();
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
					if (label.equals("Required tracks: ") && Arrays.stream(musicPanel.getComponents())
						.filter(s -> s instanceof MusicCapeHelperPanelMusicRow)
						.filter(s -> ((MusicCapeHelperPanelMusicRow) s).getMusic().isRequired())
						.anyMatch(s -> !s.isEnabled()))
					{
						//if there is one or more matches, then set the header to false
						((MusicCapeHelperMusicRowHeader) r).setHeader(false);
					}
					else if (label.equals("Optional tracks: ") && Arrays.stream(musicPanel.getComponents())
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

	public void updateAllMusicPanelRows()
	{
		SwingUtilities.invokeLater(() ->
			{
				musicPanel.removeAll();
				musicRows = createMusicRows();
				addMusicRows();
				checkAndUpdateAllMusicRowHeader();
				musicPanel.revalidate();
				musicPanel.repaint();
			});
	}
}
