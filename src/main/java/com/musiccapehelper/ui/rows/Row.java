package com.musiccapehelper.ui.rows;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicExpandedRows;
import com.musiccapehelper.MusicMapPoints;
import com.musiccapehelper.MusicPanelRows;
import com.musiccapehelper.enums.data.MusicData;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;

public abstract class Row extends JPanel implements MouseListener
{
	@Getter
	protected MusicData musicData;
	@Getter
	protected boolean enabled;
	@Getter
	protected boolean expanded;

	//dependencies
	protected MusicCapeHelperConfig config;
	protected MusicPanelRows musicPanelRows;
	protected MusicMapPoints musicMapPoints;
	protected MusicExpandedRows musicExpandedRows;

	//content
	protected JLabel rowTitle = new JLabel();
	protected JLabel rowPinIcon = new JLabel();
	protected GridBagConstraints gbc = new GridBagConstraints();
	protected PopupMenu popupMenu;

	public Row(MusicData musicData, MusicCapeHelperConfig config,
		MusicPanelRows musicPanelRows, MusicMapPoints musicMapPoints, MusicExpandedRows musicExpandedRows)
	{
		this.musicData = musicData;
		this.config = config;
		this.musicPanelRows = musicPanelRows;
		this.musicMapPoints = musicMapPoints;
		this.musicExpandedRows = musicExpandedRows;

		this.expanded = false;

		popupMenu = new PopupMenu(this, musicMapPoints, musicExpandedRows);
		setComponentPopupMenu(popupMenu);

		setLayout(new GridBagLayout());
		setBorder(new LineBorder(ColorScheme.SCROLL_TRACK_COLOR));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

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

	public abstract void setRowTitle();
	public abstract void updateRowValues();
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
