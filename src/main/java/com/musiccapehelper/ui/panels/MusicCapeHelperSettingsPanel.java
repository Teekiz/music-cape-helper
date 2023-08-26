package com.musiccapehelper.ui.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.settings.SettingsLocked;
import com.musiccapehelper.enums.settings.SettingsOptional;
import com.musiccapehelper.enums.settings.SettingsOrderBy;
import com.musiccapehelper.enums.settings.SettingsQuest;
import com.musiccapehelper.enums.settings.SettingsRegion;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.components.IconTextField;

public class MusicCapeHelperSettingsPanel extends JPanel
{
	private final MusicCapeHelperPlugin plugin;
	private final MusicCapeHelperConfig config;
	private final MusicCapeHelperPanel panel;
	private final IconTextField searchBar;
	private final JComboBox<String> completedCombo;
	private final JComboBox<String> regionCombo;
	private final JComboBox<String> unlockedDuringQuestCombo;
	private final JComboBox<String> includeOptionalCombo;
	private final JComboBox<String> orderCombo;

	public MusicCapeHelperSettingsPanel(MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config, MusicCapeHelperPanel panel)
	{
		this.plugin = plugin;
		this.config = config;
		this.panel = panel;

		//Order of the panel Components - Search Bar, Settings tab, Music List
		// -- Settings Panel --
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(new EmptyBorder(10, 0, 10, 0));
		setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel settingsWrapper = new JPanel();
		settingsWrapper.setLayout(new DynamicGridLayout(5, 2, 5, 5));
		settingsWrapper.setBorder(new EmptyBorder(10, 0, 10, 0));
		settingsWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

		int comboWidth = 75; //50
		int comboHeight = 20; //20

		//todo - add tab completer
		// -- Search Bar --
		searchBar = new IconTextField();
		searchBar.setIcon(IconTextField.Icon.SEARCH);
		searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		searchBar.setPreferredSize(new Dimension(100, 30));
		searchBar.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				panel.createAndRefreshRows(searchBar.getText());
			}
		});
		searchBar.addClearListener(plugin::updateMusicList);
		searchBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(searchBar);

		// -- Completed --
		JLabel completedLabel = new JLabel("Show:");
		completedLabel.setToolTipText("Filters music tracks based on whether they have been discovered or not.");
		completedCombo = new JComboBox<>();
		completedCombo.setToolTipText("Filters music tracks based on whether they have been discovered or not.");
		completedLabel.setLabelFor(completedCombo);
		for (SettingsLocked settingsLocked : SettingsLocked.values()) {completedCombo.addItem(settingsLocked.getText());}
		completedCombo.setPreferredSize(new Dimension(comboWidth ,comboHeight));
		if (config.panelSettingLocked() != null) {completedCombo.setSelectedItem(config.panelSettingLocked().getText());}
		else {completedCombo.setSelectedIndex(0);}
		completedCombo.addActionListener(e ->
		{
			config.panelSettingLocked(
				Arrays.stream(SettingsLocked.values())
					.filter(n -> n.getText().equals(completedCombo.getSelectedItem()))
					.findFirst().orElse(SettingsLocked.ALL));
		});
		settingsWrapper.add(completedLabel);
		settingsWrapper.add(completedCombo);

		// -- Region --
		JLabel regionLabel = new JLabel("Region:");
		regionCombo = new JComboBox<>();
		regionCombo.setToolTipText("Filters music based on the region they are unlocked in.");
		regionLabel.setLabelFor(regionCombo);
		regionLabel.setToolTipText("Filters music based on the region they are unlocked in.");
		for (SettingsRegion settingsRegionName : SettingsRegion.values()) {regionCombo.addItem(settingsRegionName.getName());}
		regionCombo.setPreferredSize(new Dimension(comboWidth ,comboHeight));
		if (config.panelSettingRegion() != null) {regionCombo.setSelectedItem(config.panelSettingRegion().getName());}
		else {regionCombo.setSelectedIndex(0);}
		regionCombo.addActionListener(e ->
		{
			config.panelSettingRegion(
				Arrays.stream(SettingsRegion.values())
					.filter(n -> n.getName().equals(regionCombo.getSelectedItem()))
					.findFirst().orElse(SettingsRegion.ALL));
		});
		settingsWrapper.add(regionLabel);
		settingsWrapper.add(regionCombo);

		// -- Unlocked During Quest
		JLabel unlockedDuringQuestLabel = new JLabel("Quests:");
		unlockedDuringQuestLabel.setToolTipText("Filters music tracks unlocked during quests.");
		unlockedDuringQuestCombo = new JComboBox<>();
		unlockedDuringQuestCombo.setToolTipText("Filters music tracks unlocked during quests.");
		for (SettingsQuest settingsQuest : SettingsQuest.values()) {unlockedDuringQuestCombo.addItem(settingsQuest.getText());}
		unlockedDuringQuestLabel.setLabelFor(unlockedDuringQuestCombo);
		unlockedDuringQuestLabel.setPreferredSize(new Dimension(comboWidth ,comboHeight));
		if (config.panelSettingQuest() != null) {unlockedDuringQuestCombo.setSelectedItem(config.panelSettingQuest().getText());}
		unlockedDuringQuestCombo.addActionListener(e ->
		{
			config.panelSettingQuest(
				Arrays.stream(SettingsQuest.values())
					.filter(n -> n.getText().equals(unlockedDuringQuestCombo.getSelectedItem()))
					.findFirst().orElse(SettingsQuest.ALL));
		});
		settingsWrapper.add(unlockedDuringQuestLabel);
		settingsWrapper.add(unlockedDuringQuestCombo);

		// -- Included Optional
		JLabel includeOptionalLabel = new JLabel("Optional:");
		includeOptionalLabel.setToolTipText("Filters music tracks that are not required for the basic music cape unlock.");
		includeOptionalCombo = new JComboBox<>();
		for (SettingsOptional settingsOptional : SettingsOptional.values()) {includeOptionalCombo.addItem(settingsOptional.getText());}
		includeOptionalCombo.setToolTipText("Filters music tracks that are not required for the basic music cape unlock.");
		includeOptionalLabel.setLabelFor(includeOptionalCombo);
		includeOptionalLabel.setPreferredSize(new Dimension(comboWidth ,comboHeight));
		if (config.panelSettingOptional() != null) {includeOptionalCombo.setSelectedItem(config.panelSettingOptional().getText());}
		includeOptionalCombo.addActionListener(e ->
		{
			config.panelSettingOptional(
				Arrays.stream(SettingsOptional.values())
					.filter(n -> n.getText().equals(includeOptionalCombo.getSelectedItem()))
					.findFirst().orElse(SettingsOptional.ALL));
		});
		settingsWrapper.add(includeOptionalLabel);
		settingsWrapper.add(includeOptionalCombo);

		// -- Order by --
		JLabel orderLabel = new JLabel("Order by: ");
		orderLabel.setToolTipText("The order the tracks are shown in.");
		orderCombo = new JComboBox<>();
		orderCombo.setToolTipText("The order the tracks are shown in.");
		orderLabel.setLabelFor(orderCombo);
		for (SettingsOrderBy order : SettingsOrderBy.values()) {orderCombo.addItem(order.getText());}
		orderCombo.setPreferredSize(new Dimension(comboWidth ,comboHeight));
		if (config.panelSettingOrderBy() != null) {orderCombo.setSelectedItem(config.panelSettingOrderBy().getText());}
		else {orderCombo.setSelectedIndex(0);}
		orderCombo.addActionListener(e ->
		{
			config.panelSettingOrderBy(
				Arrays.stream(SettingsOrderBy.values())
					.filter(n -> n.getText().equals(orderCombo.getSelectedItem()))
					.findFirst().orElse(SettingsOrderBy.AZ));
		});
		settingsWrapper.add(orderLabel);
		settingsWrapper.add(orderCombo);

		add(settingsWrapper);
	}
}
