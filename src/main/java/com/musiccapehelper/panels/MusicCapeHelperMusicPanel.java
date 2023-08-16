package com.musiccapehelper.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.FontManager;

public class MusicCapeHelperMusicPanel extends JPanel
{
	final MusicCapeHelperPlugin plugin;
	final MusicCapeHelperConfig config;
	final JPanel musicHeaderWrapper;

	public MusicCapeHelperMusicPanel(MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config)
	{
		this.plugin = plugin;
		this.config = config;

		//name region required
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 0, 10, 0));

		musicHeaderWrapper = new JPanel();
		musicHeaderWrapper.setBorder(new EmptyBorder(5, 0, 5, 0));
		JLabel songNameLabelHeader = new JLabel("Music Track List");
		songNameLabelHeader.setToolTipText("A list of Old School Runescapes' music tracks.");
		songNameLabelHeader.setFont(FontManager.getRunescapeBoldFont());
		musicHeaderWrapper.add(songNameLabelHeader);
		add(musicHeaderWrapper);
	}

	public void removeAllMusicRows()
	{
		removeAll();
		add(musicHeaderWrapper);
	}
}
