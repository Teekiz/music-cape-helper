package com.musiccapehelper.ui.rows;

import com.musiccapehelper.enums.Music;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MusicCapeHelperMusicEventRow extends JPanel
{
	public MusicCapeHelperMusicEventRow(Music music, Font font)
	{
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5, 0, 5, 0));
		setOpaque(false);

		if (!music.isRequired())
		{
			String eventLabelText = "Unlocked during: ";
			JLabel eventLabel = new JLabel(eventLabelText);
			eventLabel.setFont(font);

			String eventNameLabelText = music.getEventType().getEventName();
			JLabel eventNameLabel = new JLabel(eventNameLabelText);
			eventNameLabel.setFont(font);

			add(eventLabel, BorderLayout.PAGE_START);
			add(eventNameLabel, BorderLayout.LINE_START);
		}

	}
}
