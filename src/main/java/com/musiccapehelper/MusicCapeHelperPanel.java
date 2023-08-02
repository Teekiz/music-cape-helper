package com.musiccapehelper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.EnumSet;
import java.util.Enumeration;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;

public class MusicCapeHelperPanel extends PluginPanel
{
	private MusicCapeHelperPlugin plugin;
	//settings and search components
	private IconTextField searchBar;
	private JPanel settingsPanel;
	private JComboBox<String> completedCombo;
	private JComboBox<String> unlockedDuringQuestCombo;
	private JComboBox<String> regionCombo;
	private JComboBox<String> orderCombo;

	//music row components
	private JPanel musicPanel;
	private JPanel musicHeaderPanel;

	public MusicCapeHelperPanel(MusicCapeHelperPlugin plugin)
	{
		this.plugin = plugin;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		buildSearchAndSettings();
		buildMusicPanel();

		for (Music song : Music.values())
		{
			JPanel musicRow = new MusicCapeHelperMusicRow(song, true);
			musicPanel.add(musicRow);
		}
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
		settingsPanel.setLayout(new DynamicGridLayout(4, 2, 5, 5));
		settingsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
		settingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// -- Completed --
		String[] completedComboText = {"All", "Locked Only", "Unlocked Only"};
		JLabel completedLabel = new JLabel("Show: ");
		completedLabel.setToolTipText("Filters music tracks based on whether they have been discovered or not.");
		completedCombo = new JComboBox<>(completedComboText);
		completedCombo.setToolTipText("Filters music tracks based on whether they have been discovered or not.");
		completedLabel.setLabelFor(completedCombo);
		completedCombo.setPreferredSize(new Dimension(80 ,20));
		//todo - add listener
		settingsPanel.add(completedLabel);
		settingsPanel.add(completedCombo);

		// -- Unlocked During Quest
		String[] unlockedDuringQuestText = {"Yes", "No"};
		JLabel unlockedDuringQuestLabel = new JLabel("Include Quest Unlocks: ");
		unlockedDuringQuestLabel.setToolTipText("Filters music tracks unlocked during quests.");
		unlockedDuringQuestCombo = new JComboBox<>(unlockedDuringQuestText);
		unlockedDuringQuestCombo.setToolTipText("Filters music tracks unlocked during quests.");
		unlockedDuringQuestLabel.setLabelFor(unlockedDuringQuestCombo);
		unlockedDuringQuestLabel.setPreferredSize(new Dimension(80 ,20));
		//todo - add listener
		settingsPanel.add(unlockedDuringQuestLabel);
		settingsPanel.add(unlockedDuringQuestCombo);

		// -- Region --
		JLabel regionLabel = new JLabel("Region: ");
		regionCombo = new JComboBox<>();
		regionCombo.setToolTipText("Filters music based on the region they are unlocked in.");
		regionLabel.setLabelFor(regionCombo);
		regionLabel.setToolTipText("Filters music based on the region they are unlocked in.");
		regionCombo.addItem("All");
		for (Region regionName : Region.values()) {regionCombo.addItem(regionName.getName());}
		regionCombo.setPreferredSize(new Dimension(80 ,20));
		//todo - add listener
		settingsPanel.add(regionLabel);
		settingsPanel.add(regionCombo);

		// -- Order by --
		String[] orderText = {"A-Z", "Z-A", "Region", "Required First", "Optional First"};
		JLabel orderLabel = new JLabel("Order by: ");
		orderLabel.setToolTipText("The order the tracks are shown in.");
		orderCombo = new JComboBox<>(orderText);
		orderCombo.setToolTipText("The order the tracks are shown in.");
		orderLabel.setLabelFor(orderCombo);
		orderCombo.setPreferredSize(new Dimension(80 ,20));
		//todo - add listener
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

		add(musicPanel);
		musicPanel.add(musicHeaderPanel);
		musicPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
	}
}
