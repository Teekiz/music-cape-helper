package com.musiccapehelper.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
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
		musicHeaderWrapper.setLayout(new GridLayout(0, 4, 5, 5));
		JLabel songNameLabelHeader = new JLabel("Name");
		songNameLabelHeader.setToolTipText("The name of the music track.");
		songNameLabelHeader.setFont(FontManager.getRunescapeBoldFont());
		JLabel songRegionLabelHeader = new JLabel("Region");
		songRegionLabelHeader.setToolTipText("The region the music track is unlocked in or where the associated quest starts.");
		songRegionLabelHeader.setFont(FontManager.getRunescapeBoldFont());
		JLabel songIsRequiredLabelHeader = new JLabel("Required?");
		songIsRequiredLabelHeader.setToolTipText("Seasonal tracks are not required for the untrimmed cape.");
		songIsRequiredLabelHeader.setFont(FontManager.getRunescapeBoldFont());
		JLabel blankSpace = new JLabel("");
		musicHeaderWrapper.add(songNameLabelHeader);
		musicHeaderWrapper.add(songRegionLabelHeader);
		musicHeaderWrapper.add(songIsRequiredLabelHeader);
		musicHeaderWrapper.add(blankSpace);

		add(musicHeaderWrapper);
	}
}
