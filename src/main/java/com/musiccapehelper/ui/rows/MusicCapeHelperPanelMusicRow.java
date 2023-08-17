package com.musiccapehelper.ui.rows;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.Music;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import net.runelite.client.ui.FontManager;

public class MusicCapeHelperPanelMusicRow extends MusicCapeHelperPanelRow
{
	public MusicCapeHelperPanelMusicRow(Music music, boolean completed, MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config)
	{
		super(music, completed, plugin, config);
	}

	@Override
	public void addRowContents()
	{
		JLabel songRegionLabel = new JLabel(music.getRegion().getName(), JLabel.LEFT);
		songRegionLabel.setFont(FontManager.getRunescapeSmallFont());
		songRegionLabel.setPreferredSize(new Dimension(50, 10));
		songRegionLabel.setHorizontalAlignment(JLabel.LEFT);
		super.gbc.ipadx = 0;
		super.gbc.gridx = 0;
		super.gbc.gridy = 1;
		super.gbc.anchor = GridBagConstraints.SOUTHWEST;
		add(songRegionLabel, super.gbc);

		JLabel songIsRequiredLabel = new JLabel();
		if (music.isRequired()){
			songIsRequiredLabel.setText("Required");}
		else {
			songIsRequiredLabel.setText("Optional");}
		songIsRequiredLabel.setFont(FontManager.getRunescapeSmallFont());
		songIsRequiredLabel.setHorizontalAlignment(JLabel.LEFT);
		super.gbc.gridx = 2;
		super.gbc.anchor = GridBagConstraints.SOUTHWEST;
		add(songIsRequiredLabel, super.gbc);

		JLabel songIsQuestLabel = new JLabel();
		if (music.isQuest()){
			songIsQuestLabel.setText("Quest Unlock");}
		else {
			songIsQuestLabel.setText("");}
		songIsQuestLabel.setFont(FontManager.getRunescapeSmallFont());
		songIsQuestLabel.setPreferredSize(new Dimension(70, 10));
		songIsQuestLabel.setHorizontalAlignment(JLabel.LEFT);
		super.gbc.gridx = 4;
		super.gbc.anchor = GridBagConstraints.SOUTHEAST;
		add(songIsQuestLabel, super.gbc);
	}
}
