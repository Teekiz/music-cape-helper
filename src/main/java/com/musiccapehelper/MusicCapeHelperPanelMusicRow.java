package com.musiccapehelper;

import com.musiccapehelper.enums.Music;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

public class MusicCapeHelperPanelMusicRow extends JPanel
{
	@Getter
	private Music music;
	@Getter
	private boolean completed;
	@Getter @Setter
	private boolean enabled;
	private Color labelColour;
	private MusicCapeHelperPlugin plugin;
	private JLabel songNameLabel;
	private JLabel songRegionLabel;
	private JLabel songIsRequiredLabel;
	private JLabel enabledDisabled;

	public MusicCapeHelperPanelMusicRow(Music song, boolean completed, MusicCapeHelperPlugin plugin)
	{
		this.music = song;
		this.completed = completed;
		this.plugin = plugin;
		enabled = false;

		setLayout(new GridLayout(0, 4, 5, 5));
		setBorder(new LineBorder(ColorScheme.SCROLL_TRACK_COLOR));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		if (completed) {labelColour = Color.GREEN;}
		else {labelColour = Color.RED;}

		songNameLabel = new JLabel(song.getSongName());
		songNameLabel.setFont(FontManager.getRunescapeSmallFont());
		songNameLabel.setForeground(labelColour);
		songRegionLabel = new JLabel(song.getRegion().getName());
		songRegionLabel.setFont(FontManager.getRunescapeSmallFont());
		songIsRequiredLabel = new JLabel();
		if (song.isRequired()){songIsRequiredLabel.setText("Yes");}
		else {songIsRequiredLabel.setText("No");}
		songIsRequiredLabel.setFont(FontManager.getRunescapeSmallFont());

		enabledDisabled = new JLabel();
		enabledDisabled.setText("+");

		add(songNameLabel);
		add(songRegionLabel);
		add(songIsRequiredLabel);
		add(enabledDisabled);

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
				plugin.rowClicked(MusicCapeHelperPanelMusicRow.this);
			}
		});
	}

	public void updateMusicRow()
	{
		enabled = !enabled;
		if (enabled)
		{
			enabledDisabled.setText("+");
		}
		else
		{
			enabledDisabled.setText("-");
		}
	}
}
