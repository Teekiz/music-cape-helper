package com.musiccapehelper.ui.rows;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.Music;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import lombok.Getter;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;

public class MusicCapeHelperMusicRow extends MusicCapeHelperRow
{
	@Getter
	protected boolean completed;
	public MusicCapeHelperMusicRow(Music music, boolean completed, MusicCapeHelperPlugin plugin,
								   MusicCapeHelperConfig config, ItemManager itemManager, ClientThread clientThread)
	{
		super(music, plugin, config);
		this.completed = completed;
		setTextColour();

		JLabel songRegionLabel = new JLabel("Region: " + music.getSettingsRegion().getName(), JLabel.LEFT);
		songRegionLabel.setFont(FontManager.getRunescapeSmallFont());
		songRegionLabel.setHorizontalAlignment(JLabel.LEFT);
		gbc.ipadx = 0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		add(songRegionLabel, gbc);

		JLabel songIsRequiredLabel = new JLabel();
		if (music.isRequired())
		{
			songIsRequiredLabel.setText("Required");
		}
		else
		{
			songIsRequiredLabel.setText("Optional");
		}
		songIsRequiredLabel.setFont(FontManager.getRunescapeSmallFont());
		songIsRequiredLabel.setHorizontalAlignment(JLabel.LEFT);
		gbc.gridx = 2;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		add(songIsRequiredLabel, gbc);

		JLabel songIsQuestLabel = new JLabel();
		if (music.isQuest())
		{
			songIsQuestLabel.setText("Quest Unlock: Yes");
		}
		else
		{
			songIsQuestLabel.setText("Quest Unlock: No");
		}
		songIsQuestLabel.setFont(FontManager.getRunescapeSmallFont());
		songIsQuestLabel.setHorizontalAlignment(JLabel.LEFT);
		gbc.gridy = 2;
		gbc.gridx = 0;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		add(songIsQuestLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.SOUTH;

		if (!music.isRequired())
		{
			add(new MusicCapeHelperMusicEventRow(music), gbc);
		}
		else if (!music.isQuest())
		{
			add(new MusicCapeHelperMusicItemRow(music, itemManager, clientThread), gbc);
		}
		else
		{
			add(new MusicCapeHelperMusicQuestRow(music), gbc);
		}
	}

	public void setTextColour()
	{
		if (completed && plugin.isPlayerLoggedIn())
		{
			rowTitle.setForeground(config.panelCompleteTextColour());
		}
		else if (!completed && plugin.isPlayerLoggedIn())
		{
			rowTitle.setForeground(config.panelIncompleteTextColour());
		}
		else
		{
			rowTitle.setForeground(config.panelDefaultTextColour());
		}
	}

	@Override
	public void updateRow()
	{
		setEnabled();
		setTextColour();
		setRowPinIcon();
		revalidate();
	}
}
