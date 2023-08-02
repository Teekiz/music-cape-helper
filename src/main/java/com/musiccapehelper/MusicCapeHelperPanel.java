package com.musiccapehelper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.EnumSet;
import java.util.Enumeration;
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
	private IconTextField searchBar;
	private JPanel settingsPanel;

	private JComboBox<String> completedCombo;
	private JComboBox<String> unlockedDuringQuestCombo;
	private JComboBox<String> regionCombo;
	private JComboBox<String> orderCombo;



	public MusicCapeHelperPanel()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 10, 0, 10));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		// -- Search Bar --
		searchBar = new IconTextField();
		searchBar.setIcon(IconTextField.Icon.SEARCH);
		searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		searchBar.setPreferredSize(new Dimension(100, 30));
		//todo - add listener
		add(searchBar);

		//Order of the panel Components - Search Bar, Settings tab, Music List
		// -- Settings Panel --
		settingsPanel = new JPanel();
		settingsPanel.setLayout(new DynamicGridLayout(4, 2, 5, 5));
		settingsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
		settingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// -- Completed --
		String[] completedComboText = {"All", "Locked Only", "Unlocked Only"};
		JLabel completedLabel = new JLabel("Show: ");
		completedCombo = new JComboBox<>(completedComboText);
		completedLabel.setLabelFor(completedCombo);
		completedCombo.setPreferredSize(new Dimension(80 ,20));
		//todo - add listener
		settingsPanel.add(completedLabel);
		settingsPanel.add(completedCombo);

		// -- Unlocked During Quest
		String[] unlockedDuringQuestText = {"All", "Yes", "No"};
		JLabel unlockedDuringQuestLabel = new JLabel("Quest Unlocks: ");
		unlockedDuringQuestCombo = new JComboBox<>(unlockedDuringQuestText);
		unlockedDuringQuestLabel.setLabelFor(unlockedDuringQuestCombo);
		unlockedDuringQuestLabel.setPreferredSize(new Dimension(80 ,20));
		//todo - add listener
		settingsPanel.add(unlockedDuringQuestLabel);
		settingsPanel.add(unlockedDuringQuestCombo);

		// -- Region --
		JLabel regionLabel = new JLabel("Region: ");
		regionCombo = new JComboBox<>();
		regionLabel.setLabelFor(regionCombo);
		regionCombo.addItem("All");
		for (Region regionName : Region.values()) {regionCombo.addItem(regionName.getName());}
		regionCombo.setPreferredSize(new Dimension(80 ,20));
		//todo - add listener
		settingsPanel.add(regionLabel);
		settingsPanel.add(regionCombo);

		// -- Order by --
		String[] orderText = {"A-Z", "Z-A"};
		JLabel orderLabel = new JLabel("Order by: ");
		orderCombo = new JComboBox<>(orderText);
		orderLabel.setLabelFor(orderCombo);
		orderCombo.setPreferredSize(new Dimension(80 ,20));
		//todo - add listener
		settingsPanel.add(orderLabel);
		settingsPanel.add(orderCombo);

		add(settingsPanel);


	}
}
