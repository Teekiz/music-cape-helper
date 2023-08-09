package com.musiccapehelper;

import com.musiccapehelper.enums.Locked;
import com.musiccapehelper.enums.OrderBy;
import com.musiccapehelper.enums.Region;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
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

		musicRows = createMusicRows();
		addMusicRows();
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
		regionCombo.addItem("All");
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
		String[] unlockedDuringQuestText = {"Yes", "No"};
		JLabel unlockedDuringQuestLabel = new JLabel("Include Quest Unlocks: ");
		unlockedDuringQuestLabel.setToolTipText("Filters music tracks unlocked during quests.");
		unlockedDuringQuestCombo = new JComboBox<>(unlockedDuringQuestText);
		unlockedDuringQuestCombo.setToolTipText("Filters music tracks unlocked during quests.");
		unlockedDuringQuestLabel.setLabelFor(unlockedDuringQuestCombo);
		unlockedDuringQuestLabel.setPreferredSize(new Dimension(80 ,20));
		if (config.panelSettingQuest()) {unlockedDuringQuestCombo.setSelectedIndex(0);}
		else {unlockedDuringQuestCombo.setSelectedIndex(1);}
		unlockedDuringQuestCombo.addActionListener(e ->
		{
			if (unlockedDuringQuestCombo.getSelectedIndex() == 0) {config.panelSettingQuest(true);}
			else {config.panelSettingQuest(false);}
		});
		settingsPanel.add(unlockedDuringQuestLabel);
		settingsPanel.add(unlockedDuringQuestCombo);

		// -- Included Optional
		String[] includeOptionalText = {"Yes", "No"};
		JLabel includeOptionalLabel = new JLabel("Include Optional Unlocks: ");
		includeOptionalLabel.setToolTipText("Filters music tracks that are not required for the basic music cape unlock.");
		includeOptionalCombo = new JComboBox<>(includeOptionalText);
		includeOptionalCombo.setToolTipText("Filters music tracks that are not required for the basic music cape unlock.");
		includeOptionalLabel.setLabelFor(includeOptionalCombo);
		includeOptionalLabel.setPreferredSize(new Dimension(80 ,20));
		if (config.panelSettingOptional()) {includeOptionalCombo.setSelectedIndex(0);}
		else {includeOptionalCombo.setSelectedIndex(1);}
		includeOptionalCombo.addActionListener(e ->
		{
			if (includeOptionalCombo.getSelectedIndex() == 0) {config.panelSettingOptional(true);}
			else {config.panelSettingOptional(false);}
		});
		settingsPanel.add(unlockedDuringQuestLabel);
		settingsPanel.add(unlockedDuringQuestCombo);

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
		musicHeaderPanel.setLayout(new GridLayout(0, 3, 5, 5));
		JLabel songNameLabelHeader = new JLabel("Name");
		songNameLabelHeader.setToolTipText("The name of the music track.");
		songNameLabelHeader.setFont(FontManager.getRunescapeBoldFont());
		JLabel songRegionLabelHeader = new JLabel("Region");
		songRegionLabelHeader.setToolTipText("The region the music track is unlocked in or where the associated quest starts.");
		songRegionLabelHeader.setFont(FontManager.getRunescapeBoldFont());
		JLabel songIsRequiredLabelHeader = new JLabel("Required?");
		songIsRequiredLabelHeader.setToolTipText("Seasonal tracks are not required for the untrimmed cape.");
		songIsRequiredLabelHeader.setFont(FontManager.getRunescapeBoldFont());
		musicHeaderPanel.add(songNameLabelHeader);
		musicHeaderPanel.add(songRegionLabelHeader);
		musicHeaderPanel.add(songIsRequiredLabelHeader);

		add(musicHeaderPanel);
		add(musicPanel);
	}

	public List<MusicCapeHelperPanelMusicRow> createMusicRows()
	{
		//todo add filters base on what the current settings are
		List<MusicCapeHelperPanelMusicRow> musicListRow = new ArrayList<>();
		plugin.getMusicList().forEach((key, value) -> musicListRow.add(new MusicCapeHelperPanelMusicRow(key, value, plugin)));
		return musicListRow;
	}

	public void addMusicRows()
	{
		if (musicPanel != null)
		{
			for (MusicCapeHelperPanelMusicRow row : musicRows)
			{
				musicPanel.add(row);
			}
		}
	}

	public void updateAllMusicPanelRows()
	{
		SwingUtilities.invokeLater(() ->
			{
				musicPanel.removeAll();
				musicRows = createMusicRows();
				addMusicRows();
				musicPanel.revalidate();
				musicPanel.repaint();
			});
	}
}
