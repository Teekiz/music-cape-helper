package com.musiccapehelper.ui.rows;

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
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

public class MusicCapeHelperMusicHeaderRow extends JPanel
{
	@Getter
	private boolean enabled;
	@Getter
	private HeaderType headerType;
	private JLabel rowLabel;
	private JLabel addRemoveAllIcon;
	private MusicCapeHelperConfig config;
	private Music music;
	private MusicCapeHelperPlugin plugin;
	private String addRemoveToolTip;
	private JPopupMenu popupMenu;
	private JMenuItem popupMenuText;

	public MusicCapeHelperMusicHeaderRow(MusicCapeHelperConfig config, Music music, MusicCapeHelperPlugin plugin)
	{
		this.config = config;
		this.music = music;
		this.plugin = plugin;
		this.enabled = false;

		//todo - add collapse function

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

		addRemoveAllIcon.addMouseListener(new MouseAdapter()
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
				//left click
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					enabled = !enabled;
					setBackground(ColorScheme.DARK_GRAY_COLOR);
					plugin.rowHeaderClicked(MusicCapeHelperMusicHeaderRow.this);
				}
				//right click
				else if (e.getButton() == MouseEvent.BUTTON3)
				{
					setBackground(ColorScheme.DARK_GRAY_COLOR);
					popUpMenuActions();
				}
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

	public void popUpMenuActions()
	{
		popupMenu = new JPopupMenu();
		popupMenuText = new JMenuItem();
		popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));

		if (enabled) {popupMenuText.setText("Unpin map icons");}
		else {popupMenuText.setText("Pin map icons");}
		popupMenuText.setFont(FontManager.getRunescapeSmallFont());
		popupMenu.add(popupMenuText);

		setComponentPopupMenu(popupMenu);

		popupMenuText.addActionListener(a ->
		{
			enabled = !enabled;
			plugin.rowHeaderClicked(MusicCapeHelperMusicHeaderRow.this);
		});
	}
}
