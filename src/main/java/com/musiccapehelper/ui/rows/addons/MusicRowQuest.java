package com.musiccapehelper.ui.rows.addons;

import com.musiccapehelper.enums.data.MusicData;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MusicRowQuest extends JPanel
{
	public MusicRowQuest(MusicData musicData, Font font)
	{
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5, 0, 5, 0));
		setOpaque(false);

		if (musicData.isQuest())
		{
			String questLabelText = "Unlocked during: ";
			JLabel questLabel = new JLabel(questLabelText);
			questLabel.setFont(font);

			String questNameLabelText = musicData.getQuest().getName();
			JLabel questNameLabel = new JLabel(questNameLabelText);
			questNameLabel.setFont(font);

			add(questLabel, BorderLayout.PAGE_START);
			add(questNameLabel, BorderLayout.LINE_START);
		}

	}
}
