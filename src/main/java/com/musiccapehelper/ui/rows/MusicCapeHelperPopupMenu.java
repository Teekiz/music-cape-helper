package com.musiccapehelper.ui.rows;

import com.musiccapehelper.MusicCapeHelperPlugin;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.FontManager;

public class MusicCapeHelperPopupMenu extends JPopupMenu implements ActionListener
{
	private final MusicCapeHelperPlugin plugin;
	private final MusicCapeHelperRow row;
	private final JMenuItem popupMenuIconText;
	private JMenuItem popupMenuBackgroundText;

	public MusicCapeHelperPopupMenu(MusicCapeHelperRow row,MusicCapeHelperPlugin plugin)
	{
		this.plugin = plugin;
		this.row = row;

		setBorder(new EmptyBorder(5, 5, 5, 5));

		popupMenuIconText = new JMenuItem();
		popupMenuIconText.setFont(FontManager.getRunescapeSmallFont());
		popupMenuIconText.addActionListener(this);
		add(popupMenuIconText);

		if (row instanceof MusicCapeHelperMusicRow)
		{
			popupMenuBackgroundText = new JMenuItem();
			popupMenuBackgroundText.setFont(FontManager.getRunescapeSmallFont());
			popupMenuBackgroundText.addActionListener(this);
			add(popupMenuBackgroundText);
		}

		setText();
	}

	public void setText()
	{
		if (row.isEnabled())
		{
			popupMenuIconText.setText("Unpin map icon");
		}
		else
		{
			popupMenuIconText.setText("Pin map icon");
		}

		if (popupMenuBackgroundText != null)
		{
			if (row.isExpanded())
			{
				popupMenuBackgroundText.setText("Hide music details");
			}
			else
			{
				popupMenuBackgroundText.setText("Show music details");
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(popupMenuIconText))
		{
			plugin.rowPinClicked(row);
			setVisible(false);
		}

		if (e.getSource().equals(popupMenuBackgroundText))
		{
			plugin.rowExpandClicked(row);
			setVisible(false);
		}
	}
}
