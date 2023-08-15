package com.musiccapehelper.panels;

import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.Music;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

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

	public MusicCapeHelperPanelMusicRow(Music music, boolean completed, MusicCapeHelperPlugin plugin)
	{
		this.music = music;
		this.completed = completed;
		this.plugin = plugin;
		enabled = false;

		setLayout(new GridLayout(0, 4, 5, 5));
		setBorder(new LineBorder(ColorScheme.SCROLL_TRACK_COLOR));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		if (completed) {labelColour = Color.GREEN;}
		else {labelColour = Color.RED;}

		songNameLabel = new JLabel(music.getSongName());
		songNameLabel.setFont(FontManager.getRunescapeSmallFont());
		songNameLabel.setForeground(labelColour);
		songRegionLabel = new JLabel(music.getRegion().getName());
		songRegionLabel.setFont(FontManager.getRunescapeSmallFont());
		songIsRequiredLabel = new JLabel();
		if (music.isRequired()){songIsRequiredLabel.setText("Yes");}
		else {songIsRequiredLabel.setText("No");}
		songIsRequiredLabel.setFont(FontManager.getRunescapeSmallFont());
		enabledDisabled = new JLabel();
		enabled = plugin.getMapPoints().stream().anyMatch(m -> m.getMusic().equals(this.getMusic()));
		updateMusicRow();

		add(songNameLabel);
		add(songRegionLabel);
		add(songIsRequiredLabel);
		add(enabledDisabled);

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
		if (enabled) enabledDisabled.setText("-");
		else enabledDisabled.setText("+");
	}
}
