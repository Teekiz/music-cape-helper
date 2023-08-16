package com.musiccapehelper.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.FontManager;

public class MusicCapeHelperMapPanel extends JPanel
{
	final MusicCapeHelperPlugin plugin;
	final MusicCapeHelperConfig config;
	final JPanel musicHeaderWrapper;
	public MusicCapeHelperMapPanel(MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config)
	{
		this.plugin = plugin;
		this.config = config;

		//name region required
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 0, 10, 0));

		musicHeaderWrapper = new JPanel();
		musicHeaderWrapper.setBorder(new EmptyBorder(5, 0, 5, 0));
		JLabel songNameLabelHeader = new JLabel("Map List");
		songNameLabelHeader.setToolTipText("A list of all markers currently pinned to the map.");
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
