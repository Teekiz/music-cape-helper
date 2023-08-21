package com.musiccapehelper.ui.rows;

import com.musiccapehelper.enums.Music;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

public class MusicCapeHelperMusicEventRow extends JPanel
{
	private final Music music;

	public MusicCapeHelperMusicEventRow(Music music)
	{
		this.music = music;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5, 0, 5, 0));
		setOpaque(false);

		if (!music.isRequired())
		{
			String eventLabelText = "Unlocked during: ";
			JLabel eventLabel = new JLabel(eventLabelText);
			eventLabel.setFont(FontManager.getRunescapeSmallFont());

			String eventNameLabelText = music.getEventType().getEventName();
			JLabel eventNameLabel = new JLabel(eventNameLabelText);
			eventNameLabel.setFont(FontManager.getRunescapeSmallFont());

			add(eventLabel, BorderLayout.PAGE_START);
			add(eventNameLabel, BorderLayout.LINE_START);
		}

	}
}
