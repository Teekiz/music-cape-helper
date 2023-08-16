package com.musiccapehelper.panels;

import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.Music;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

public class MusicCapeHelperPanelMusicRow extends JPanel
{
	@Getter
	private Music music;
	@Getter
	private boolean completed;
	@Getter
	private boolean enabled;
	private Color labelColour;
	private MusicCapeHelperPlugin plugin;
	private JLabel songNameLabel;
	private JLabel songRegionLabel;
	private JLabel songIsRequiredLabel;
	private JLabel enabledDisabled;
	//icons
	final ImageIcon addIcon;
	final ImageIcon removeIcon;

	public MusicCapeHelperPanelMusicRow(Music music, boolean completed, MusicCapeHelperPlugin plugin)
	{
		this.music = music;
		this.completed = completed;
		this.plugin = plugin;
		enabled = false;

		addIcon = new ImageIcon("addicon.png");
		removeIcon = new ImageIcon("removeicon.png");

		//setLayout(new GridLayout(0, 4, 5, 5));
		setLayout(new GridBagLayout());
		setBorder(new LineBorder(ColorScheme.SCROLL_TRACK_COLOR));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;

		if (completed) {labelColour = Color.GREEN;}
		else {labelColour = Color.RED;}

		songNameLabel = new JLabel(music.getSongName(), JLabel.LEFT);
		songNameLabel.setFont(FontManager.getRunescapeSmallFont());
		songNameLabel.setForeground(labelColour);
		songRegionLabel = new JLabel(music.getRegion().getName(), JLabel.LEFT);
		songRegionLabel.setFont(FontManager.getRunescapeSmallFont());
		songIsRequiredLabel = new JLabel();
		if (music.isRequired()){songIsRequiredLabel.setText("Yes");}
		else {songIsRequiredLabel.setText("No");}
		songIsRequiredLabel.setFont(FontManager.getRunescapeSmallFont());
		enabledDisabled = new JLabel();
		enabled = plugin.getMapPoints().stream().anyMatch(m -> m.getMusic().equals(this.getMusic()));
		updateMusicRow();

		gbc.weightx = 0.5;
		gbc.gridwidth = 1;
		gbc.gridx = 0;

		gbc.gridy = 0;
		add(songNameLabel, gbc);

		gbc.ipadx = 0;
		gbc.gridx = 1;
		add(songRegionLabel, gbc);

		gbc.gridx = 2;
		add(songIsRequiredLabel, gbc);

		gbc.gridx = 3;
		add(enabledDisabled, gbc);

		setToolTipText(music.getDescription());

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
				plugin.rowClicked(MusicCapeHelperPanelMusicRow.this);
			}
		});
	}

	public void setEnabledDisabled(Boolean isOnMap)
	{
		enabled = isOnMap;
		updateMusicRow();
	}

	public void updateMusicRow()
	{
		if (enabled) enabledDisabled.setIcon(removeIcon);
		else enabledDisabled.setIcon(addIcon);
	}
}
