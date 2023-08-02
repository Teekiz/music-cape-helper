package com.musiccapehelper;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

public class MusicCapeHelperMusicRow extends JPanel
{
	private Music music;
	private boolean completed;

	public MusicCapeHelperMusicRow(Music song, boolean completed)
	{
		this.music = song;
		this.completed = completed;

		setLayout(new GridLayout(0, 3, 5, 5));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		JLabel songNameLabel = new JLabel(song.getSongName());
		songNameLabel.setFont(FontManager.getRunescapeSmallFont());
		JLabel songRegionLabel = new JLabel(song.getRegion().getName());
		songRegionLabel.setFont(FontManager.getRunescapeSmallFont());
		JLabel songIsRequiredLabel = new JLabel();
		if (song.isRequired()){songIsRequiredLabel.setText("Yes");}
		else {songIsRequiredLabel.setText("No");}
		songIsRequiredLabel.setFont(FontManager.getRunescapeSmallFont());

		add(songNameLabel);
		add(songRegionLabel);
		add(songIsRequiredLabel);

		setToolTipText(song.getDescription());

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				setBackground(ColorScheme.DARK_GRAY_COLOR);
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				//todo - what happens when mouse is clicked
				//check if client is logged in
				//right click, left click?
			}
		});
	}
}
