package com.musiccapehelper.ui.rows;

import com.musiccapehelper.enums.Music;
import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

public class MusicCapeHelperMusicQuestRow extends JPanel
{
	private final Music music;

	public MusicCapeHelperMusicQuestRow(Music music)
	{
		this.music = music;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5, 0, 5, 0));
		setOpaque(false);

		if (music.isQuest())
		{
			String questLabelText = "Unlocked during: ";
			JLabel questLabel = new JLabel(questLabelText);
			questLabel.setFont(FontManager.getRunescapeSmallFont());

			String questNameLabelText = music.getQuest().getName();
			JLabel questNameLabel = new JLabel(questNameLabelText);
			questNameLabel.setFont(FontManager.getRunescapeSmallFont());

			add(questLabel, BorderLayout.PAGE_START);
			add(questNameLabel, BorderLayout.LINE_START);
		}

	}
}
