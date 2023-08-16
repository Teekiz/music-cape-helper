package com.musiccapehelper.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.HeaderType;
import com.musiccapehelper.enums.Music;
import com.musiccapehelper.enums.OrderBy;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

public class MusicCapeHelperMusicRowHeader extends JPanel
{
	@Getter
	boolean enabled;
	@Getter
	HeaderType headerType;
	JLabel rowLabel;
	JLabel addRemoveAllIcon;
	MusicCapeHelperConfig config;
	Music music;
	MusicCapeHelperPlugin plugin;
	String addRemoveToolTip;

	public MusicCapeHelperMusicRowHeader(MusicCapeHelperConfig config, Music music, MusicCapeHelperPlugin plugin)
	{
		this.config = config;
		this.music = music;
		this.plugin = plugin;
		enabled = false;
		addRemoveToolTip = "";

		setLayout(new BorderLayout(5,5));
		setBorder(new LineBorder(ColorScheme.SCROLL_TRACK_COLOR));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		rowLabel = new JLabel();
		addRemoveAllIcon = new JLabel();

		addRemoveAllIcon.setToolTipText("Use this button to remove all map markers");

		if (config.panelSettingOrderBy().equals(OrderBy.REGION))
		{
			headerType = Arrays.stream(HeaderType.values()).filter(m -> m.getRegion().equals(music.getRegion())).findFirst().orElse(HeaderType.ERROR);
			rowLabel.setText("Region: " + music.getRegion().getName());
			addRemoveToolTip = "tracks from the region " + music.getRegion().getName();
		}
		else if (config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
		{
			if (music.isRequired())
			{
				headerType = HeaderType.REQUIRED;
				rowLabel.setText("Required tracks: ");
				addRemoveToolTip = "all required tracks";
			}
			else
			{
				headerType = HeaderType.OPTIONAL;
				rowLabel.setText("Optional tracks: ");
				addRemoveToolTip = "all optional tracks";
			}
		}

		rowLabel.setFont(FontManager.getRunescapeBoldFont());
		updateHeader();
		add(rowLabel, BorderLayout.LINE_START);
		add(addRemoveAllIcon, BorderLayout.LINE_END);

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
				setBackground(ColorScheme.DARK_GRAY_COLOR);
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
		if (enabled)
		{
			addRemoveAllIcon.setIcon(new ImageIcon(ImageUtil.loadImageResource(getClass(), "/removeicon.png")));
			addRemoveAllIcon.setToolTipText("Click to unpin all " + addRemoveToolTip + " icons from map");
		}
		else
		{
			addRemoveAllIcon.setIcon(new ImageIcon(ImageUtil.loadImageResource(getClass(), "/addicon.png")));
			addRemoveAllIcon.setToolTipText("Click to pin all icons " + addRemoveToolTip + " to the map");
		}
	}
}
