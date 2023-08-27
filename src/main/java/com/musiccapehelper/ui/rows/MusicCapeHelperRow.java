package com.musiccapehelper.ui.rows;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.Icon;
import com.musiccapehelper.enums.Music;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

public abstract class MusicCapeHelperRow extends JPanel implements ActionListener, MouseListener
{
	@Getter
	protected Music music;
	@Getter @Setter
	protected boolean enabled;
	@Getter @Setter
	protected boolean expanded;

	protected MusicCapeHelperPlugin plugin;

	protected MusicCapeHelperConfig config;

	//content
	protected JLabel rowTitle = new JLabel();
	protected JLabel rowPinIcon = new JLabel();
	protected GridBagConstraints gbc = new GridBagConstraints();

	//popup
	protected JPopupMenu popupMenu = new JPopupMenu();
	protected JMenuItem popupMenuIconText = new JMenuItem();
	protected JMenuItem popupMenuBackgroundText = new JMenuItem();

	public MusicCapeHelperRow(Music music, MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config)
	{
		this.music = music;
		this.plugin = plugin;
		this.config = config;
		this.expanded = false;

		setLayout(new GridBagLayout());
		setBorder(new LineBorder(ColorScheme.SCROLL_TRACK_COLOR));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		setRowTitle();
		setEnabled();
		setRowPinIcon();
		setPopup();

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

		for (MouseListener mouseListener : this.getMouseListeners()) { this.removeMouseListener(mouseListener); }
		for (MouseListener mouseListener : rowPinIcon.getMouseListeners()) { rowPinIcon.removeMouseListener(mouseListener); }
		for (ActionListener actionListener : popupMenuIconText.getActionListeners()) { popupMenuIconText.removeActionListener(actionListener); }

		addMouseListener(this);
		rowPinIcon.addMouseListener(this);
		popupMenuIconText.addActionListener(this);
		popupMenuBackgroundText.addActionListener(this);
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

	public void setEnabled()
	{
		enabled = plugin.getMapPoints().stream().anyMatch(m -> m.getMusic().equals(this.getMusic()));
	}

	public void setPopup()
	{
		//right click popup
		popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
		if (enabled)
		{
			popupMenuIconText.setText("Unpin map icon");
		}
		else
		{
			popupMenuIconText.setText("Pin map icon");
		}
		popupMenuIconText.setFont(FontManager.getRunescapeSmallFont());
		popupMenu.add(popupMenuIconText);

		if (this instanceof MusicCapeHelperMusicRow)
		{
			if (expanded)
			{
				popupMenuBackgroundText.setText("Hide music details");
			}
			else
			{
				popupMenuBackgroundText.setText("Show music details");
			}
			popupMenuBackgroundText.setFont(FontManager.getRunescapeSmallFont());
			popupMenu.add(popupMenuBackgroundText);
		}
		setComponentPopupMenu(popupMenu);
	}

	public void updateRow()
	{
		setEnabled();
		setRowPinIcon();
		revalidate();
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		//upin/pin icon
		if (e.getComponent().equals(rowPinIcon))
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				plugin.rowPinClicked(this);
			}
			else if (e.getButton() == MouseEvent.BUTTON3)
			{
				popupMenu.setVisible(true);
			}
		}

		//clicking the background
		else if (e.getComponent().equals(this))
		{
			if (e.getButton() == MouseEvent.BUTTON1 && this instanceof MusicCapeHelperMusicRow)
			{
				plugin.rowExpandClicked(this);

			}
			else if (e.getButton() == MouseEvent.BUTTON1 && this instanceof MusicCapeHelperHeader)
			{
				plugin.rowPinClicked(this);
			}
			else if (e.getButton() == MouseEvent.BUTTON3)
			{
				popupMenu.setVisible(true);
			}
		}
	}

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

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(popupMenuIconText))
		{
			plugin.rowPinClicked(this);
			popupMenu.setVisible(false);
		}

		if (e.getSource().equals(popupMenuBackgroundText))
		{
			plugin.rowExpandClicked(this);
			popupMenu.setVisible(false);
		}
	}
}
