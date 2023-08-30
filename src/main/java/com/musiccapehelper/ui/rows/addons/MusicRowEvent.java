package com.musiccapehelper.ui.rows.addons;

import com.musiccapehelper.enums.data.MusicData;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MusicRowEvent extends JPanel
{
	public MusicRowEvent(MusicData musicData, Font font)
	{
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5, 0, 5, 0));
		setOpaque(false);

		if (!musicData.isRequired())
		{
			String eventLabelText = "Unlocked during: ";
			JLabel eventLabel = new JLabel(eventLabelText);
			eventLabel.setFont(font);

			String eventNameLabelText = musicData.getEventType().getEventName();
			JLabel eventNameLabel = new JLabel(eventNameLabelText);
			eventNameLabel.setFont(font);

			add(eventLabel, BorderLayout.PAGE_START);
			add(eventNameLabel, BorderLayout.LINE_START);
		}

	}
}
