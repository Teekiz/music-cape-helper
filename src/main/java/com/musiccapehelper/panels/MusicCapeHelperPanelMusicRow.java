package com.musiccapehelper.panels;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.Music;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
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
	private MusicCapeHelperConfig config;
	private JLabel songNameLabel;
	private JLabel songRegionLabel;
	private JLabel songIsRequiredLabel;
	private JLabel songIsQuestLabel;
	private JLabel enabledDisabled;
	private JPopupMenu popupMenu;
	private JMenuItem popupMenuText;

	public MusicCapeHelperPanelMusicRow(Music music, boolean completed, MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config)
	{
		this.music = music;
		this.completed = completed;
		this.plugin = plugin;
		this.config = config;
		enabled = false;

		setLayout(new GridBagLayout());
		setBorder(new LineBorder(ColorScheme.SCROLL_TRACK_COLOR));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 5, 2, 5);
		gbc.fill = GridBagConstraints.NONE;

		if (completed && plugin.isPlayerLoggedIn()) {labelColour = config.panelCompleteTextColour();}
		else if (!completed && plugin.isPlayerLoggedIn()){labelColour = config.panelIncompleteTextColour();}
		else {labelColour = config.panelDefaultTextColour();}

		songNameLabel = new JLabel(music.getSongName(), JLabel.LEFT);
		songNameLabel.setFont(FontManager.getRunescapeSmallFont());
		songNameLabel.setForeground(labelColour);
		songNameLabel.setHorizontalAlignment(JLabel.LEFT);

		songRegionLabel = new JLabel(music.getRegion().getName(), JLabel.LEFT);
		songRegionLabel.setFont(FontManager.getRunescapeSmallFont());
		songRegionLabel.setPreferredSize(new Dimension(50, 10));
		songRegionLabel.setHorizontalAlignment(JLabel.LEFT);

		songIsRequiredLabel = new JLabel();
		if (music.isRequired()){songIsRequiredLabel.setText("Required");}
		else {songIsRequiredLabel.setText("Optional");}
		songIsRequiredLabel.setFont(FontManager.getRunescapeSmallFont());
		songIsRequiredLabel.setHorizontalAlignment(JLabel.LEFT);

		songIsQuestLabel = new JLabel();
		if (music.isQuest()){songIsQuestLabel.setText("Quest Unlock");}
		else {songIsQuestLabel.setText("");}
		songIsQuestLabel.setFont(FontManager.getRunescapeSmallFont());
		songIsQuestLabel.setPreferredSize(new Dimension(70, 10));
		songIsQuestLabel.setHorizontalAlignment(JLabel.LEFT);

		enabledDisabled = new JLabel();
		enabled = plugin.getMapPoints().stream().anyMatch(m -> m.getMusic().equals(this.getMusic()));
		updateMusicRow();

		gbc.gridwidth = 4;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		add(songNameLabel, gbc);

		gbc.gridwidth = 1;
		gbc.gridx = 4;
		gbc.weightx = 0.5;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		add(enabledDisabled, gbc);

		gbc.ipadx = 0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		add(songRegionLabel, gbc);

		gbc.gridx = 2;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		add(songIsRequiredLabel, gbc);

		gbc.gridx = 4;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		add(songIsQuestLabel, gbc);

		setToolTipText(music.getDescription());


		enabledDisabled.addMouseListener(new MouseAdapter()
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
					setBackground(ColorScheme.DARK_GRAY_COLOR);
					plugin.rowClicked(MusicCapeHelperPanelMusicRow.this);
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

	public void setEnabledDisabled(Boolean isOnMap)
	{
		enabled = isOnMap;
		updateMusicRow();
	}

	public void updateMusicRow()
	{
		if (enabled)
		{
			enabledDisabled.setIcon(new ImageIcon(ImageUtil.loadImageResource(getClass(), "/removeicon.png")));
			enabledDisabled.setToolTipText("Click to unpin icon from map");

		}
		else
		{
			enabledDisabled.setIcon(new ImageIcon(ImageUtil.loadImageResource(getClass(), "/addicon.png")));
			enabledDisabled.setToolTipText("Click to pin icon to the map");

		}
	}

	public void popUpMenuActions()
	{
		popupMenu = new JPopupMenu();
		popupMenuText = new JMenuItem();
		popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));

		if (enabled) {popupMenuText.setText("Unpin map icon");}
		else {popupMenuText.setText("Pin map icon");}
		popupMenuText.setFont(FontManager.getRunescapeSmallFont());
		popupMenu.add(popupMenuText);

		setComponentPopupMenu(popupMenu);

		popupMenuText.addActionListener(a ->
		{
			plugin.rowClicked(MusicCapeHelperPanelMusicRow.this);
		});
	}
}
