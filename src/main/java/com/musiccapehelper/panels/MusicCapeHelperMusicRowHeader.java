package com.musiccapehelper.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.Music;
import com.musiccapehelper.enums.OrderBy;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;

public class MusicCapeHelperMusicRowHeader extends JPanel
{
	@Getter
	boolean enabled;
	@Getter
	JLabel rowLabel;
	JLabel spaceLabel;
	JLabel addRemoveAllIcon;
	MusicCapeHelperConfig config;
	Music music;
	MusicCapeHelperPlugin plugin;

	public MusicCapeHelperMusicRowHeader(MusicCapeHelperConfig config, Music music, MusicCapeHelperPlugin plugin)
	{
		this.config = config;
		this.music = music;
		this.plugin = plugin;
		enabled = false;

		setLayout(new GridLayout(0, 3, 5, 5));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		rowLabel = new JLabel();
		spaceLabel = new JLabel("");
		addRemoveAllIcon = new JLabel();

		addRemoveAllIcon.setToolTipText("Use this button to remove all map markers");
		//todo add action listener

		if (config.panelSettingOrderBy().equals(OrderBy.REGION))
		{
			rowLabel.setText(music.getRegion().getName());
		}
		else if (config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
		{
			if (music.isRequired())
			{
				rowLabel.setText("Required tracks: ");
			}
			else
			{
				rowLabel.setText("Optional tracks: ");
			}
		}

		updateHeader();

		add(rowLabel);
		add(spaceLabel);
		add(addRemoveAllIcon);

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
				enabled = !enabled;
				plugin.rowHeaderClicked(MusicCapeHelperMusicRowHeader.this);
			}
		});
	}

	public void setHeader(Boolean isAllOnMap)
	{
		enabled = isAllOnMap;
		updateHeader();
	}

	public void updateHeader()
	{
		if (enabled) addRemoveAllIcon.setText("-");
		else addRemoveAllIcon.setText("+");
	}
}
