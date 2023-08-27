package com.musiccapehelper.ui.rows;

import com.musiccapehelper.enums.Music;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MusicCapeHelperMusicQuestRow extends JPanel
{
	public MusicCapeHelperMusicQuestRow(Music music, Font font)
	{
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5, 0, 5, 0));
		setOpaque(false);

		if (music.isQuest())
		{
			String questLabelText = "Unlocked during: ";
			JLabel questLabel = new JLabel(questLabelText);
			questLabel.setFont(font);

			String questNameLabelText = music.getQuest().getName();
			JLabel questNameLabel = new JLabel(questNameLabelText);
			questNameLabel.setFont(font);

			add(questLabel, BorderLayout.PAGE_START);
			add(questNameLabel, BorderLayout.LINE_START);
		}

	}
}
