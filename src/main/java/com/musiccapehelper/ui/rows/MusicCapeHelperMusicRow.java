package com.musiccapehelper.ui.rows;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.Music;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JTextArea;
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

		JLabel descriptionTextAreaLabel = new JLabel();
		descriptionTextAreaLabel.setText("Description:");
		descriptionTextAreaLabel.setFont(FontManager.getRunescapeSmallFont());
		descriptionTextAreaLabel.setHorizontalAlignment(JLabel.LEFT);
		gbc.gridy = 3;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		add(descriptionTextAreaLabel, gbc);

		JTextArea descriptionTextArea = new JTextArea();
		descriptionTextArea.setEnabled(false);
		descriptionTextArea.setLineWrap(true);
		descriptionTextArea.setWrapStyleWord(true);
		descriptionTextArea.setEditable(false);
		descriptionTextArea.setFont(FontManager.getRunescapeSmallFont());
		descriptionTextArea.append(music.getDescription());

		gbc.gridy = 4;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(descriptionTextArea, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.SOUTH;

		//events
		if (!music.isQuest() && !music.isRequired())
		{
			add(new MusicCapeHelperMusicEventRow(music), gbc);
		}
		//quests
		else if (music.isQuest() && music.isRequired())
		{
			add(new MusicCapeHelperMusicQuestRow(music), gbc);
		}
		//normal
		else if (!music.isQuest() && music.isRequired() && !music.getItems().isEmpty())
		{
			add(new MusicCapeHelperMusicItemRow(music, itemManager, clientThread), gbc);
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
		repaint();
	}
}
