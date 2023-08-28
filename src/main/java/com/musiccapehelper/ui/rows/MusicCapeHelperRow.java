package com.musiccapehelper.ui.rows;

import com.google.inject.Injector;
import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.data.Icon;
import com.musiccapehelper.enums.data.Music;
import com.musiccapehelper.ui.panels.MusicCapeHelperPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

public abstract class MusicCapeHelperRow extends JPanel implements MouseListener
{
	@Getter
	protected Music music;
	@Getter @Setter
	protected boolean enabled;
	@Getter @Setter
	protected boolean expanded;

	protected MusicCapeHelperPlugin plugin;

	protected MusicCapeHelperConfig config;
	protected MusicCapeHelperPanel panel;

	//content
	protected JLabel rowTitle = new JLabel();
	protected JLabel rowPinIcon = new JLabel();
	protected GridBagConstraints gbc = new GridBagConstraints();
	protected MusicCapeHelperPopupMenu popupMenu;

	public MusicCapeHelperRow(Music music, MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config)
	{
		this.music = music;
		this.plugin = plugin;
		this.config = config;
		this.expanded = false;

		//popupMenu = new MusicCapeHelperPopupMenu(this, plugin);
		popupMenu = new MusicCapeHelperPopupMenu(this, plugin.getInjector().getInstance(MusicCapeHelperPlugin.class));
		setComponentPopupMenu(popupMenu);

		setLayout(new GridBagLayout());
		setBorder(new LineBorder(ColorScheme.SCROLL_TRACK_COLOR));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		setRowTitle();
		setEnabled();
		setRowPinIcon();

		//row title (song name)
		gbc.insets = new Insets(4, 5, 0, 5);
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = 4;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		add(rowTitle, gbc);

		//row icon (pin)
		gbc.gridwidth = 1;
		gbc.gridx = 4;
		gbc.weightx = 0.5;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		add(rowPinIcon, gbc);

		addMouseListener(this);
		rowPinIcon.addMouseListener(this);
		setComponentPopupMenu(popupMenu);
	}

	/*
	 	this is used for dependency injection for the header class,
	 	setEnabled() must be called again in order to work correctly as the first pass will have panel set to null
	 */
	public MusicCapeHelperRow(Music music, MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config, MusicCapeHelperPanel panel)
	{
		this(music, plugin, config);
		this.panel = panel;
		setEnabled();
	}

	public void setRowTitle()
	{
		rowTitle.setText(music.getSongName());
		rowTitle.setHorizontalAlignment(JLabel.LEFT);
		rowTitle.setFont(FontManager.getRunescapeFont());
		if (expanded)
		{
			rowTitle.setIcon(Icon.UP_ICON.getIcon());

		}
		else
		{
			rowTitle.setIcon(Icon.DOWN_ICON.getIcon());
		}
		rowTitle.setHorizontalTextPosition(JLabel.RIGHT);
		rowTitle.setVerticalTextPosition(JLabel.CENTER);
	}

	public void setRowPinIcon()
	{
		enabled = plugin.getMapPoints().stream().anyMatch(m -> m.getMusic().equals(this.getMusic()));

		if (enabled)
		{
			rowPinIcon.setIcon(Icon.REMOVE_ICON.getIcon());
		}
		else
		{
			rowPinIcon.setIcon(Icon.ADD_ICON.getIcon());
		}
	}

	public abstract void setEnabled();
	public abstract void updateRow();
	@Override
	public abstract void mousePressed(MouseEvent e);
	@Override
	public void mouseClicked(MouseEvent e) {setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		setBackground(ColorScheme.DARK_GRAY_COLOR);
	}

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
}
