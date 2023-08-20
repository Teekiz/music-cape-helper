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

		JLabel songRegionLabel = new JLabel(music.getSettingsRegion().getName(), JLabel.LEFT);
		songRegionLabel.setFont(FontManager.getRunescapeSmallFont());
		songRegionLabel.setPreferredSize(new Dimension(50, 10));
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
			songIsQuestLabel.setText("Quest Unlock");
		}
		else
		{
			songIsQuestLabel.setText("");
		}
		songIsQuestLabel.setFont(FontManager.getRunescapeSmallFont());
		songIsQuestLabel.setPreferredSize(new Dimension(70, 10));
		songIsQuestLabel.setHorizontalAlignment(JLabel.LEFT);
		gbc.gridx = 4;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		add(songIsQuestLabel, gbc);

		if (!music.isQuest())
		{
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.SOUTH;
			add(new MusicCapeHelperMusicNonQuestRow(music, itemManager, clientThread), gbc);
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
